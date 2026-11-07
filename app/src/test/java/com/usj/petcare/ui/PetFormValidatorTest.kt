package com.usj.petcare.ui

import com.usj.petcare.ui.view_model.PetFormValidator
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PetFormValidatorTest {
    private val today = LocalDate.of(2026, 11, 7)

    @Test
    fun validInputHasNoErrors() {
        val errors = PetFormValidator.validate("Luna", "28.5", LocalDate.of(2022, 3, 14), today)
        assertFalse(errors.hasErrors)
    }

    @Test
    fun blankNameIsRejected() {
        val errors = PetFormValidator.validate("   ", "", null, today)
        assertEquals("Please enter a name", errors.name)
    }

    @Test
    fun tooLongNameIsRejected() {
        val errors = PetFormValidator.validate("a".repeat(31), "", null, today)
        assertTrue(errors.name!!.contains("30"))
    }

    @Test
    fun weightIsOptional() = assertNull(PetFormValidator.validate("Luna", "", null, today).weight)

    @Test
    fun nonNumericWeightIsRejected() =
        assertEquals("Enter a number, for example 4.5", PetFormValidator.validate("Luna", "abc", null, today).weight)

    @Test
    fun zeroAndHugeWeightsAreRejected() {
        assertEquals("Weight must be more than 0", PetFormValidator.validate("Luna", "0", null, today).weight)
        assertTrue(PetFormValidator.validate("Luna", "500", null, today).weight != null)
    }

    @Test
    fun commaDecimalIsAccepted() = assertEquals(4.5, PetFormValidator.parseWeight("4,5")!!, 0.0)

    @Test
    fun futureBirthDateIsRejected() {
        val errors = PetFormValidator.validate("Luna", "", today.plusDays(1), today)
        assertEquals("Birth date cannot be in the future", errors.birthDate)
    }
}
