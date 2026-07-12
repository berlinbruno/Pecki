package dev.berlinbruno.pecki.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType
import dev.berlinbruno.pecki.ui.components.common.BottomSheetLayout
import dev.berlinbruno.pecki.ui.theme.Spacing
import dev.berlinbruno.pecki.ui.transactions.models.DateRange
import dev.berlinbruno.pecki.ui.transactions.models.TransactionFilters
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    initialFilters: TransactionFilters,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onApply: (TransactionFilters) -> Unit
) {
    var title by remember { mutableStateOf(initialFilters.title) }
    var selectedType by remember { mutableStateOf(initialFilters.type) }
    var selectedCategoryId by remember { mutableStateOf(initialFilters.categoryId) }
    var selectedDateRange by remember { mutableStateOf(initialFilters.dateRange) }
    var fromDate by remember { mutableStateOf(initialFilters.fromDate) }
    var toDate by remember { mutableStateOf(initialFilters.toDate) }

    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yy", Locale.getDefault()) }

    // Categories that are NOT "Other"
    val namedCategories = remember(selectedType, categories) {
        val filtered = if (selectedType == null) categories
        else categories.filter { it.type == null || it.type == selectedType }
        filtered.filter { !it.id.endsWith("_other") && it.id != "other" }
    }

    val currentOtherId = when (selectedType) {
        null -> "all_other"
        TransactionType.DEBIT -> "debit_other"
        TransactionType.CREDIT -> "credit_other"
    }

    val currentOtherCategory = remember(currentOtherId, categories) {
        categories.find { it.id == "debit_other" || it.id == "credit_other" || it.id == "other" }
    }

    LaunchedEffect(selectedType) {
        if (selectedCategoryId != null) {
            val isOtherSelected = selectedCategoryId == "all_other" || 
                                selectedCategoryId == "debit_other" || 
                                selectedCategoryId == "credit_other"
            
            if (isOtherSelected) {
                selectedCategoryId = currentOtherId
            } else if (namedCategories.none { it.id == selectedCategoryId }) {
                selectedCategoryId = null
            }
        }
    }

    BottomSheetLayout(
        title = "Filters",
        resetLabel = "Reset All",
        onResetClick = {
            title = ""
            selectedType = null
            selectedCategoryId = null
            selectedDateRange = DateRange.ALL
            fromDate = null
            toDate = null
            onApply(TransactionFilters())
        },
        actionLabel = "Apply Filters",
        onActionClick = {
            onApply(
                initialFilters.copy(
                    title = title,
                    type = selectedType,
                    categoryId = selectedCategoryId,
                    dateRange = selectedDateRange,
                    fromDate = fromDate,
                    toDate = toDate
                )
            )
            onDismiss()
        }
    ) {
        // Search
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Search by Title") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (title.isNotEmpty()) {
                    IconButton(onClick = { title = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        // Type
        Column {
            Text(
                "Transaction Type",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.padding(top = Spacing.extraSmall),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                FilterChip(
                    selected = selectedType == null,
                    onClick = { selectedType = null },
                    label = { Text("All") }
                )
                TransactionType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }

        // Date Range
        Column {
            Text(
                "Date Range",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.extraSmall),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                items(DateRange.entries) { range ->
                    FilterChip(
                        selected = selectedDateRange == range,
                        onClick = { selectedDateRange = range },
                        label = { Text(range.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }

        if (selectedDateRange == DateRange.CUSTOM) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.extraSmall),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                DateField(
                    label = "From",
                    value = fromDate?.let { dateFormatter.format(Date(it)) } ?: "Select",
                    onClick = { showFromDatePicker = true },
                    modifier = Modifier.weight(1f)
                )
                DateField(
                    label = "To",
                    value = toDate?.let { dateFormatter.format(Date(it)) } ?: "Select",
                    onClick = { showToDatePicker = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Category
        Column {
            Text(
                "Category",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.extraSmall),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { selectedCategoryId = null },
                        label = { Text("All") }
                    )
                }
                items(namedCategories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategoryId == category.id,
                        onClick = { selectedCategoryId = category.id }
                    )
                }
                item {
                    CategoryChip(
                        category = currentOtherCategory,
                        isSelected = selectedCategoryId == currentOtherId,
                        onClick = { selectedCategoryId = currentOtherId }
                    )
                }
            }
        }
    }

    if (showFromDatePicker) {
        DatePickerModal(
            initialDate = fromDate,
            onDismiss = { showFromDatePicker = false },
            onDateSelected = { fromDate = it }
        )
    }

    if (showToDatePicker) {
        DatePickerModal(
            initialDate = toDate,
            onDismiss = { showToDatePicker = false },
            onDateSelected = { toDate = it }
        )
    }
}

@Composable
private fun DateField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            readOnly = true,
            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
            trailingIcon = { Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp)) },
            shape = RoundedCornerShape(12.dp)
        )
        Box(modifier = Modifier.matchParentSize().clickable(onClick = onClick))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    initialDate: Long?,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate ?: System.currentTimeMillis()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
