package dev.berlinbruno.pecki.ui.transactions.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType
import dev.berlinbruno.pecki.domain.transactions.usecases.AddCategoryUseCase
import dev.berlinbruno.pecki.domain.transactions.usecases.DeleteCategoryUseCase
import dev.berlinbruno.pecki.domain.transactions.usecases.GetCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    companion object {
        const val MAX_CATEGORIES = 40
    }

    val categories = getCategoriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(CategoryManagementUiState())
    val uiState = _uiState.asStateFlow()

    val presetColors = listOf(
        0xFFFF6B6B.toInt(), 0xFFFFA94D.toInt(), 0xFFFFD43B.toInt(), 0xFF6BCB77.toInt(), 0xFF4D96FF.toInt(),
        0xFF845EC2.toInt(), 0xFFFF9671.toInt(), 0xFFFF6B9D.toInt(), 0xFF4ECDC4.toInt(), 0xFFA8E6CF.toInt(),
        0xFF20C997.toInt(), 0xFF15AABF.toInt(), 0xFFF783AC.toInt(), 0xFF32CD32.toInt(), 0xFF40E0D0.toInt(),
        0xFFDA70D6.toInt(), 0xFFFFA500.toInt(), 0xFF9370DB.toInt(), 0xFF20B2AA.toInt(), 0xFFFF7F50.toInt(),
        0xFF6495ED.toInt(), 0xFF868E96.toInt(), 0xFF45B7D1.toInt(), 0xFF96CEB4.toInt(), 0xFFFFEEAD.toInt()
    )

    fun onNameChange(name: String) {
        if (name.length <= 25) {
            _uiState.update { it.copy(name = name, nameError = null) }
        }
    }

    fun onTypeChange(type: TransactionType?) {
        _uiState.update { it.copy(type = type) }
    }

    fun onColorChange(color: Int) {
        _uiState.update { it.copy(color = color) }
    }

    fun onKeywordsChange(keywords: String) {
        _uiState.update { it.copy(keywords = keywords) }
    }

    fun resetState() {
        _uiState.value = CategoryManagementUiState()
    }

    fun loadCategory(category: Category) {
        _uiState.value = CategoryManagementUiState(
            name = category.name,
            type = category.type,
            color = category.color,
            keywords = category.keywords.joinToString(", "),
            isEdit = true,
            editId = category.id
        )
    }

    fun saveCategory() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Name cannot be empty") }
            return
        }

        if (!state.isEdit && categories.value.size >= MAX_CATEGORIES) {
            _uiState.update { it.copy(nameError = "Maximum limit of $MAX_CATEGORIES categories reached") }
            return
        }

        viewModelScope.launch {
            val category = Category(
                id = if (state.isEdit) state.editId!! else UUID.randomUUID().toString(),
                name = state.name,
                type = state.type,
                icon = null,
                color = state.color ?: presetColors.first(),
                keywords = state.keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                isSystem = false
            )
            addCategoryUseCase(category)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            deleteCategoryUseCase(category)
        }
    }

    fun clearSaved() {
        _uiState.update { it.copy(isSaved = false) }
    }
}

data class CategoryManagementUiState(
    val name: String = "",
    val nameError: String? = null,
    val type: TransactionType? = TransactionType.DEBIT,
    val color: Int? = null,
    val keywords: String = "",
    val isEdit: Boolean = false,
    val editId: String? = null,
    val isSaved: Boolean = false
)
