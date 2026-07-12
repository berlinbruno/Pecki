package dev.berlinbruno.pecki.ui.transactions.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.berlinbruno.pecki.domain.transactions.models.Mode
import dev.berlinbruno.pecki.domain.transactions.usecases.AddModeUseCase
import dev.berlinbruno.pecki.domain.transactions.usecases.DeleteModeUseCase
import dev.berlinbruno.pecki.domain.transactions.usecases.GetModesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ModeManagementViewModel @Inject constructor(
    private val getModesUseCase: GetModesUseCase,
    private val addModeUseCase: AddModeUseCase,
    private val deleteModeUseCase: DeleteModeUseCase
) : ViewModel() {

    companion object {
        const val MAX_MODES = 15
    }

    val modes = getModesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(ModeManagementUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        if (name.length <= 25) {
            _uiState.update { it.copy(name = name, nameError = null) }
        }
    }

    fun resetState() {
        _uiState.value = ModeManagementUiState()
    }

    fun loadMode(mode: Mode) {
        _uiState.value = ModeManagementUiState(
            name = mode.name,
            isEdit = true,
            editId = mode.id
        )
    }

    fun saveMode() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Name cannot be empty") }
            return
        }

        if (!state.isEdit && modes.value.size >= MAX_MODES) {
            _uiState.update { it.copy(nameError = "Maximum limit of $MAX_MODES modes reached") }
            return
        }

        viewModelScope.launch {
            val mode = Mode(
                id = if (state.isEdit) state.editId!! else UUID.randomUUID().toString(),
                name = state.name,
                icon = null,
                isSystem = false
            )
            addModeUseCase(mode)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun deleteMode(mode: Mode) {
        viewModelScope.launch {
            deleteModeUseCase(mode)
        }
    }

    fun clearSaved() {
        _uiState.update { it.copy(isSaved = false) }
    }
}

data class ModeManagementUiState(
    val name: String = "",
    val nameError: String? = null,
    val isEdit: Boolean = false,
    val editId: String? = null,
    val isSaved: Boolean = false
)
