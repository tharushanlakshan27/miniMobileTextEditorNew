package com.example.minimobileapplicationmad.viewmodel

import androidx.lifecycle.*
import com.example.minimobileapplicationmad.database.entities.FileEntity
import com.example.minimobileapplicationmad.repository.FileRepository
import com.example.minimobileapplicationmad.repository.VersionRepository
import com.example.minimobileapplicationmad.storage.FileStorageManager
import com.example.minimobileapplicationmad.versioncontrol.VersionManager
import kotlinx.coroutines.launch

class EditorViewModel(
    private val fileRepository: FileRepository,
    private val versionRepository: VersionRepository,
    private val fileStorageManager: FileStorageManager,
    private val versionManager: VersionManager
) : ViewModel() {

    private val _currentFile = MutableLiveData<FileEntity?>()
    val currentFile: LiveData<FileEntity?> = _currentFile

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    fun loadOrCreateFile(fileName: String, path: String, encoding: String = "UTF-8") {
        viewModelScope.launch {
            var file = fileRepository.getFileByPath(path)
            if (file == null) {
                val newFile = FileEntity(
                    fileName = fileName,
                    filePath = path,
                    createdDate = System.currentTimeMillis(),
                    modifiedDate = System.currentTimeMillis(),
                    encoding = encoding
                )
                val id = fileRepository.insertFile(newFile)
                file = newFile.copy(id = id)
            } else if (file.encoding != encoding) {
                // Update encoding if it changed (e.g. during Save As)
                val updatedFile = file.copy(encoding = encoding)
                fileRepository.updateFile(updatedFile)
                file = updatedFile
            }
            _currentFile.value = file
        }
    }

    fun saveVersion(versionName: String, content: String) {
        val file = _currentFile.value ?: return
        viewModelScope.launch {
            val result = versionManager.saveVersion(file.id, versionName, content)
            result.onSuccess {
                _statusMessage.value = "Version saved: $versionName"
                // Refresh file to get new modified date
                _currentFile.value = fileRepository.getFileById(file.id)
            }.onFailure {
                _statusMessage.value = "Error saving version: ${it.message}"
            }
        }
    }

    fun updateReadOnly(isReadOnly: Boolean) {
        val file = _currentFile.value ?: return
        viewModelScope.launch {
            fileRepository.updateReadOnlyStatus(file.id, isReadOnly)
            _currentFile.value = file.copy(isReadOnly = isReadOnly)
        }
    }
}

class EditorViewModelFactory(
    private val fileRepository: FileRepository,
    private val versionRepository: VersionRepository,
    private val fileStorageManager: FileStorageManager,
    private val versionManager: VersionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditorViewModel(fileRepository, versionRepository, fileStorageManager, versionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
