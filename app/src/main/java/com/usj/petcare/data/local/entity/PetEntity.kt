package com.usj.petcare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.usj.petcare.model.Pet
import com.usj.petcare.model.Species
import java.time.LocalDate

/**
 * One row of the "pets" table. Room cannot store LocalDate or enums directly,
 * so we store simple types and convert in toDomain() / toEntity().
 */
@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val species: String,
    val breed: String?,
    val birthDateEpochDay: Long?,
    val weightKg: Double?,
    val notes: String
) {
    fun toDomain(): Pet = Pet(
        id = id,
        name = name,
        species = Species.fromName(species),
        breed = breed,
        birthDate = birthDateEpochDay?.let { LocalDate.ofEpochDay(it) },
        weightKg = weightKg,
        notes = notes
    )
}

fun Pet.toEntity(): PetEntity = PetEntity(
    id = id,
    name = name,
    species = species.name,
    breed = breed,
    birthDateEpochDay = birthDate?.toEpochDay(),
    weightKg = weightKg,
    notes = notes
)
