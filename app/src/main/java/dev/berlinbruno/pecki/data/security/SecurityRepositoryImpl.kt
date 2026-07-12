package dev.berlinbruno.pecki.data.security

import dev.berlinbruno.pecki.domain.security.SecurityRepository
import dev.berlinbruno.pecki.utils.security.SecurityUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityRepositoryImpl @Inject constructor(
    private val dataSource: SecurityPreferencesDataSource
) : SecurityRepository {

    override val securityPreferences: Flow<SecurityPreferences> = dataSource.securityPreferencesFlow

    override suspend fun setThemeMode(mode: Int) {
        dataSource.updateThemeMode(mode)
    }

    override suspend fun setCurrencyCode(code: String) {
        dataSource.updateCurrencyCode(code)
    }

    override suspend fun setTimeRangeType(type: Int) {
        dataSource.updateTimeRangeType(type)
    }

    override suspend fun setSecurityEnabled(enabled: Boolean) {
        dataSource.updateSecurityEnabled(enabled)
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        dataSource.updateBiometricEnabled(enabled)
    }

    override suspend fun setAutoLockTimeout(timeoutMs: Long) {
        dataSource.updateAutoLockTimeout(timeoutMs)
    }

    override suspend fun updateLastUnlockedAt(timestamp: Long) {
        dataSource.updateLastUnlockedAt(timestamp)
    }

    override suspend fun updateLastBackgroundedAt(timestamp: Long) {
        dataSource.updateLastBackgroundedAt(timestamp)
    }

    override suspend fun savePin(pin: String) {
        val hash = SecurityUtils.hashPin(pin)
        dataSource.updatePinHash(hash)
        dataSource.updateSecurityEnabled(true)
    }

    override suspend fun clearPin() {
        dataSource.updatePinHash(null)
        dataSource.updateSecurityEnabled(false)
        dataSource.updateBiometricEnabled(false)
    }

    override suspend fun verifyPin(pin: String): Boolean {
        val currentHash = securityPreferences.first().pinHash
        return currentHash == SecurityUtils.hashPin(pin)
    }

    override suspend fun incrementFailedAttempts() {
        val current = securityPreferences.first().failedAttempts
        dataSource.updateFailedAttempts(current + 1)
    }

    override suspend fun resetFailedAttempts() {
        dataSource.updateFailedAttempts(0)
    }

    override suspend fun setCooldownUntil(timestamp: Long) {
        dataSource.updateCooldownUntil(timestamp)
    }

    override suspend fun clearAll() {
        dataSource.clearSecuritySettings()
    }
}
