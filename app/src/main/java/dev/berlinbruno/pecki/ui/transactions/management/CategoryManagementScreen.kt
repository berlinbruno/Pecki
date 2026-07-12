package dev.berlinbruno.pecki.ui.transactions.management

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType
import dev.berlinbruno.pecki.ui.components.common.BottomSheetLayout
import dev.berlinbruno.pecki.ui.theme.Spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    viewModel: CategoryManagementViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showSheet = false
                    viewModel.clearSaved()
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { 
                showSheet = false 
                viewModel.resetState()
            },
            sheetState = sheetState,
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
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
                        if (uiState.nameError != null) {
                            Text(text = uiState.nameError!!)
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
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    viewModel.resetState()
                    showSheet = true 
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            Text(
                "Current Categories", 
                style = MaterialTheme.typography.titleSmall, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = Spacing.medium)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
                contentPadding = PaddingValues(bottom = 80.dp) // Avoid overlap with FAB
            ) {
                items(categories, key = { it.id }) { category ->
                    CategoryItem(
                        category = category,
                        onEditClick = {
                            viewModel.loadCategory(category)
                            showSheet = true
                        },
                        onDeleteClick = { viewModel.deleteCategory(category) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val categoryColor = category.color?.let { Color(it) } ?: MaterialTheme.colorScheme.primary

    ListItem(
        headlineContent = { Text(category.name, fontWeight = FontWeight.Medium) },
        supportingContent = {
            Text(
                when (category.type) {
                    TransactionType.DEBIT -> "Debit"
                    TransactionType.CREDIT -> "Credit"
                    else -> "Both Types"
                }
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(categoryColor)
            )
        },
        trailingContent = {
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onEditClick() }
    )
}
