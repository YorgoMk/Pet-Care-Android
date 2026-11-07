package com.usj.petcare.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.usj.petcare.data.local.entity.PetEntity
import kotlinx.coroutines.flow.Flow

/** The database operations the app needs for pets. Reads are Flows, writes are suspend functions. */
@Dao
interface PetDao {
    @Query("SELECT * FROM pets ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE id = :id")
    fun observeById(id: Long): Flow<PetEntity?>

    @Query("SELECT * FROM pets WHERE id = :id")
    suspend fun getById(id: Long): PetEntity?

    /** Inserts a new pet (id = 0) or updates an existing one. Returns the row id. */
    @Upsert
    suspend fun upsert(pet: PetEntity): Long

    @Query("DELETE FROM pets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
