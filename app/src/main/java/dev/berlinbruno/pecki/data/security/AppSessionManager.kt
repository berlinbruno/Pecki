package dev.berlinbruno.pecki.data.security

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.berlinbruno.pecki.domain.security.SecurityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSessionManager @Inject constructor(
    private val securityRepository: SecurityRepository
) : DefaultLifecycleObserver {

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)

    fun setUnlocked(unlocked: Boolean) {
        _isUnlocked.value = unlocked
        if (unlocked) {
            scope.launch(Dispatchers.IO) {
                securityRepository.updateLastUnlockedAt(System.currentTimeMillis())
            }
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        scope.launch(Dispatchers.IO) {
            val prefs = securityRepository.securityPreferences.first()
            if (!prefs.securityEnabled) {
                _isUnlocked.value = true
            } else {
                if (!_isUnlocked.value) return@launch // Already locked, keep it locked

                val now = System.currentTimeMillis()
                val elapsedSinceBackground = now - prefs.lastBackgroundedAt
                
                if (elapsedSinceBackground > prefs.autoLockTimeoutMs) {
                    _isUnlocked.value = false
                }
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        scope.launch(Dispatchers.IO) {
            securityRepository.updateLastBackgroundedAt(System.currentTimeMillis())
        }
    }

    fun lock() {
        _isUnlocked.value = false
        scope.launch(Dispatchers.IO) {
            securityRepository.updateLastBackgroundedAt(0)
            securityRepository.updateLastUnlockedAt(0)
        }
    }
}
