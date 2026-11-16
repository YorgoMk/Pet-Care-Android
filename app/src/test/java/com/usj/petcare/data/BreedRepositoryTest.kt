package com.usj.petcare.data

import com.usj.petcare.data.remote.dto.BreedDto
import com.usj.petcare.data.remote.dto.BreedImageDto
import com.usj.petcare.data.repository.BreedRepository
import com.usj.petcare.fakes.FakeDogApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BreedRepositoryTest {
    @Test
    fun mapsDtoFieldsToBreed() = runTest {
        val api = FakeDogApiService(
            listOf(
                BreedDto(
                    id = 10, name = "Golden Retriever", temperament = "Friendly",
                    lifeSpan = "10 - 12 years", breedGroup = "Sporting",
                    image = BreedImageDto(url = "https://example.com/golden.jpg")
                )
            )
        )
        val breed = BreedRepository(api).searchBreeds("golden").single()

        assertEquals("10", breed.id)
        assertEquals("Golden Retriever", breed.name)
        assertEquals("Friendly", breed.temperament)
        assertEquals("10 - 12 years", breed.lifeSpan)
        assertEquals("Sporting", breed.breedGroup)
        assertEquals("https://example.com/golden.jpg", breed.imageUrl)
    }

    @Test
    fun missingOptionalFieldsStayNull() = runTest {
        val breed = BreedRepository(FakeDogApiService(listOf(BreedDto(id = 1, name = "Akita"))))
            .searchBreeds("akita").single()
        assertNull(breed.temperament)
        assertNull(breed.imageUrl)
    }

    @Test
    fun lifeSpanAlwaysHasAUnit() = runTest {
        val api = FakeDogApiService(listOf(BreedDto(id = 1, name = "Akita", lifeSpan = "10-12")))
        assertEquals("10-12 years", BreedRepository(api).searchBreeds("akita").single().lifeSpan)
    }

    @Test
    fun passesTheQueryToTheApi() = runTest {
        val api = FakeDogApiService()
        BreedRepository(api).searchBreeds("beagle")
        assertEquals(listOf("beagle"), api.queries)
    }
}
