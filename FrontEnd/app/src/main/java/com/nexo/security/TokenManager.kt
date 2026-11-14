package com.nexo.security

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// DataStore instance bound to the Android Context
private val Context.dataStore by preferencesDataStore(name = "nexo_auth_tokens")

class TokenManager(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
    }

    // In-memory cache to prevent blocking network threads with DataStore I/O
    @Volatile
    var cachedAccessToken: String? = null
        private set

    @Volatile
    var cachedRefreshToken: String? = null
        private set

    @Volatile
    var cachedUserId: Long? = null
        private set

    @Volatile
    var cachedUsername: String? = null
        private set

    // Flow for observing the Access Token in real time
    val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    // Flow for observing the Refresh token in real time
    val refreshToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    // Flow for observing the User ID in real time
    val userIdFlow: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    // Flow for observing the Username in real time
    val usernameFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USERNAME_KEY]
    }

    init {
        // Keep in-memory cache synced with DataStore updates
        CoroutineScope(Dispatchers.IO).launch {
            accessToken.collect { cachedAccessToken = it }
        }
        CoroutineScope(Dispatchers.IO).launch {
            refreshToken.collect { cachedRefreshToken = it }
        }
        CoroutineScope(Dispatchers.IO).launch {
            userIdFlow.collect { cachedUserId = it }
        }
        CoroutineScope(Dispatchers.IO).launch {
            usernameFlow.collect { cachedUsername = it }
        }
    }

    // Helper methods with DataStore fallback for cold start safety
    suspend fun getAccessToken(): String? {
        return cachedAccessToken ?: accessToken.firstOrNull()?.also { cachedAccessToken = it }
    }

    suspend fun getRefreshToken(): String? {
        return cachedRefreshToken ?: refreshToken.firstOrNull()?.also { cachedRefreshToken = it }
    }

    suspend fun getUserId(): Long? {
        return cachedUserId ?: userIdFlow.firstOrNull()?.also { cachedUserId = it }
    }

    suspend fun getUsername(): String? {
        return cachedUsername ?: usernameFlow.firstOrNull()?.also { cachedUsername = it }
    }

    // Saves the tokens, user ID, and username received during login
    suspend fun saveTokens(
        access: String,
        refresh: String,
        userId: Long? = null,
        username: String? = null
    ) {
        cachedAccessToken = access
        cachedRefreshToken = refresh
        if (userId != null) cachedUserId = userId
        if (!username.isNullOrBlank()) cachedUsername = username

        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = access
            preferences[REFRESH_TOKEN_KEY] = refresh
            if (userId != null) {
                preferences[USER_ID_KEY] = userId
            }
            if (!username.isNullOrBlank()) {
                preferences[USERNAME_KEY] = username
            }
        }
    }

    // Saves or updates only the user ID
    suspend fun saveUserId(userId: Long) {
        cachedUserId = userId

        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    // Saves or updates only the username
    suspend fun saveUsername(username: String) {
        cachedUsername = username

        context.dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }

    // Clears all saved auth data during logout
    suspend fun clearTokens() {
        cachedAccessToken = null
        cachedRefreshToken = null
        cachedUserId = null
        cachedUsername = null

        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(USERNAME_KEY)
        }
    }
}