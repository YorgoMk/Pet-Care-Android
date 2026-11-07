package com.usj.petcare.model

import java.time.LocalDate
import java.time.Period

/** Turns a birth date into a short label such as "2 years 3 months" or "5 weeks". */
object PetAge {
    fun describe(birthDate: LocalDate, today: LocalDate): String {
        if (birthDate.isAfter(today)) return "Not born yet"
        val period = Period.between(birthDate, today)
        return when {
            period.years > 0 && period.months > 0 ->
                "${plural(period.years, "year")} ${plural(period.months, "month")}"
            period.years > 0 -> plural(period.years, "year")
            period.months > 0 -> plural(period.months, "month")
            else -> {
                val weeks = period.days / 7
                if (weeks > 0) plural(weeks, "week") else plural(period.days, "day")
            }
        }
    }

    private fun plural(count: Int, unit: String) = if (count == 1) "1 $unit" else "$count ${unit}s"
}
