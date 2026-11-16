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

private val Context.dataStore by preferencesDataStore(name = "nexo_auth_tokens")

class TokenManager(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val AVATAR_ID_KEY = stringPreferencesKey("avatar_id")
        private val LAST_STUDIED_DATE_KEY = stringPreferencesKey("last_studied_date")
        private val NOTIFICATIONS_ENABLED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("notifications_enabled")
        private val STUDY_WEEKENDS_ENABLED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("study_weekends_enabled")

    }

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

    @Volatile
    var cachedAvatarId: String = "default"
        private set

    val accessToken: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN_KEY] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[REFRESH_TOKEN_KEY] }
    val userIdFlow: Flow<Long?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val usernameFlow: Flow<String?> = context.dataStore.data.map { it[USERNAME_KEY] }
    val avatarIdFlow: Flow<String> = context.dataStore.data.map { it[AVATAR_ID_KEY] ?: "default" }

    init {
        CoroutineScope(Dispatchers.IO).launch { accessToken.collect { cachedAccessToken = it } }
        CoroutineScope(Dispatchers.IO).launch { refreshToken.collect { cachedRefreshToken = it } }
        CoroutineScope(Dispatchers.IO).launch { userIdFlow.collect { cachedUserId = it } }
        CoroutineScope(Dispatchers.IO).launch { usernameFlow.collect { cachedUsername = it } }
        CoroutineScope(Dispatchers.IO).launch { avatarIdFlow.collect { cachedAvatarId = it } }
    }

    suspend fun getAccessToken(): String? =
        cachedAccessToken ?: accessToken.firstOrNull()?.also { cachedAccessToken = it }

    suspend fun getRefreshToken(): String? =
        cachedRefreshToken ?: refreshToken.firstOrNull()?.also { cachedRefreshToken = it }

    suspend fun getUserId(): Long? =
        cachedUserId ?: userIdFlow.firstOrNull()?.also { cachedUserId = it }

    suspend fun getUsername(): String? =
        cachedUsername ?: usernameFlow.firstOrNull()?.also { cachedUsername = it }

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
            if (userId != null) preferences[USER_ID_KEY] = userId
            if (!username.isNullOrBlank()) preferences[USERNAME_KEY] = username
        }
    }

    suspend fun saveUserId(userId: Long) {
        cachedUserId = userId
        context.dataStore.edit { it[USER_ID_KEY] = userId }
    }

    suspend fun saveUsername(username: String) {
        cachedUsername = username
        context.dataStore.edit { it[USERNAME_KEY] = username }
    }

    suspend fun saveAvatarId(avatarId: String) {
        cachedAvatarId = avatarId
        context.dataStore.edit { it[AVATAR_ID_KEY] = avatarId }
    }

    suspend fun clearTokens() {
        cachedAccessToken = null
        cachedRefreshToken = null
        cachedUserId = null
        cachedUsername = null
        cachedAvatarId = "default"

        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(USERNAME_KEY)
            preferences.remove(AVATAR_ID_KEY)
            preferences.remove(LAST_STUDIED_DATE_KEY)
            preferences.remove(NOTIFICATIONS_ENABLED_KEY)
            preferences.remove(STUDY_WEEKENDS_ENABLED_KEY)
        }
    }

    suspend fun updateStudiedToday(studiedToday: Boolean) {
        val todayStr = java.time.LocalDate.now().toString()
        context.dataStore.edit { prefs ->
            if (studiedToday) {
                prefs[LAST_STUDIED_DATE_KEY] = todayStr
            } else if (prefs[LAST_STUDIED_DATE_KEY] == todayStr) {
                prefs.remove(LAST_STUDIED_DATE_KEY)
            }
        }
    }

    suspend fun saveNotificationPreferences(notificationsEnabled: Boolean, studyWeekendsEnabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED_KEY] = notificationsEnabled
            prefs[STUDY_WEEKENDS_ENABLED_KEY] = studyWeekendsEnabled
        }
    }

    suspend fun areNotificationsEnabled(): Boolean {
        return context.dataStore.data.firstOrNull()?.get(NOTIFICATIONS_ENABLED_KEY) ?: true
    }

    suspend fun isStudyWeekendsEnabled(): Boolean {
        return context.dataStore.data.firstOrNull()?.get(STUDY_WEEKENDS_ENABLED_KEY) ?: true
    }
    suspend fun hasStudiedToday(): Boolean {
        val todayStr = java.time.LocalDate.now().toString()
        val saved = context.dataStore.data.firstOrNull()?.get(LAST_STUDIED_DATE_KEY)
        return saved == todayStr
    }
}