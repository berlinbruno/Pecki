package dev.berlinbruno.pecki.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType
import dev.berlinbruno.pecki.ui.components.common.BottomSheetLayout
import dev.berlinbruno.pecki.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTransactionScreen(
    viewModel: CreateTransactionViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val modes by viewModel.modes.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onBackClick()
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.date
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onDateChange(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    BottomSheetLayout(
        title = if (uiState.isEdit) "Edit Transaction" else "Add Transaction",
        resetLabel = if (uiState.isEdit) "Restore" else "Reset",
        onResetClick = { viewModel.resetState() },
        actionLabel = if (uiState.isEdit) "Update Transaction" else "Save Transaction",
        onActionClick = { viewModel.saveTransaction() }
    ) {
        // Type Selection
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            TransactionType.entries.forEachIndexed { index, type ->
                SegmentedButton(
                    selected = uiState.type == type,
                    onClick = { viewModel.onTypeChange(type) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = TransactionType.entries.size)
                ) {
                    Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }

        OutlinedTextField(
            value = uiState.title,
            onValueChange = { viewModel.onTitleChange(it) },
            label = { Text("Title") },
            placeholder = { Text("e.g. Grocery Shopping") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.titleError != null,
            supportingText = {
                if (uiState.titleError != null) {
                    Text(text = uiState.titleError!!)
                }
            },
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = uiState.amount,
            onValueChange = { viewModel.onAmountChange(it) },
            label = { Text("Amount") },
            placeholder = { Text("0.00") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            suffix = { Text("€") },
            isError = uiState.amountError != null,
            supportingText = {
                if (uiState.amountError != null) {
                    Text(text = uiState.amountError!!)
                }
            },
            shape = RoundedCornerShape(12.dp)
        )

        // Date Selection
        val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = dateFormat.format(Date(uiState.date)),
                onValueChange = {},
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                trailingIcon = {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Select Date", modifier = Modifier.size(20.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }

        // Mode Selection
        Text("Mode", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            modes.forEach { mode ->
                FilterChip(
                    selected = uiState.modeId == mode.id,
                    onClick = { viewModel.onModeChange(mode.id) },
                    label = { Text(mode.name, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        // Category Selection
        Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            categories.forEach { category ->
                CategoryChip(
                    category = category,
                    isSelected = uiState.categoryId == category.id,
                    onClick = { viewModel.onCategoryChange(category.id) }
                )
            }
        }

        OutlinedTextField(
            value = uiState.note,
            onValueChange = { viewModel.onNoteChange(it) },
            label = { Text("Note (Optional)") },
            placeholder = { Text("Add more details...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
            supportingText = {
                Text(
                    text = "${uiState.note.length} / 200",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}
