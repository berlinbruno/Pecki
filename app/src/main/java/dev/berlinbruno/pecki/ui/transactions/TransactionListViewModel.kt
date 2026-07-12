package dev.berlinbruno.pecki.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.Mode
import dev.berlinbruno.pecki.domain.transactions.models.Transaction
import dev.berlinbruno.pecki.domain.transactions.models.TransactionStatus
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType
import dev.berlinbruno.pecki.domain.transactions.usecases.DeleteTransactionUseCase
import dev.berlinbruno.pecki.domain.transactions.usecases.DeleteTransactionsUseCase
import dev.berlinbruno.pecki.domain.transactions.usecases.GetCategoriesUseCase
import dev.berlinbruno.pecki.domain.transactions.usecases.GetModesUseCase
import dev.berlinbruno.pecki.domain.transactions.usecases.GetTransactionsUseCase
import dev.berlinbruno.pecki.domain.transactions.usecases.UpdateTransactionStatusUseCase
import dev.berlinbruno.pecki.ui.transactions.models.DateRange
import dev.berlinbruno.pecki.ui.transactions.models.SortBy
import dev.berlinbruno.pecki.ui.transactions.models.TransactionFilters
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val deleteTransactionsUseCase: DeleteTransactionsUseCase,
    private val updateTransactionStatusUseCase: UpdateTransactionStatusUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getModesUseCase: GetModesUseCase
) : ViewModel() {

    private val _filters = MutableStateFlow(TransactionFilters())
    val filters = _filters.asStateFlow()

    private val _selectedTransactionIds = MutableStateFlow(setOf<String>())
    val selectedTransactionIds = _selectedTransactionIds.asStateFlow()

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        combine(
            getTransactionsUseCase(),
            getCategoriesUseCase(),
            getModesUseCase(),
            _filters,
            _selectedTransactionIds
        ) { transactions, categories, modes, filters, selectedIds ->
            val filteredTransactions = filterAndSortTransactions(transactions, filters)
            TransactionListUiState(
                transactions = filteredTransactions,
                categories = categories,
                modes = modes,
                isLoading = false,
                activeFilterCount = calculateActiveFilterCount(filters),
                selectedIds = selectedIds,
                isSelectionMode = selectedIds.isNotEmpty()
            )
        }.onStart { _uiState.update { it.copy(isLoading = true) } }
        .onEach { newState -> _uiState.value = newState }
        .launchIn(viewModelScope)
    }

    fun updateFilters(newFilters: TransactionFilters) {
        _filters.value = newFilters
    }

    fun updateSort(sortBy: SortBy) {
        _filters.update { it.copy(sortBy = sortBy) }
    }

    fun toggleSelection(transactionId: String) {
        _selectedTransactionIds.update { current ->
            if (current.contains(transactionId)) current - transactionId
            else current + transactionId
        }
    }

    fun toggleSelectAll(transactions: List<Transaction>) {
        _selectedTransactionIds.update { current ->
            if (current.size == transactions.size) emptySet()
            else transactions.map { it.id }.toSet()
        }
    }

    fun clearSelection() {
        _selectedTransactionIds.value = emptySet()
    }

    fun deleteSelectedTransactions() {
        val ids = _selectedTransactionIds.value.toList()
        viewModelScope.launch {
            deleteTransactionsUseCase(ids)
            clearSelection()
        }
    }

    fun disapproveSelectedTransactions() {
        val ids = _selectedTransactionIds.value.toList()
        viewModelScope.launch {
            updateTransactionStatusUseCase.invokeBulk(ids, TransactionStatus.PENDING_APPROVAL)
            clearSelection()
        }
    }

    fun disapproveTransaction(transaction: Transaction) {
        viewModelScope.launch {
            updateTransactionStatusUseCase(transaction.id, TransactionStatus.PENDING_APPROVAL)
        }
    }

    private fun filterAndSortTransactions(
        transactions: List<Transaction>,
        filters: TransactionFilters
    ): List<Transaction> {
        return transactions.asSequence()
            .filter { it.status == TransactionStatus.APPROVED }
            .filter { transaction ->
                if (filters.title.isBlank()) true
                else transaction.title.contains(filters.title, ignoreCase = true)
            }
            .filter { transaction ->
                filters.type == null || transaction.type == filters.type
            }
            .filter { transaction ->
                when {
                    filters.categoryId == null -> true
                    filters.categoryId == "all_other" -> 
                        transaction.categoryId == "debit_other" || transaction.categoryId == "credit_other"
                    else -> transaction.categoryId == filters.categoryId
                }
            }
            .filter { transaction ->
                applyDateFilter(transaction.dateTime, filters)
            }
            .sortedWith(getComparator(filters.sortBy))
            .toList()
    }

    private fun applyDateFilter(timestamp: Long, filters: TransactionFilters): Boolean {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        
        return when (filters.dateRange) {
            DateRange.ALL -> true
            DateRange.TODAY -> {
                isSameDay(timestamp, now)
            }
            DateRange.YESTERDAY -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                isSameDay(timestamp, calendar.timeInMillis)
            }
            DateRange.THIS_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                timestamp >= calendar.timeInMillis
            }
            DateRange.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                timestamp >= calendar.timeInMillis
            }
            DateRange.CUSTOM -> {
                val from = filters.fromDate ?: 0L
                val to = filters.toDate ?: Long.MAX_VALUE
                timestamp in from..to
            }
        }
    }

    private fun isSameDay(t1: Long, t2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun getComparator(sortBy: SortBy): Comparator<Transaction> {
        return when (sortBy) {
            SortBy.DATE_DESC -> compareByDescending { it.dateTime }
            SortBy.DATE_ASC -> compareBy { it.dateTime }
            SortBy.AMOUNT_DESC -> compareByDescending { it.amount }
            SortBy.AMOUNT_ASC -> compareBy { it.amount }
        }
    }

    private fun calculateActiveFilterCount(filters: TransactionFilters): Int {
        var count = 0
        if (filters.title.isNotEmpty()) count++
        if (filters.type != null) count++
        if (filters.categoryId != null) count++
        if (filters.dateRange != DateRange.ALL) count++
        return count
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            deleteTransactionUseCase(transaction)
        }
    }
}

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val modes: List<Mode> = emptyList(),
    val isLoading: Boolean = false,
    val activeFilterCount: Int = 0,
    val selectedIds: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false,
    val error: String? = null
)
