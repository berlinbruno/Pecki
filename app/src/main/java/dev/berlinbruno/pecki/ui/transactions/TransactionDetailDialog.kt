package dev.berlinbruno.pecki.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.Mode
import dev.berlinbruno.pecki.domain.transactions.models.Transaction
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType
import dev.berlinbruno.pecki.ui.theme.CornerRadius
import dev.berlinbruno.pecki.ui.theme.Spacing
import dev.berlinbruno.pecki.utils.formatCurrency
import java.text.SimpleDateFormat
import java.util.*

private const val DETAIL_DIVIDER_THICKNESS = 0.5f
private const val DETAIL_DATE_FORMAT = "EEEE, dd MMM yyyy"
private const val TIME_FORMAT = "HH:mm"

// Vibrant Colors
private val VibrantGreen = Color(0xFF00C853)
private val VibrantRed = Color(0xFFD50000)

@Composable
fun TransactionDetailDialog(
    transaction: Transaction,
    categories: List<Category>,
    modes: List<Mode>,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat(DETAIL_DATE_FORMAT, Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat(TIME_FORMAT, Locale.getDefault()) }
    val category = categories.find { it.id == transaction.categoryId }
    val mode = modes.find { it.id == transaction.modeId }
    
    val categoryColor = category?.color?.let { Color(it) } ?: MaterialTheme.colorScheme.primary

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        dismissButton = {
            DialogActionButtons(
                onEdit = onEdit,
                onDelete = onDelete
            )
        },
        title = {
            DialogTitle(transaction, category, categoryColor)
        },
        text = {
            DialogContent(
                transaction = transaction,
                mode = mode,
                categoryColor = categoryColor,
                dateFormatter = dateFormatter,
                timeFormatter = timeFormatter
            )
        },
        shape = RoundedCornerShape(CornerRadius.large),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun DialogActionButtons(
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row {
        TextButton(
            onClick = onEdit,
            modifier = Modifier.padding(end = Spacing.small),
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Edit")
        }
        TextButton(
            onClick = onDelete,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Delete")
        }
    }
}

@Composable
private fun DialogTitle(transaction: Transaction, category: Category?, categoryColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = transaction.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = category?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Other",
            style = MaterialTheme.typography.bodyMedium,
            color = categoryColor
        )
    }
}

@Composable
private fun DialogContent(
    transaction: Transaction,
    mode: Mode?,
    categoryColor: Color,
    dateFormatter: SimpleDateFormat,
    timeFormatter: SimpleDateFormat
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
    ) {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = Spacing.small),
            thickness = DETAIL_DIVIDER_THICKNESS.dp
        )

        val isDebit = transaction.type == TransactionType.DEBIT
        DetailRow(
            label = "Amount",
            value = formatCurrency(transaction.amount, transaction.currency),
            color = if (isDebit) VibrantRed else VibrantGreen
        )
        DetailRow(
            label = "Date",
            value = dateFormatter.format(Date(transaction.dateTime))
        )
        DetailRow(
            label = "Time",
            value = timeFormatter.format(Date(transaction.dateTime))
        )
        DetailRow(
            label = "Mode",
            value = mode?.name ?: "Other"
        )
        DetailRow(
            label = "Source",
            value = transaction.source.name.lowercase().replaceFirstChar { it.uppercase() }
        )

        if (!transaction.note.isNullOrBlank()) {
            NoteSection(transaction.note, categoryColor)
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.extraSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun NoteSection(note: String, themeColor: Color) {
    Spacer(modifier = Modifier.height(Spacing.small))
    Text(
        text = "Note",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(themeColor.copy(alpha = 0.1f))
            .padding(Spacing.medium)
    ) {
        Text(
            text = note,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
