package com.usj.petcare.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usj.petcare.data.repository.PetRepository
import com.usj.petcare.model.Pet
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** The three things the "My pets" screen can show. */
sealed interface PetListUiState {
    data object Loading : PetListUiState
    data object Empty : PetListUiState
    data class Content(val pets: List<Pet>) : PetListUiState
}

class PetListViewModel(repository: PetRepository) : ViewModel() {

    /** Room emits a new list after every insert, edit or delete, so the screen updates by itself. */
    val uiState: StateFlow<PetListUiState> = repository.observePets()
        .map { pets -> if (pets.isEmpty()) PetListUiState.Empty else PetListUiState.Content(pets) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PetListUiState.Loading
        )
}
