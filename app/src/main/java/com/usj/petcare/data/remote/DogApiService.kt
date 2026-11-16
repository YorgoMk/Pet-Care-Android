package com.usj.petcare.data.remote

import com.usj.petcare.data.remote.dto.BreedDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DogApiService {
    @GET("breeds/search")
    suspend fun searchBreeds(
        @Query("q") query: String
    ): List<BreedDto>
}
