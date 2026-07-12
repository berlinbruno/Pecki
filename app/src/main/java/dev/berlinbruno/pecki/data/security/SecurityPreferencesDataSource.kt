package dev.berlinbruno.pecki.data.security

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "security_settings")

@Singleton
class SecurityPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val THEME_MODE = intPreferencesKey("theme_mode")
        val CURRENCY_CODE = stringPreferencesKey("currency_code")
        val TIME_RANGE_TYPE = intPreferencesKey("time_range_type")
        val SECURITY_ENABLED = booleanPreferencesKey("security_enabled")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val AUTO_LOCK_TIMEOUT = longPreferencesKey("auto_lock_timeout")
        val LAST_UNLOCKED_AT = longPreferencesKey("last_unlocked_at")
        val LAST_BACKGROUNDED_AT = longPreferencesKey("last_backgrounded_at")
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val FAILED_ATTEMPTS = intPreferencesKey("failed_attempts")
        val COOLDOWN_UNTIL = longPreferencesKey("cooldown_until")
    }

    val securityPreferencesFlow: Flow<SecurityPreferences> = context.dataStore.data
        .map { preferences ->
            SecurityPreferences(
                themeMode = preferences[PreferencesKeys.THEME_MODE] ?: 0,
                currencyCode = preferences[PreferencesKeys.CURRENCY_CODE] ?: "€",
                timeRangeType = preferences[PreferencesKeys.TIME_RANGE_TYPE] ?: 0,
                securityEnabled = preferences[PreferencesKeys.SECURITY_ENABLED] ?: false,
                biometricEnabled = preferences[PreferencesKeys.BIOMETRIC_ENABLED] ?: false,
                autoLockTimeoutMs = preferences[PreferencesKeys.AUTO_LOCK_TIMEOUT] ?: 0,
                lastUnlockedAt = preferences[PreferencesKeys.LAST_UNLOCKED_AT] ?: 0,
                lastBackgroundedAt = preferences[PreferencesKeys.LAST_BACKGROUNDED_AT] ?: 0,
                pinHash = preferences[PreferencesKeys.PIN_HASH],
                failedAttempts = preferences[PreferencesKeys.FAILED_ATTEMPTS] ?: 0,
                cooldownUntil = preferences[PreferencesKeys.COOLDOWN_UNTIL] ?: 0
            )
        }

    suspend fun updateThemeMode(mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    suspend fun updateCurrencyCode(code: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENCY_CODE] = code
        }
    }

    suspend fun updateTimeRangeType(type: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TIME_RANGE_TYPE] = type
        }
    }

    suspend fun updateSecurityEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SECURITY_ENABLED] = enabled
        }
    }

    suspend fun updateBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_ENABLED] = enabled
        }
    }

    suspend fun updateAutoLockTimeout(timeoutMs: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_LOCK_TIMEOUT] = timeoutMs
        }
    }

    suspend fun updateLastUnlockedAt(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_UNLOCKED_AT] = timestamp
        }
    }

    suspend fun updateLastBackgroundedAt(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_BACKGROUNDED_AT] = timestamp
        }
    }

    suspend fun updatePinHash(hash: String?) {
        context.dataStore.edit { preferences ->
            if (hash == null) {
                preferences.remove(PreferencesKeys.PIN_HASH)
            } else {
                preferences[PreferencesKeys.PIN_HASH] = hash
            }
        }
    }

    suspend fun updateFailedAttempts(attempts: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FAILED_ATTEMPTS] = attempts
        }
    }

    suspend fun updateCooldownUntil(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COOLDOWN_UNTIL] = timestamp
        }
    }

    suspend fun clearSecuritySettings() {
        context.dataStore.edit { it.clear() }
    }
}
