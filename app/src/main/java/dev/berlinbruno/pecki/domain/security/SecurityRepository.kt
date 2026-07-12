package dev.berlinbruno.pecki.domain.security

import dev.berlinbruno.pecki.data.security.SecurityPreferences
import kotlinx.coroutines.flow.Flow

interface SecurityRepository {
    val securityPreferences: Flow<SecurityPreferences>
    suspend fun setThemeMode(mode: Int)
    suspend fun setSecurityEnabled(enabled: Boolean)
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun setAutoLockTimeout(timeoutMs: Long)
    suspend fun updateLastUnlockedAt(timestamp: Long)
    suspend fun updateLastBackgroundedAt(timestamp: Long)
    suspend fun savePin(pin: String)
    suspend fun clearPin()
    suspend fun verifyPin(pin: String): Boolean
    suspend fun incrementFailedAttempts()
    suspend fun resetFailedAttempts()
    suspend fun setCooldownUntil(timestamp: Long)
    suspend fun clearAll()
}
