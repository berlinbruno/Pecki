package dev.berlinbruno.pecki.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.Mode
import dev.berlinbruno.pecki.domain.transactions.usecases.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val restoreDefaultsUseCase: RestoreDefaultsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getModesUseCase: GetModesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val deleteModeUseCase: DeleteModeUseCase,
    private val clearAllDataUseCase: ClearAllDataUseCase
) : ViewModel() {

    val categories = getCategoriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val modes = getModesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun restoreDefaults() {
        viewModelScope.launch {
            restoreDefaultsUseCase.restoreCategories()
            restoreDefaultsUseCase.restoreModes()
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            deleteCategoryUseCase(category)
        }
    }

    fun deleteMode(mode: Mode) {
        viewModelScope.launch {
            deleteModeUseCase(mode)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            clearAllDataUseCase()
        }
    }
}
