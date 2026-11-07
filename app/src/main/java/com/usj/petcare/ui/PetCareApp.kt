package com.usj.petcare.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.usj.petcare.ui.navigation.PetCareNavHost

/** Root composable: creates the one NavController and hands it to the NavHost. */
@Composable
fun PetCareApp() {
    val navController = rememberNavController()
    PetCareNavHost(navController = navController)
}
