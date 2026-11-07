package com.usj.petcare.data.repository

import com.usj.petcare.data.local.dao.PetDao
import com.usj.petcare.data.local.entity.toEntity
import com.usj.petcare.model.Pet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The one place ViewModels ask for pet data. It hides Room and converts
 * database rows into [Pet] objects.
 */
class PetRepository(private val dao: PetDao) {

    fun observePets(): Flow<List<Pet>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    fun observePet(id: Long): Flow<Pet?> =
        dao.observeById(id).map { it?.toDomain() }

    suspend fun getPet(id: Long): Pet? = dao.getById(id)?.toDomain()

    /** Returns the id of the saved pet (useful right after creating one). */
    suspend fun savePet(pet: Pet): Long {
        val rowId = dao.upsert(pet.toEntity())
        // Room returns -1 when @Upsert updates an existing row instead of inserting.
        return if (rowId == -1L) pet.id else rowId
    }

    suspend fun deletePet(id: Long) = dao.deleteById(id)
}
