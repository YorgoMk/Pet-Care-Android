package com.usj.petcare.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Teal40,
    primaryContainer = Teal90,
    secondary = Orange40,
    secondaryContainer = Orange90,
    background = Sand99,
    surface = Sand99
)

private val DarkColors = darkColorScheme(
    primary = Teal80,
    primaryContainer = Teal30,
    secondary = Orange80,
    secondaryContainer = Orange30
)

@Composable
fun PetCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
