package com.example.minimobileapplicationmad.storage

import android.content.Context
import java.io.File
import java.io.IOException

class FileStorageManager(private val context: Context) {

    private val baseDirectory: File by lazy {
        File(context.filesDir, "MyFiles").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private val draftDirectory: File by lazy {
        File(context.filesDir, "Drafts").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private val versionBaseDirectory: File by lazy {
        File(context.filesDir, "VersionBases").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    fun saveVersionBase(fileId: Long, content: String): Result<Unit> {
        return try {
            val file = File(versionBaseDirectory, "base_$fileId.txt")
            file.writeText(content)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun loadVersionBase(fileId: Long): Result<String> {
        return try {
            val file = File(versionBaseDirectory, "base_$fileId.txt")
            if (file.exists()) {
                Result.success(file.readText())
            } else {
                Result.failure(IOException("Base file not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun hasVersionBase(fileId: Long): Boolean {
        return File(versionBaseDirectory, "base_$fileId.txt").exists()
    }

    fun saveDraft(fileName: String, content: String): Result<Unit> {
        return try {
            val file = File(draftDirectory, fileName)
            file.writeText(content)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun loadDraft(fileName: String): Result<String> {
        return try {
            val file = File(draftDirectory, fileName)
            if (file.exists()) {
                Result.success(file.readText())
            } else {
                Result.failure(IOException("Draft not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deleteDraft(fileName: String): Result<Unit> {
        return try {
            val file = File(draftDirectory, fileName)
            if (file.exists()) {
                file.delete()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun hasDraft(fileName: String): Boolean {
        return File(draftDirectory, fileName).exists()
    }

    fun createFile(fileName: String, content: String = ""): Result<File> {
        return try {
            val file = File(baseDirectory, fileName)
            if (file.exists()) {
                Result.failure(IOException("File already exists"))
            } else {
                file.writeText(content)
                Result.success(file)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun saveFile(fileName: String, content: String): Result<Unit> {
        return try {
            val file = File(baseDirectory, fileName)
            file.writeText(content)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun loadFile(fileName: String): Result<String> {
        return try {
            val file = File(baseDirectory, fileName)
            if (file.exists()) {
                Result.success(file.readText())
            } else {
                Result.failure(IOException("File not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deleteFile(fileName: String): Result<Unit> {
        return try {
            val file = File(baseDirectory, fileName)
            if (file.exists()) {
                if (file.delete()) {
                    Result.success(Unit)
                } else {
                    Result.failure(IOException("Could not delete file"))
                }
            } else {
                Result.success(Unit) // Already deleted
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun renameFile(oldName: String, newName: String): Result<File> {
        return try {
            val oldFile = File(baseDirectory, oldName)
            val newFile = File(baseDirectory, newName)
            if (oldFile.exists()) {
                if (oldFile.renameTo(newFile)) {
                    Result.success(newFile)
                } else {
                    Result.failure(IOException("Could not rename file"))
                }
            } else {
                Result.failure(IOException("Source file not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getFile(fileName: String): File {
        return File(baseDirectory, fileName)
    }

    fun listFiles(): List<File> {
        return baseDirectory.listFiles()?.toList() ?: emptyList()
    }
}
