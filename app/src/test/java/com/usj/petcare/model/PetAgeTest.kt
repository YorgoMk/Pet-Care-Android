package com.usj.petcare.model

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class PetAgeTest {
    private val today = LocalDate.of(2026, 11, 7)

    @Test
    fun yearsAndMonths() = assertEquals("2 years 3 months", PetAge.describe(LocalDate.of(2024, 8, 1), today))

    @Test
    fun exactlyOneYear() = assertEquals("1 year", PetAge.describe(LocalDate.of(2025, 11, 7), today))

    @Test
    fun youngPetShowsWeeks() = assertEquals("3 weeks", PetAge.describe(LocalDate.of(2026, 10, 17), today))

    @Test
    fun bornToday() = assertEquals("0 days", PetAge.describe(today, today))

    @Test
    fun futureDate() = assertEquals("Not born yet", PetAge.describe(today.plusDays(1), today))
}
