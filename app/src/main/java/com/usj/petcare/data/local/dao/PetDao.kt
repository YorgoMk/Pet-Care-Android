package com.usj.petcare.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.usj.petcare.data.local.entity.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pets ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE id = :id")
    fun observeById(id: Long): Flow<PetEntity?>

    @Query("SELECT * FROM pets WHERE id = :id")
    suspend fun getById(id: Long): PetEntity?

    @Upsert
    suspend fun upsert(pet: PetEntity): Long

    @Query("DELETE FROM pets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
