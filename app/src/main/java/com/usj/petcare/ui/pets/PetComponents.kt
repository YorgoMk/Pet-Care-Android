package com.usj.petcare.ui.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usj.petcare.model.Pet
import com.usj.petcare.model.Species
import java.time.format.DateTimeFormatter

/** Round avatar showing the species emoji. Reused by the list and the detail screen. */
@Composable
fun SpeciesAvatar(species: Species, modifier: Modifier = Modifier, size: Dp = 48.dp) {
    Box(
        modifier = modifier
            .size(size)
            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = species.emoji, fontSize = (size.value * 0.5f).sp)
    }
}

/** "Dog · Golden Retriever", or just "Dog" when no breed was entered. */
fun Pet.speciesAndBreed(): String =
    if (breed.isNullOrBlank()) species.label else "${species.label} · $breed"

val BirthDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
