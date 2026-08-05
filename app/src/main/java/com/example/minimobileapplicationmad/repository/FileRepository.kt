package com.example.minimobileapplicationmad.repository

import com.example.minimobileapplicationmad.database.dao.FileDao
import com.example.minimobileapplicationmad.database.entities.FileEntity
import kotlinx.coroutines.flow.Flow

class FileRepository(private val fileDao: FileDao) {
    val allFiles: Flow<List<FileEntity>> = fileDao.getAllFiles()

    suspend fun getFileById(id: Long) = fileDao.getFileById(id)

    suspend fun getFileByPath(path: String) = fileDao.getFileByPath(path)

    suspend fun insertFile(file: FileEntity) = fileDao.insertFile(file)

    suspend fun updateFile(file: FileEntity) = fileDao.updateFile(file)

    suspend fun deleteFile(file: FileEntity) = fileDao.deleteFile(file)

    suspend fun updateReadOnlyStatus(id: Long, isReadOnly: Boolean) = 
        fileDao.updateReadOnlyStatus(id, isReadOnly)
}
