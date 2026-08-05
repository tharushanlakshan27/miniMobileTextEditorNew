package com.example.minimobileapplicationmad.repository

import com.example.minimobileapplicationmad.database.dao.VersionDao
import com.example.minimobileapplicationmad.database.entities.VersionEntity
import kotlinx.coroutines.flow.Flow

class VersionRepository(private val versionDao: VersionDao) {
    fun getVersionsForFile(fileId: Long): Flow<List<VersionEntity>> = 
        versionDao.getVersionsForFile(fileId)

    suspend fun getVersionsForFileSync(fileId: Long): List<VersionEntity> =
        versionDao.getVersionsForFileSync(fileId)

    suspend fun insertVersion(version: VersionEntity) = versionDao.insertVersion(version)

    suspend fun deleteVersion(version: VersionEntity) = versionDao.deleteVersion(version)

    suspend fun deleteAllVersionsForFile(fileId: Long) = 
        versionDao.deleteAllVersionsForFile(fileId)
}
