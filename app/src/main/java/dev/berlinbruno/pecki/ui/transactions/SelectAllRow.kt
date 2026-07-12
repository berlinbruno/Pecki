package dev.berlinbruno.pecki.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import dev.berlinbruno.pecki.ui.theme.CornerRadius
import dev.berlinbruno.pecki.ui.theme.Spacing

/**
 * Select all row for bulk transaction selection
 * Shows checkbox and toggles between "Select All" and "Deselect All"
 */
@Composable
fun SelectAllRow(
    selectedCount: Int,
    totalCount: Int,
    onSelectAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.medium))
            .clickable { onSelectAll() }
            .padding(
                vertical = Spacing.small,
                horizontal = Spacing.extraSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = selectedCount == totalCount && totalCount > 0,
            onCheckedChange = null
        )
        Text(
            text = if (selectedCount == totalCount && totalCount > 0) "Deselect All" else "Select All",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = Spacing.small)
        )
    }
}
