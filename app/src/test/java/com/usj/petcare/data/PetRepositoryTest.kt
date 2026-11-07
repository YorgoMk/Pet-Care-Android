package com.usj.petcare.data

import com.usj.petcare.data.repository.PetRepository
import com.usj.petcare.fakes.FakePetDao
import com.usj.petcare.model.Pet
import com.usj.petcare.model.Species
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PetRepositoryTest {
    private val repository = PetRepository(FakePetDao())

    @Test
    fun savedPetIsReadBackWithAllFields() = runTest {
        val pet = Pet(name = "Luna", species = Species.DOG, breed = "Golden Retriever",
            birthDate = LocalDate.of(2022, 3, 14), weightKg = 28.5, notes = "Loves walks")
        val id = repository.savePet(pet)
        assertEquals(pet.copy(id = id), repository.getPet(id))
    }

    @Test
    fun editingKeepsTheSameId() = runTest {
        val id = repository.savePet(Pet(name = "Milo", species = Species.CAT))
        val returnedId = repository.savePet(Pet(id = id, name = "Milo Jr", species = Species.CAT))
        assertEquals(id, returnedId)
        assertEquals("Milo Jr", repository.getPet(id)?.name)
    }

    @Test
    fun petsAreListedAlphabetically() = runTest {
        repository.savePet(Pet(name = "zoe", species = Species.CAT))
        repository.savePet(Pet(name = "Bella", species = Species.DOG))
        assertEquals(listOf("Bella", "zoe"), repository.observePets().first().map { it.name })
    }

    @Test
    fun deletedPetIsGone() = runTest {
        val id = repository.savePet(Pet(name = "Kiwi", species = Species.BIRD))
        repository.deletePet(id)
        assertNull(repository.getPet(id))
    }
}
