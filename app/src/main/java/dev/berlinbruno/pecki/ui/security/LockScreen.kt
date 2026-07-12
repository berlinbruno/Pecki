package dev.berlinbruno.pecki.ui.security

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * The LockScreen is shown when the app is locked (via PIN or Biometric).
 * It handles the authentication flow and unlocks the app upon success.
 */
@Composable
fun LockScreen(
    securityViewModel: SecurityViewModel,
    pinViewModel: PinViewModel
) {
    val prefs by securityViewModel.securityPreferences.collectAsState()
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    
    // Track if we have already automatically prompted for biometrics in this session of the LockScreen
    var hasAutoPrompted by remember { mutableStateOf(false) }

    // Logic to show biometric prompt
    val triggerBiometric = {
        if (activity != null) {
            showBiometricPrompt(activity) {
                securityViewModel.unlock()
            }
        }
    }

    // Auto-trigger biometric prompt if enabled and not already prompted
    LaunchedEffect(prefs, activity) {
        if (prefs?.biometricEnabled == true && activity != null && !hasAutoPrompted) {
            val biometricManager = BiometricManager.from(activity)
            val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            
            if (biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS) {
                hasAutoPrompted = true
                triggerBiometric()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PinEntryScreen(
                title = "Pecki is Locked",
                subtitle = "Enter PIN to unlock",
                viewModel = pinViewModel,
                modifier = Modifier.fillMaxSize(),
                onBiometricClick = if (prefs?.biometricEnabled == true) {
                    { triggerBiometric() }
                } else null,
                onSuccess = {
                    securityViewModel.unlock()
                }
            )
        }
    }
}

/**
 * Helper function to initialize and show the Android BiometricPrompt.
 */
private fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    
    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            // Handle specific errors if needed, otherwise user can still use PIN
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onSuccess()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            // Feedback is usually handled by the system UI
        }
    }

    val biometricPrompt = BiometricPrompt(activity, executor, callback)

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock Pecki")
        .setSubtitle("Authenticate to access your finances")
        .setNegativeButtonText("Use PIN")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        .build()

    try {
        biometricPrompt.authenticate(promptInfo)
    } catch (_: Exception) {
        // Fallback: the user can still use their PIN if biometrics fail to start
    }
}
