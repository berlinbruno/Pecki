package dev.berlinbruno.pecki.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.berlinbruno.pecki.ui.components.common.BottomSheetLayout
import dev.berlinbruno.pecki.ui.transactions.models.SortBy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSortBy: SortBy,
    onDismiss: () -> Unit,
    onApply: (SortBy) -> Unit
) {
    var selectedSortBy by remember { mutableStateOf(currentSortBy) }

    BottomSheetLayout(
        title = "Sort By",
        onResetClick = {
            selectedSortBy = SortBy.DATE_DESC
            onApply(SortBy.DATE_DESC)
        },
        actionLabel = "Apply Sort",
        onActionClick = {
            onApply(selectedSortBy)
            onDismiss()
        }
    ) {
        // Date Section
        SortSection(
            title = "Date",
            options = listOf(
                SortBy.DATE_DESC to "Newest First",
                SortBy.DATE_ASC to "Oldest First"
            ),
            selected = selectedSortBy,
            onSelected = { selectedSortBy = it }
        )

        // Amount Section
        SortSection(
            title = "Amount",
            options = listOf(
                SortBy.AMOUNT_DESC to "Highest First",
                SortBy.AMOUNT_ASC to "Lowest First"
            ),
            selected = selectedSortBy,
            onSelected = { selectedSortBy = it }
        )
    }
}

@Composable
private fun SortSection(
    title: String,
    options: List<Pair<SortBy, String>>,
    selected: SortBy,
    onSelected: (SortBy) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.padding(top = dev.berlinbruno.pecki.ui.theme.Spacing.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(dev.berlinbruno.pecki.ui.theme.Spacing.small)
        ) {
            options.forEach { (sortBy, label) ->
                FilterChip(
                    selected = selected == sortBy,
                    onClick = { onSelected(sortBy) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    }
}
