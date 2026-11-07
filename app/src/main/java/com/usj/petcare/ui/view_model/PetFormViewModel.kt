package com.usj.petcare.ui.view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usj.petcare.data.repository.PetRepository
import com.usj.petcare.model.Pet
import com.usj.petcare.model.Species
import com.usj.petcare.ui.navigation.PetCareDestinations
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Everything the add/edit form shows, including validation messages. */
data class PetFormUiState(
    val name: String = "",
    val species: Species = Species.DOG,
    val breed: String = "",
    val birthDate: LocalDate? = null,
    val weightText: String = "",
    val notes: String = "",
    val errors: PetFormErrors = PetFormErrors(),
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

class PetFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PetRepository,
    private val today: () -> LocalDate = { LocalDate.now() }
) : ViewModel() {

    /** null when adding a new pet; the pet's id when editing. */
    private val petId: Long? = savedStateHandle[PetCareDestinations.PET_ID_ARG]

    private val _uiState = MutableStateFlow(PetFormUiState(isEditing = petId != null, isLoading = petId != null))
    val uiState: StateFlow<PetFormUiState> = _uiState.asStateFlow()

    init {
        if (petId != null) loadPet(petId)
    }

    private fun loadPet(id: Long) {
        viewModelScope.launch {
            val pet = repository.getPet(id)
            _uiState.update { state ->
                if (pet == null) state.copy(isLoading = false)
                else state.copy(
                    name = pet.name,
                    species = pet.species,
                    breed = pet.breed.orEmpty(),
                    birthDate = pet.birthDate,
                    weightText = pet.weightKg?.let { formatWeight(it) }.orEmpty(),
                    notes = pet.notes,
                    isLoading = false
                )
            }
        }
    }

    // Each field change clears that field's error so the message disappears while typing.
    fun onNameChange(value: String) =
        _uiState.update { it.copy(name = value, errors = it.errors.copy(name = null)) }

    fun onSpeciesChange(value: Species) = _uiState.update { it.copy(species = value) }

    fun onBreedChange(value: String) = _uiState.update { it.copy(breed = value) }

    fun onBirthDateChange(value: LocalDate?) =
        _uiState.update { it.copy(birthDate = value, errors = it.errors.copy(birthDate = null)) }

    fun onWeightChange(value: String) =
        _uiState.update { it.copy(weightText = value, errors = it.errors.copy(weight = null)) }

    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun save() {
        val state = _uiState.value
        val errors = PetFormValidator.validate(state.name, state.weightText, state.birthDate, today())
        if (errors.hasErrors) {
            _uiState.update { it.copy(errors = errors) }
            return
        }
        val pet = Pet(
            id = petId ?: 0,
            name = state.name.trim(),
            species = state.species,
            breed = state.breed.trim().ifEmpty { null },
            birthDate = state.birthDate,
            weightKg = PetFormValidator.parseWeight(state.weightText),
            notes = state.notes.trim()
        )
        viewModelScope.launch {
            repository.savePet(pet)
            // The screen watches isSaved and navigates back once the write is finished.
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    private fun formatWeight(kg: Double): String =
        if (kg % 1.0 == 0.0) kg.toInt().toString() else kg.toString()
}
