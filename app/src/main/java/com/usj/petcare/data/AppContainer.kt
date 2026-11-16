package com.usj.petcare.data

import android.content.Context
import com.usj.petcare.data.local.AppDatabase
import com.usj.petcare.data.remote.DogApiClient
import com.usj.petcare.data.repository.BreedRepository
import com.usj.petcare.data.repository.PetRepository

class AppContainer(context: Context) {
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    val petRepository: PetRepository by lazy {
        PetRepository(database.petDao())
    }

    val breedRepository: BreedRepository by lazy {
        BreedRepository(DogApiClient.service)
    }
}
