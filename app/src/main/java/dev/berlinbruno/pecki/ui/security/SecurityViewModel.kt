package dev.berlinbruno.pecki.ui.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.berlinbruno.pecki.data.security.AppSessionManager
import dev.berlinbruno.pecki.domain.security.SecurityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val securityRepository: SecurityRepository,
    private val sessionManager: AppSessionManager
) : ViewModel() {

    val securityPreferences = securityRepository.securityPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isUnlocked = sessionManager.isUnlocked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            securityRepository.setBiometricEnabled(enabled)
        }
    }

    fun setAutoLockTimeout(timeoutMs: Long) {
        viewModelScope.launch {
            securityRepository.setAutoLockTimeout(timeoutMs)
        }
    }

    fun disableSecurity() {
        viewModelScope.launch {
            securityRepository.clearPin()
        }
    }

    fun lockNow() {
        sessionManager.lock()
    }

    fun unlock() {
        sessionManager.setUnlocked(true)
    }
}
