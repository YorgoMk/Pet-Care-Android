package com.usj.petcare.ui

import androidx.lifecycle.SavedStateHandle
import com.usj.petcare.data.repository.PetRepository
import com.usj.petcare.fakes.FakePetDao
import com.usj.petcare.model.Pet
import com.usj.petcare.model.Species
import com.usj.petcare.ui.navigation.PetCareDestinations
import com.usj.petcare.ui.view_model.PetFormViewModel
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PetFormViewModelTest {
    private val repository = PetRepository(FakePetDao())
    private val today = { LocalDate.of(2026, 11, 7) }

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun invalidFormShowsErrorsAndSavesNothing() = runTest {
        val viewModel = PetFormViewModel(SavedStateHandle(), repository, today)
        viewModel.onWeightChange("abc")
        viewModel.save()

        val state = viewModel.uiState.value
        assertEquals("Please enter a name", state.errors.name)
        assertFalse(state.isSaved)
        assertTrue(repository.observePets().first().isEmpty())
    }

    @Test
    fun validFormIsSavedToTheRepository() = runTest {
        val viewModel = PetFormViewModel(SavedStateHandle(), repository, today)
        viewModel.onNameChange("  Luna ")
        viewModel.onSpeciesChange(Species.DOG)
        viewModel.onWeightChange("28,5")
        viewModel.save()

        assertTrue(viewModel.uiState.value.isSaved)
        val saved = repository.observePets().first().single()
        assertEquals("Luna", saved.name)
        assertEquals(28.5, saved.weightKg!!, 0.0)
    }

    @Test
    fun typingClearsTheFieldError() = runTest {
        val viewModel = PetFormViewModel(SavedStateHandle(), repository, today)
        viewModel.save()
        viewModel.onNameChange("L")
        assertEquals(null, viewModel.uiState.value.errors.name)
    }

    @Test
    fun editModeLoadsTheExistingPet() = runTest {
        val id = repository.savePet(Pet(name = "Milo", species = Species.CAT, weightKg = 4.0))
        val viewModel = PetFormViewModel(
            SavedStateHandle(mapOf(PetCareDestinations.PET_ID_ARG to id)), repository, today
        )

        val state = viewModel.uiState.value
        assertTrue(state.isEditing)
        assertEquals("Milo", state.name)
        assertEquals(Species.CAT, state.species)
        assertEquals("4", state.weightText)
    }
}
