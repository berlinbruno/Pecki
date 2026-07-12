package dev.berlinbruno.pecki.ui.transactions.management

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType
import dev.berlinbruno.pecki.ui.components.common.BottomSheetLayout
import dev.berlinbruno.pecki.ui.theme.Spacing

@Composable
fun CategoryForm(viewModel: CategoryManagementViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    BottomSheetLayout(
        title = if (uiState.isEdit) "Edit Category" else "Add Category",
        onResetClick = { viewModel.resetState() },
        resetLabel = if (uiState.isEdit) "Restore" else "Reset",
        actionLabel = if (uiState.isEdit) "Update Category" else "Save Category",
        onActionClick = { viewModel.saveCategory() }
    ) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.onNameChange(it) },
            label = { Text("Category Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.nameError != null,
            supportingText = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.weight(1f)) {
                        uiState.nameError?.let { Text(text = it) }
                    }
                    Text(text = "${uiState.name.length} / 25")
                }
            },
            shape = RoundedCornerShape(12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
            Text("Type", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val types = listOf(TransactionType.DEBIT, TransactionType.CREDIT, null)
                types.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = uiState.type == type,
                        onClick = { viewModel.onTypeChange(type) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = types.size)
                    ) {
                        Text(
                            when (type) {
                                TransactionType.DEBIT -> "Debit"
                                TransactionType.CREDIT -> "Credit"
                                else -> "Both"
                            }
                        )
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
            Text("Color", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(viewModel.presetColors) { colorInt ->
                    val color = Color(colorInt)
                    val isSelected = uiState.color == colorInt || (uiState.color == null && colorInt == viewModel.presetColors.first())
                    
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { viewModel.onColorChange(colorInt) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = if (colorInt == 0xFFFFFFFF.toInt()) Color.Black else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = uiState.keywords,
            onValueChange = { viewModel.onKeywordsChange(it) },
            label = { Text("Keywords (Optional)") },
            placeholder = { Text("e.g. food, restaurant, cafe") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = { Text("Comma-separated values for auto-categorization") },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun ModeForm(viewModel: ModeManagementViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    BottomSheetLayout(
        title = if (uiState.isEdit) "Edit Mode" else "Add Mode",
        onResetClick = { viewModel.resetState() },
        resetLabel = if (uiState.isEdit) "Restore" else "Reset",
        actionLabel = if (uiState.isEdit) "Update Mode" else "Save Mode",
        onActionClick = { viewModel.saveMode() }
    ) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.onNameChange(it) },
            label = { Text("Mode Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = uiState.nameError != null,
            supportingText = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.weight(1f)) {
                        uiState.nameError?.let { Text(text = it) }
                    }
                    Text(text = "${uiState.name.length} / 25")
                }
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}
