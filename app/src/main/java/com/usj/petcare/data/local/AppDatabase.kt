package com.usj.petcare.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.usj.petcare.data.local.dao.PetDao
import com.usj.petcare.data.local.entity.PetEntity

/**
 * The single Room database of the app. New tables (care tasks, logs) will be
 * added to `entities` and the version increased when they arrive.
 */
@Database(entities = [PetEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pet_care.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
