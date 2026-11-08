package com.usj.petcare.data.repository

import com.usj.petcare.data.remote.DogApiService
import com.usj.petcare.model.Breed

/**
 * Calls the Dog API and maps each BreedDto (JSON shape) to a Breed (what the UI shows).
 * Network errors are not caught here: the ViewModel decides what message to show.
 */
class BreedRepository(
    private val api: DogApiService
) {

    suspend fun searchBreeds(query: String): List<Breed> {
        return api.searchBreeds(query).map { dto ->
            Breed(
                id = dto.id.toString(),
                name = dto.name,
                temperament = dto.temperament,
                lifeSpan = formatLifeSpan(dto.lifeSpan),
                breedGroup = dto.breedGroup,
                imageUrl = dto.image?.url
            )
        }
    }

    /** The API sends "10 - 12 years" for some breeds and "10-12" for others; always show the unit. */
    private fun formatLifeSpan(raw: String?): String? {
        val text = raw?.trim().orEmpty()
        if (text.isEmpty()) return null
        return if (text.contains("year", ignoreCase = true)) text else "$text years"
    }
}
