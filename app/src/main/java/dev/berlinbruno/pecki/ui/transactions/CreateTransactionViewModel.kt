package dev.berlinbruno.pecki.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.berlinbruno.pecki.domain.transactions.models.*
import dev.berlinbruno.pecki.domain.transactions.usecases.CreateTransactionUseCase
import dev.berlinbruno.pecki.domain.transactions.usecases.GetCategoriesUseCase
import dev.berlinbruno.pecki.domain.transactions.usecases.GetModesUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateTransactionViewModel @Inject constructor(
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getModesUseCase: GetModesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTransactionUiState())
    val uiState = _uiState.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val categories = _uiState.flatMapLatest { state ->
        getCategoriesUseCase(state.type)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val modes = getModesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
        suggestCategory(title)
    }

    private fun suggestCategory(title: String) {
        if (title.isBlank()) return
        
        viewModelScope.launch {
            val currentCategories = categories.value
            val match = currentCategories.find { category ->
                category.keywords.any { keyword -> 
                    title.contains(keyword, ignoreCase = true) 
                }
            }
            
            if (match != null) {
                _uiState.update { it.copy(categoryId = match.id) }
            }
        }
    }

    fun onAmountChange(amount: String) {
        // Only allow digits and a single decimal point with up to two decimal places
        if (amount.isEmpty() || amount.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
            val amountDouble = amount.toDoubleOrNull() ?: 0.0
            if (amountDouble <= 1_000_000_000.0) {
                _uiState.update { it.copy(amount = amount, amountError = null) }
            }
        }
    }

    fun onTypeChange(type: TransactionType) {
        val defaultCategory = if (type == TransactionType.DEBIT) "debit_other" else "credit_other"
        _uiState.update { it.copy(type = type, categoryId = defaultCategory) }
    }

    fun onDateChange(date: Long) {
        _uiState.update { it.copy(date = date) }
    }

    fun onModeChange(modeId: String) {
        _uiState.update { it.copy(modeId = modeId) }
    }

    fun onCategoryChange(categoryId: String) {
        _uiState.update { it.copy(categoryId = categoryId) }
    }

    fun onNoteChange(note: String) {
        if (note.length <= 200) {
            _uiState.update { it.copy(note = note) }
        }
    }

    fun resetState() {
        val state = _uiState.value
        if (state.isEdit && state.originalTransaction != null) {
            val original = state.originalTransaction
            _uiState.value = state.copy(
                title = original.title,
                amount = original.amount.toString(),
                type = original.type,
                date = original.dateTime,
                modeId = original.modeId,
                categoryId = original.categoryId,
                note = original.note ?: "",
                titleError = null,
                amountError = null
            )
        } else {
            _uiState.value = CreateTransactionUiState()
        }
    }

    fun loadTransaction(transaction: Transaction) {
        _uiState.value = CreateTransactionUiState(
            title = transaction.title,
            amount = transaction.amount.toString(),
            type = transaction.type,
            date = transaction.dateTime,
            modeId = transaction.modeId,
            categoryId = transaction.categoryId,
            note = transaction.note ?: "",
            isEdit = true,
            editId = transaction.id,
            originalTransaction = transaction
        )
    }

    fun saveTransaction() {
        val state = _uiState.value
        val titleError = if (state.title.isBlank()) "Title cannot be empty" else null
        val amountDouble = state.amount.toDoubleOrNull()
        val amountError = when {
            state.amount.isBlank() -> "Amount cannot be empty"
            amountDouble == null || amountDouble <= 0 -> "Enter a valid positive amount"
            amountDouble > 1_000_000_000.0 -> "Amount cannot exceed 1 billion"
            else -> null
        }

        if (titleError != null || amountError != null) {
            _uiState.update { it.copy(titleError = titleError, amountError = amountError) }
            return
        }

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val transaction = Transaction(
                id = if (state.isEdit) state.editId!! else UUID.randomUUID().toString(),
                title = state.title,
                type = state.type,
                amount = amountDouble!!,
                currency = "€",
                dateTime = state.date,
                merchant = "", 
                categoryId = state.categoryId,
                accountId = null,
                note = if (state.note.isBlank()) null else state.note,
                referenceId = null,
                modeId = state.modeId,
                status = TransactionStatus.APPROVED,
                source = TransactionSource.MANUAL,
                hashValue = true,
                createdAt = if (state.isEdit) state.originalTransaction!!.createdAt else now,
                updatedAt = now
            )
            createTransactionUseCase(transaction)
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}

data class CreateTransactionUiState(
    val title: String = "",
    val amount: String = "",
    val type: TransactionType = TransactionType.DEBIT,
    val date: Long = System.currentTimeMillis(),
    val modeId: String? = "other",
    val categoryId: String? = "debit_other",
    val note: String = "",
    val titleError: String? = null,
    val amountError: String? = null,
    val isEdit: Boolean = false,
    val editId: String? = null,
    val originalTransaction: Transaction? = null,
    val isSaved: Boolean = false
)
