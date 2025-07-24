package com.example.awarely.data.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.awarely.model.SessionEntity

@Database(entities = [SessionEntity::class], version = 2, exportSchema = false)  // Incremented version
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "awarely_database"
                )
                    .fallbackToDestructiveMigration()  // Add this to handle schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}