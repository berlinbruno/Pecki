package dev.berlinbruno.pecki.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.berlinbruno.pecki.ui.theme.Spacing

@Composable
fun BottomSheetLayout(
    title: String,
    onResetClick: () -> Unit,
    resetLabel: String = "Reset",
    actionLabel: String,
    onActionClick: () -> Unit,
    actionEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onResetClick) {
                Text(
                    text = resetLabel,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            content()
            // Spacer to ensure content doesn't touch the bottom button too early
            Spacer(modifier = Modifier.height(Spacing.extraSmall))
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = Spacing.medium),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Sticky Action Button
        Button(
            onClick = onActionClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = actionEnabled,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = actionLabel)
        }
        
        Spacer(modifier = Modifier.height(Spacing.medium))
    }
}
