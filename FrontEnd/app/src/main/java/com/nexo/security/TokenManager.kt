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
    }

    // In-memory cache to prevent blocking network threads with DataStore I/O
    var cachedAccessToken: String? = null
        private set

    var cachedRefreshToken: String? = null
        private set

    var cachedUserId: Long? = null
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
    }

    // Helper method to obtain the current user ID synchronously in coroutines
    suspend fun getUserId(): Long? {
        // Fallback to flow if cache is empty during early initialization, otherwise use cache
        return cachedUserId ?: userIdFlow.firstOrNull()
    }

    // Saves the tokens and user ID received during login
    suspend fun saveTokens(access: String, refresh: String, userId: Long? = null) {
        // Update cache instantly to avoid delays
        cachedAccessToken = access
        cachedRefreshToken = refresh
        if (userId != null) cachedUserId = userId

        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = access
            preferences[REFRESH_TOKEN_KEY] = refresh
            if (userId != null) {
                preferences[USER_ID_KEY] = userId
            }
        }
    }

    // Saves or updates only the user ID
    suspend fun saveUserId(userId: Long) {
        // Update cache instantly
        cachedUserId = userId

        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    // Clears all saved auth data during logout
    suspend fun clearTokens() {
        // Clear cache instantly
        cachedAccessToken = null
        cachedRefreshToken = null
        cachedUserId = null

        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
        }
    }
}