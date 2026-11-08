package com.usj.petcare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.usj.petcare.ui.breeds.BreedGuideRoute
import com.usj.petcare.ui.pets.PetDetailRoute
import com.usj.petcare.ui.pets.PetFormRoute
import com.usj.petcare.ui.pets.PetListRoute

/** Route names in one place so screens never build route strings themselves. */
object PetCareDestinations {

    const val PET_ID_ARG = "petId"

    const val PET_LIST = "pets"
    const val PET_ADD = "pets/add"
    const val PET_DETAIL = "pets/{$PET_ID_ARG}"
    const val PET_EDIT = "pets/{$PET_ID_ARG}/edit"
    const val BREED_GUIDE = "breed-guide"

    fun petDetail(petId: Long) = "pets/$petId"

    fun petEdit(petId: Long) = "pets/$petId/edit"
}

/**
 * The navigation graph. Screens only receive callbacks ("the user wants to
 * open X"); this is the only place that calls navigate() or popBackStack().
 */
@Composable
fun PetCareNavHost(navController: NavHostController) {

    val petIdArgument = listOf(
        navArgument(PetCareDestinations.PET_ID_ARG) {
            type = NavType.LongType
        }
    )

    NavHost(
        navController = navController,
        startDestination = PetCareDestinations.PET_LIST
    ) {

        // My Pets screen
        composable(PetCareDestinations.PET_LIST) {
            PetListRoute(
                onAddPet = {
                    navController.navigate(PetCareDestinations.PET_ADD)
                },
                onOpenPet = { petId ->
                    navController.navigate(
                        PetCareDestinations.petDetail(petId)
                    )
                },
                onOpenBreedGuide = {
                    navController.navigate(PetCareDestinations.BREED_GUIDE)
                }
            )
        }

        // Breed Guide screen
        composable(PetCareDestinations.BREED_GUIDE) {
            BreedGuideRoute(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Add Pet screen
        composable(PetCareDestinations.PET_ADD) {
            PetFormRoute(
                onBack = {
                    navController.popBackStack()
                },
                onSaved = {
                    navController.popBackStack()
                }
            )
        }

        // Pet Details screen
        composable(
            PetCareDestinations.PET_DETAIL,
            arguments = petIdArgument
        ) { entry ->

            val petId = entry.arguments?.getLong(
                PetCareDestinations.PET_ID_ARG
            ) ?: 0L

            PetDetailRoute(
                onBack = {
                    navController.popBackStack()
                },
                onEdit = {
                    navController.navigate(
                        PetCareDestinations.petEdit(petId)
                    )
                }
            )
        }

        // Edit Pet screen
        composable(
            PetCareDestinations.PET_EDIT,
            arguments = petIdArgument
        ) {
            PetFormRoute(
                onBack = {
                    navController.popBackStack()
                },
                onSaved = {
                    navController.popBackStack()
                }
            )
        }
    }
}
