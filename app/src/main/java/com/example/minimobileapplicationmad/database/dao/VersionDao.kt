package com.example.minimobileapplicationmad.database.dao

import androidx.room.*
import com.example.minimobileapplicationmad.database.entities.VersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VersionDao {
    @Query("SELECT * FROM versions WHERE fileId = :fileId ORDER BY timestamp DESC")
    fun getVersionsForFile(fileId: Long): Flow<List<VersionEntity>>

    @Query("SELECT * FROM versions WHERE fileId = :fileId ORDER BY timestamp ASC")
    suspend fun getVersionsForFileSync(fileId: Long): List<VersionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: VersionEntity): Long

    @Delete
    suspend fun deleteVersion(version: VersionEntity)

    @Query("DELETE FROM versions WHERE fileId = :fileId")
    suspend fun deleteAllVersionsForFile(fileId: Long)
}
