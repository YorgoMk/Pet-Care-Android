package com.usj.petcare.data

import android.content.Context
import com.usj.petcare.data.local.AppDatabase
import com.usj.petcare.data.repository.PetRepository

/**
 * Manual dependency container: builds each data object once and hands it to
 * the ViewModels. No dependency-injection library is needed at this size.
 */
class AppContainer(context: Context) {
    private val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }

    val petRepository: PetRepository by lazy { PetRepository(database.petDao()) }
}
