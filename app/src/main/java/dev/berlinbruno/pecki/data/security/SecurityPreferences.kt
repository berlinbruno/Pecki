package dev.berlinbruno.pecki.data.security

data class SecurityPreferences(
    val themeMode: Int = 0, // 0: System, 1: Light, 2: Dark
    val securityEnabled: Boolean = false,
    val biometricEnabled: Boolean = false,
    val autoLockTimeoutMs: Long = 0,
    val lastUnlockedAt: Long = 0,
    val lastBackgroundedAt: Long = 0,
    val pinHash: String? = null,
    val failedAttempts: Int = 0,
    val cooldownUntil: Long = 0
)
