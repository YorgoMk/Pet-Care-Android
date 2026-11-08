
package com.usj.petcare.data.repository

import com.usj.petcare.data.remote.DogApiService
import com.usj.petcare.model.Breed

class BreedRepository(
    private val api: DogApiService
) {

    suspend fun searchBreeds(query: String): List<Breed> {
        return api.searchBreeds(query).map { dto ->
            Breed(
                id = dto.id.toString(),
                name = dto.name,
                temperament = dto.temperament,
                lifeSpan = dto.lifeSpan,
                breedGroup = dto.breedGroup,
                imageUrl = dto.image?.url
            )
        }
    }
}
