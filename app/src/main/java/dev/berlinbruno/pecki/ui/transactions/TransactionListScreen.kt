package dev.berlinbruno.pecki.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.berlinbruno.pecki.domain.transactions.models.Transaction
import dev.berlinbruno.pecki.ui.components.common.BaseBottomBar
import dev.berlinbruno.pecki.ui.components.common.BottomBarButton
import dev.berlinbruno.pecki.ui.components.common.BottomBarDivider
import dev.berlinbruno.pecki.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel = hiltViewModel(),
    onMenuClick: () -> Unit,
    onAddTransactionClick: () -> Unit,
    onEditTransactionClick: (Transaction) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val filters by viewModel.filters.collectAsState()
    
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }
    var transactionToDisapprove by remember { mutableStateOf<Transaction?>(null) }
    var transactionToView by remember { mutableStateOf<Transaction?>(null) }
    var showBulkDeleteConfirm by remember { mutableStateOf(false) }
    var showBulkDisapproveConfirm by remember { mutableStateOf(false) }
    
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (transactionToView != null) {
        TransactionDetailDialog(
            transaction = transactionToView!!,
            categories = uiState.categories,
            modes = uiState.modes,
            onDismiss = { transactionToView = null },
            onEdit = {
                onEditTransactionClick(transactionToView!!)
                transactionToView = null
            },
            onDelete = {
                transactionToDelete = transactionToView
                transactionToView = null
            }
        )
    }

    if (transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        transactionToDelete?.let { viewModel.deleteTransaction(it) }
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (transactionToDisapprove != null) {
        AlertDialog(
            onDismissRequest = { transactionToDisapprove = null },
            title = { Text("Disapprove Transaction") },
            text = { Text("This transaction will be moved back to pending approval. Continue?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        transactionToDisapprove?.let { viewModel.disapproveTransaction(it) }
                        transactionToDisapprove = null
                    }
                ) {
                    Text("Disapprove")
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDisapprove = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showBulkDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteConfirm = false },
            title = { Text("Delete Selected") },
            text = { Text("Are you sure you want to delete ${uiState.selectedIds.size} transactions?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSelectedTransactions()
                        showBulkDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showBulkDisapproveConfirm) {
        AlertDialog(
            onDismissRequest = { showBulkDisapproveConfirm = false },
            title = { Text("Disapprove Selected") },
            text = { Text("Move ${uiState.selectedIds.size} transactions back to pending approval?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.disapproveSelectedTransactions()
                        showBulkDisapproveConfirm = false
                    }
                ) {
                    Text("Disapprove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDisapproveConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            FilterBottomSheet(
                initialFilters = filters,
                categories = uiState.categories,
                onDismiss = { showFilterSheet = false },
                onApply = { viewModel.updateFilters(it) }
            )
        }
    }

    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            SortBottomSheet(
                currentSortBy = filters.sortBy,
                onDismiss = { showSortSheet = false },
                onApply = { viewModel.updateSort(it) }
            )
        }
    }

    Scaffold(
        topBar = {
            Column {
                if (uiState.isSelectionMode) {
                    TopAppBar(
                        title = { 
                            Text(
                                text = "${uiState.selectedIds.size} selected",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            ) 
                        },
                        navigationIcon = {
                            IconButton(onClick = { viewModel.clearSelection() }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear Selection")
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.toggleSelectAll(uiState.transactions) }) {
                                val isAllSelected = uiState.selectedIds.size == uiState.transactions.size && uiState.transactions.isNotEmpty()
                                Icon(
                                    imageVector = Icons.Default.SelectAll,
                                    contentDescription = "Select All",
                                    tint = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(onClick = { showBulkDisapproveConfirm = true }) {
                                Icon(Icons.Default.Block, contentDescription = "Disapprove Selected")
                            }
                            IconButton(onClick = { showBulkDeleteConfirm = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Selected", tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                } else {
                    TopAppBar(
                        title = { 
                            Text(
                                text = "Transactions",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            ) 
                        },
                        navigationIcon = {
                            IconButton(onClick = onMenuClick) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            }
        },
        bottomBar = {
            if (!uiState.isSelectionMode) {
                BaseBottomBar {
                    BottomBarButton(
                        label = "Filter",
                        icon = Icons.Default.FilterList,
                        onClick = { showFilterSheet = true },
                        badgeCount = uiState.activeFilterCount
                    )

                    BottomBarDivider()

                    BottomBarButton(
                        label = "Add",
                        icon = Icons.Default.Add,
                        onClick = onAddTransactionClick,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    BottomBarDivider()

                    BottomBarButton(
                        label = "Sort",
                        icon = Icons.AutoMirrored.Filled.Sort,
                        onClick = { showSortSheet = true }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.transactions.isEmpty()) {
                Text(
                    text = if (uiState.activeFilterCount > 0) "No transactions match filters" else "No transactions yet",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    items(uiState.transactions, key = { it.id }) { transaction ->
                        val isSelected = uiState.selectedIds.contains(transaction.id)
                        RevealableTransactionItem(
                            transaction = transaction,
                            categories = uiState.categories,
                            isSelected = isSelected,
                            isSelectionMode = uiState.isSelectionMode,
                            onLongClick = { viewModel.toggleSelection(transaction.id) },
                            onDelete = { transactionToDelete = transaction },
                            onEdit = { onEditTransactionClick(transaction) },
                            onDisapprove = { transactionToDisapprove = transaction },
                            onClick = {
                                if (uiState.isSelectionMode) {
                                    viewModel.toggleSelection(transaction.id)
                                } else {
                                    transactionToView = transaction
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
