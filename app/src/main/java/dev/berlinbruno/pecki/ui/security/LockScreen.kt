package dev.berlinbruno.pecki.ui.security

import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

@Composable
fun LockScreen(
    securityViewModel: SecurityViewModel,
    pinViewModel: PinViewModel
) {
    val prefs by securityViewModel.securityPreferences.collectAsState()
    val context = LocalContext.current as? FragmentActivity

    LaunchedEffect(prefs) {
        if (prefs?.biometricEnabled == true && context != null) {
            showBiometricPrompt(context) {
                securityViewModel.unlock()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PinEntryScreen(
                title = "Locked",
                subtitle = "Enter PIN to unlock",
                viewModel = pinViewModel,
                modifier = Modifier.fillMaxSize(),
                onBiometricClick = if (prefs?.biometricEnabled == true && context != null) {
                    {
                        showBiometricPrompt(context) {
                            securityViewModel.unlock()
                        }
                    }
                } else null,
                onSuccess = { /* sessionManager handled it */ }
            )
        }
    }
}

private fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock Pecki")
        .setSubtitle("Use your biometric credential")
        .setNegativeButtonText("Use PIN")
        .build()

    biometricPrompt.authenticate(promptInfo)
}
