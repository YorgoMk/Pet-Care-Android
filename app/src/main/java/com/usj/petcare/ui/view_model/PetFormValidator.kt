package com.usj.petcare.ui.view_model

import java.time.LocalDate

data class PetFormErrors(
    val name: String? = null,
    val weight: String? = null,
    val birthDate: String? = null
) {
    val hasErrors: Boolean get() = name != null || weight != null || birthDate != null
}

object PetFormValidator {
    const val MAX_NAME_LENGTH = 30
    const val MAX_WEIGHT_KG = 150.0

    fun validate(name: String, weightText: String, birthDate: LocalDate?, today: LocalDate): PetFormErrors {
        val trimmedName = name.trim()
        val nameError = when {
            trimmedName.isEmpty() -> "Please enter a name"
            trimmedName.length > MAX_NAME_LENGTH -> "Name must be $MAX_NAME_LENGTH characters or fewer"
            else -> null
        }
        val weightError = if (weightText.isBlank()) null else {
            val weight = parseWeight(weightText)
            when {
                weight == null -> "Enter a number, for example 4.5"
                weight <= 0 -> "Weight must be more than 0"
                weight > MAX_WEIGHT_KG -> "Weight must be $MAX_WEIGHT_KG kg or less"
                else -> null
            }
        }
        val birthDateError = if (birthDate != null && birthDate.isAfter(today)) {
            "Birth date cannot be in the future"
        } else null
        return PetFormErrors(name = nameError, weight = weightError, birthDate = birthDateError)
    }

    fun parseWeight(text: String): Double? = text.trim().replace(',', '.').toDoubleOrNull()
}
