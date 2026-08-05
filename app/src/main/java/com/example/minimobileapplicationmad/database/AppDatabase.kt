package com.example.minimobileapplicationmad.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.minimobileapplicationmad.database.dao.FileDao
import com.example.minimobileapplicationmad.database.dao.VersionDao
import com.example.minimobileapplicationmad.database.entities.FileEntity
import com.example.minimobileapplicationmad.database.entities.VersionEntity

@Database(entities = [FileEntity::class, VersionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDao
    abstract fun versionDao(): VersionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
