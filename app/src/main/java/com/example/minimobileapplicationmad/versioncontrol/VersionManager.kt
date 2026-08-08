package com.example.minimobileapplicationmad.versioncontrol

import android.content.Context
import android.net.Uri
import com.example.minimobileapplicationmad.database.entities.VersionEntity
import com.example.minimobileapplicationmad.repository.FileRepository
import com.example.minimobileapplicationmad.repository.VersionRepository
import com.example.minimobileapplicationmad.storage.FileStorageManager
import java.io.BufferedWriter
import java.io.OutputStreamWriter

class VersionManager(
    private val fileRepository: FileRepository,
    private val versionRepository: VersionRepository,
    private val fileStorageManager: FileStorageManager
) {

    suspend fun saveVersion(fileId: Long, versionName: String, currentText: String): Result<Unit> {
        return try {
            val fileEntity = fileRepository.getFileById(fileId) 
                ?: return Result.failure(Exception("File not found in database"))
            
            if (fileEntity.isReadOnly) {
                return Result.failure(Exception("This file is read-only."))
            }

            val versions = versionRepository.getVersionsForFileSync(fileId)
            val previousText = if (versions.isEmpty()) {
                ""
            } else {
                reconstructTextAtVersion(versions)
            }

            val patch = DiffHelper.generateDiff(previousText, currentText, fileEntity.fileName)
            
            val newVersion = VersionEntity(
                fileId = fileId,
                versionName = versionName,
                timestamp = System.currentTimeMillis(),
                diffPatch = patch
            )

            versionRepository.insertVersion(newVersion)
            
            // Update modified date
            fileRepository.updateFile(fileEntity.copy(modifiedDate = System.currentTimeMillis()))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreVersion(context: Context, fileId: Long, versionId: Long): Result<Unit> {
        return try {
            val fileEntity = fileRepository.getFileById(fileId)
                ?: return Result.failure(Exception("File not found"))

            if (fileEntity.isReadOnly) {
                return Result.failure(Exception("This file is read-only."))
            }

            val versions = versionRepository.getVersionsForFileSync(fileId)
            val targetVersions = mutableListOf<VersionEntity>()
            
            for (v in versions) {
                targetVersions.add(v)
                if (v.id == versionId) break
            }

            val restoredText = reconstructTextAtVersion(targetVersions)
            
            // 1. Save to internal storage (as backup/cache)
            fileStorageManager.saveFile(fileEntity.fileName, restoredText)
            
            // 2. Save to the actual file URI if it's a content URI
            if (fileEntity.filePath.startsWith("content://")) {
                val uri = Uri.parse(fileEntity.filePath)
                context.contentResolver.openOutputStream(uri, "wt")?.use { outputStream ->
                    BufferedWriter(OutputStreamWriter(outputStream, "UTF-8")).use { writer ->
                        writer.write(restoredText)
                    }
                }
            }

            // Update modified date in DB
            fileRepository.updateFile(fileEntity.copy(modifiedDate = System.currentTimeMillis()))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun reconstructTextAtVersion(versions: List<VersionEntity>): String {
        var currentText = ""
        // Versions are assumed to be in chronological order (ASC)
        for (version in versions) {
            currentText = DiffHelper.applyPatch(currentText, version.diffPatch)
        }
        return currentText
    }
}
