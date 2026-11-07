package com.usj.petcare.ui.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usj.petcare.data.repository.PetRepository
import com.usj.petcare.model.Pet
import com.usj.petcare.ui.navigation.PetCareDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PetDetailUiState {
    data object Loading : PetDetailUiState
    data class Content(val pet: Pet) : PetDetailUiState
    data object NotFound : PetDetailUiState
    /** Set after the user confirms delete, so the screen knows to go back. */
    data object Deleted : PetDetailUiState
}

class PetDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PetRepository
) : ViewModel() {

    private val petId: Long = checkNotNull(savedStateHandle[PetCareDestinations.PET_ID_ARG])
    private val deleted = MutableStateFlow(false)

    val uiState: StateFlow<PetDetailUiState> =
        combine(repository.observePet(petId), deleted) { pet, isDeleted ->
            when {
                isDeleted -> PetDetailUiState.Deleted
                pet == null -> PetDetailUiState.NotFound
                else -> PetDetailUiState.Content(pet)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PetDetailUiState.Loading
        )

    fun deletePet() {
        // Mark as deleted first so the screen never flashes "not found".
        deleted.value = true
        viewModelScope.launch { repository.deletePet(petId) }
    }
}
