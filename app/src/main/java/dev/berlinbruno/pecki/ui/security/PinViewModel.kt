package dev.berlinbruno.pecki.ui.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.berlinbruno.pecki.data.security.AppSessionManager
import dev.berlinbruno.pecki.domain.security.SecurityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val securityRepository: SecurityRepository,
    private val sessionManager: AppSessionManager
) : ViewModel() {

    private val _pinState = MutableStateFlow(PinState())
    val pinState = _pinState.asStateFlow()

    private var cooldownJob: Job? = null

    data class PinState(
        val pin: String = "",
        val confirmPin: String = "",
        val isConfirming: Boolean = false,
        val error: String? = null,
        val success: Boolean = false,
        val cooldownSeconds: Int = 0
    )

    init {
        viewModelScope.launch {
            securityRepository.securityPreferences.collect { prefs ->
                val now = System.currentTimeMillis()
                if (prefs.cooldownUntil > now) {
                    startCooldownTimer(prefs.cooldownUntil)
                } else {
                    stopCooldownTimer()
                }
            }
        }
    }

    private fun startCooldownTimer(until: Long) {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            while (System.currentTimeMillis() < until) {
                val secondsLeft = ((until - System.currentTimeMillis()) / 1000).toInt() + 1
                _pinState.value = _pinState.value.copy(cooldownSeconds = secondsLeft)
                delay(1000)
            }
            _pinState.value = _pinState.value.copy(cooldownSeconds = 0, error = null)
        }
    }

    private fun stopCooldownTimer() {
        cooldownJob?.cancel()
        _pinState.value = _pinState.value.copy(cooldownSeconds = 0, error = null)
    }

    fun onPinInput(digit: String) {
        if (_pinState.value.cooldownSeconds > 0) return
        if (_pinState.value.pin.length < 4) {
            _pinState.value = _pinState.value.copy(
                pin = _pinState.value.pin + digit,
                error = null
            )
        }
    }

    fun onBackspace() {
        if (_pinState.value.cooldownSeconds > 0) return
        if (_pinState.value.pin.isNotEmpty()) {
            _pinState.value = _pinState.value.copy(
                pin = _pinState.value.pin.dropLast(1)
            )
        }
    }

    fun submitPin(isSetup: Boolean) {
        viewModelScope.launch {
            if (isSetup) {
                handleSetupSubmit()
            } else {
                handleUnlockSubmit()
            }
        }
    }

    private suspend fun handleSetupSubmit() {
        val state = _pinState.value
        if (!state.isConfirming) {
            _pinState.value = state.copy(
                isConfirming = true,
                confirmPin = state.pin,
                pin = ""
            )
        } else {
            if (state.pin == state.confirmPin) {
                securityRepository.savePin(state.pin)
                sessionManager.setUnlocked(true)
                _pinState.value = state.copy(success = true)
            } else {
                _pinState.value = state.copy(
                    error = "PINs do not match",
                    pin = ""
                )
            }
        }
    }

    private suspend fun handleUnlockSubmit() {
        val state = _pinState.value
        val prefs = securityRepository.securityPreferences.first()
        val now = System.currentTimeMillis()
        
        if (prefs.cooldownUntil > now) {
            _pinState.value = state.copy(
                error = "Too many attempts. Try again later.",
                pin = ""
            )
            return
        }

        if (securityRepository.verifyPin(state.pin)) {
            securityRepository.resetFailedAttempts()
            sessionManager.setUnlocked(true)
            _pinState.value = state.copy(success = true)
        } else {
            securityRepository.incrementFailedAttempts()
            val updatedPrefs = securityRepository.securityPreferences.first()
            if (updatedPrefs.failedAttempts >= 5) {
                val cooldownTime = 30000L // 30 seconds
                securityRepository.setCooldownUntil(now + cooldownTime)
                _pinState.value = state.copy(
                    error = "Too many attempts. Locked for 30s.",
                    pin = ""
                )
            } else {
                val remaining = 5 - updatedPrefs.failedAttempts
                _pinState.value = state.copy(
                    error = "Incorrect PIN. $remaining attempts remaining.",
                    pin = ""
                )
            }
        }
    }

    fun clearState() {
        _pinState.value = PinState()
    }
}
