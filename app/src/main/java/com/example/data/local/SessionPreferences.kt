package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.i18n.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lady_lebanan_prefs")

class SessionPreferences(private val context: Context) {
    private object Keys {
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        val GUEST_MODE = booleanPreferencesKey("guest_mode")
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }

    val currentUserId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.CURRENT_USER_ID]?.takeIf { it.isNotBlank() }
    }

    val isGuestMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.GUEST_MODE] ?: false
    }

    val language: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        AppLanguage.fromCode(prefs[Keys.LANGUAGE_CODE])
    }

    suspend fun setSignedInUser(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CURRENT_USER_ID] = userId
            prefs[Keys.GUEST_MODE] = false
        }
    }

    suspend fun setGuestMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GUEST_MODE] = enabled
            if (enabled) prefs.remove(Keys.CURRENT_USER_ID)
        }
    }

    suspend fun signOut() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.CURRENT_USER_ID)
            prefs[Keys.GUEST_MODE] = false
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE_CODE] = language.code
        }
    }
}
