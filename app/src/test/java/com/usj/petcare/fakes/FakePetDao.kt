package com.usj.petcare.fakes

import com.usj.petcare.data.local.dao.PetDao
import com.usj.petcare.data.local.entity.PetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory stand-in for Room, so tests run on the computer without an emulator. */
class FakePetDao : PetDao {
    private val rows = MutableStateFlow<List<PetEntity>>(emptyList())
    private var nextId = 1L

    override fun observeAll(): Flow<List<PetEntity>> = rows.map { list -> list.sortedBy { it.name.lowercase() } }

    override fun observeById(id: Long): Flow<PetEntity?> = rows.map { list -> list.find { it.id == id } }

    override suspend fun getById(id: Long): PetEntity? = rows.value.find { it.id == id }

    override suspend fun upsert(pet: PetEntity): Long {
        // Mirrors Room: new rows get an id, updates return -1.
        return if (pet.id == 0L) {
            val saved = pet.copy(id = nextId++)
            rows.value = rows.value + saved
            saved.id
        } else {
            rows.value = rows.value.map { if (it.id == pet.id) pet else it }
            -1L
        }
    }

    override suspend fun deleteById(id: Long) {
        rows.value = rows.value.filterNot { it.id == id }
    }
}
