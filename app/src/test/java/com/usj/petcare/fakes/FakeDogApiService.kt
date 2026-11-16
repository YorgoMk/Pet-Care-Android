package com.usj.petcare.fakes

import com.usj.petcare.data.remote.DogApiService
import com.usj.petcare.data.remote.dto.BreedDto

class FakeDogApiService(var breeds: List<BreedDto> = emptyList()) : DogApiService {
    var error: Exception? = null
    val queries = mutableListOf<String>()

    override suspend fun searchBreeds(query: String): List<BreedDto> {
        queries += query
        error?.let { throw it }
        return breeds.filter { it.name.contains(query, ignoreCase = true) }
    }
}
