
package com.usj.petcare.ui.view_model

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.usj.petcare.PetCareApplication

object AppViewModelProvider {

    val Factory = viewModelFactory {

        initializer {
            PetListViewModel(
                petCareApplication().container.petRepository
            )
        }

        initializer {
            PetDetailViewModel(
                createSavedStateHandle(),
                petCareApplication().container.petRepository
            )
        }

        initializer {
            PetFormViewModel(
                createSavedStateHandle(),
                petCareApplication().container.petRepository
            )
        }

        initializer {
            BreedViewModel(
                petCareApplication().container.breedRepository
            )
        }
    }
}

fun CreationExtras.petCareApplication(): PetCareApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
            as PetCareApplication
