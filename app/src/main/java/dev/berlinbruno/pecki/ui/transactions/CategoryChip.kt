package dev.berlinbruno.pecki.ui.transactions

import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.berlinbruno.pecki.domain.transactions.models.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChip(
    category: Category?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = category?.color?.let { Color(it) } ?: MaterialTheme.colorScheme.onSurfaceVariant
    
    // If selected, use category color. If not, use standard greyish style.
    val activeColor = if (isSelected) categoryColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { 
            Text(
                category?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Other",
                style = MaterialTheme.typography.labelSmall
            ) 
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = activeColor.copy(alpha = 0.15f),
            selectedLabelColor = activeColor,
            selectedLeadingIconColor = activeColor,
            selectedTrailingIconColor = activeColor,
            containerColor = Color.Transparent,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            selectedBorderColor = activeColor.copy(alpha = 0.5f),
            borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            borderWidth = 1.dp,
            selectedBorderWidth = 1.dp
        ),
        modifier = modifier.height(32.dp)
    )
}
