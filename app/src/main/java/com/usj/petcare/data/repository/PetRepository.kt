package com.usj.petcare.data.repository

import com.usj.petcare.data.local.dao.PetDao
import com.usj.petcare.data.local.entity.toEntity
import com.usj.petcare.model.Pet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PetRepository(private val dao: PetDao) {
    fun observePets(): Flow<List<Pet>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    fun observePet(id: Long): Flow<Pet?> =
        dao.observeById(id).map { it?.toDomain() }

    suspend fun getPet(id: Long): Pet? = dao.getById(id)?.toDomain()

    suspend fun savePet(pet: Pet): Long {
        val rowId = dao.upsert(pet.toEntity())

        return if (rowId == -1L) pet.id else rowId
    }

    suspend fun deletePet(id: Long) = dao.deleteById(id)
}
