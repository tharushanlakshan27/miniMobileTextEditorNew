package com.example.minimobileapplicationmad.database.dao

import androidx.room.*
import com.example.minimobileapplicationmad.database.entities.FileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Query("SELECT * FROM files ORDER BY modifiedDate DESC")
    fun getAllFiles(): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE id = :id")
    suspend fun getFileById(id: Long): FileEntity?

    @Query("SELECT * FROM files WHERE filePath = :path")
    suspend fun getFileByPath(path: String): FileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileEntity): Long

    @Update
    suspend fun updateFile(file: FileEntity)

    @Delete
    suspend fun deleteFile(file: FileEntity)

    @Query("UPDATE files SET isReadOnly = :isReadOnly WHERE id = :id")
    suspend fun updateReadOnlyStatus(id: Long, isReadOnly: Boolean)
}
