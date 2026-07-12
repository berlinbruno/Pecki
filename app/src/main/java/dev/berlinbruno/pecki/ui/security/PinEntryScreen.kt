package dev.berlinbruno.pecki.ui.security

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.berlinbruno.pecki.R
import dev.berlinbruno.pecki.ui.theme.Spacing
import kotlin.math.roundToInt

@Composable
fun PinEntryScreen(
    title: String,
    subtitle: String? = null,
    viewModel: PinViewModel,
    isSetup: Boolean = false,
    modifier: Modifier = Modifier,
    onBiometricClick: (() -> Unit)? = null,
    onSuccess: () -> Unit
) {
    val state by viewModel.pinState.collectAsState()
    
    // Shake animation logic
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(state.error) {
        if (state.error != null) {
            repeat(4) {
                shakeOffset.animateTo(
                    targetValue = if (it % 2 == 0) 10f else -10f,
                    animationSpec = tween(durationMillis = 50, easing = LinearEasing)
                )
            }
            shakeOffset.animateTo(0f)
        }
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            onSuccess()
            viewModel.clearState()
        }
    }

    Column(
        modifier = modifier
            .padding(Spacing.large)
            .offset { IntOffset(x = shakeOffset.value.roundToInt(), y = 0) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(Spacing.huge))
            
            // Branding
            Image(
                painter = painterResource(id = R.drawable.lock_mascot),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(Spacing.large))

            AnimatedContent(
                targetState = if (state.isConfirming) "Confirm PIN" else title,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "TitleAnimation"
            ) { targetTitle ->
                Text(
                    text = targetTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(Spacing.small))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(Spacing.extraLarge))
            PinDisplay(pinLength = state.pin.length)
            
            val message = when {
                state.cooldownSeconds > 0 -> "Too many attempts. Try again in ${state.cooldownSeconds}s"
                state.error != null -> state.error
                else -> null
            }

            AnimatedVisibility(
                visible = message != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (message != null) {
                    Column {
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PinPad(
                onDigitClick = { digit ->
                    viewModel.onPinInput(digit)
                    if (viewModel.pinState.value.pin.length == 4 && !isSetup) {
                        viewModel.submitPin(isSetup)
                    }
                },
                onBackspaceClick = { viewModel.onBackspace() },
                onBiometricClick = onBiometricClick
            )
            
            if (isSetup) {
                Button(
                    onClick = { viewModel.submitPin(isSetup) },
                    enabled = state.pin.length == 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.medium)
                        .padding(bottom = Spacing.medium),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(if (state.isConfirming) "Confirm" else "Next")
                }
            }
        }
    }
}
