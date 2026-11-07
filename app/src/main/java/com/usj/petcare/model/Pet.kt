package com.usj.petcare.model

import java.time.LocalDate

/** The pet as the rest of the app sees it (UI and ViewModels never see Room classes). */
data class Pet(
    val id: Long = 0,
    val name: String,
    val species: Species,
    val breed: String? = null,
    val birthDate: LocalDate? = null,
    val weightKg: Double? = null,
    val notes: String = ""
)

enum class Species(val label: String, val emoji: String) {
    DOG("Dog", "🐶"),
    CAT("Cat", "🐱"),
    BIRD("Bird", "🐦"),
    RABBIT("Rabbit", "🐰"),
    OTHER("Other", "🐾");

    companion object {
        /** Unknown values from storage fall back to OTHER instead of crashing. */
        fun fromName(name: String): Species = entries.firstOrNull { it.name == name } ?: OTHER
    }
}
