package com.example.minimobileapplicationmad.history

import androidx.lifecycle.*
import androidx.lifecycle.asLiveData
import com.example.minimobileapplicationmad.database.entities.VersionEntity
import com.example.minimobileapplicationmad.repository.VersionRepository
import com.example.minimobileapplicationmad.versioncontrol.VersionManager
import kotlinx.coroutines.launch

class VersionHistoryViewModel(
    private val versionRepository: VersionRepository,
    private val versionManager: VersionManager,
    private val fileId: Long
) : ViewModel() {

    val versions: LiveData<List<VersionEntity>> = versionRepository.getVersionsForFile(fileId).asLiveData()

    private val _restoreStatus = MutableLiveData<Result<Unit>>()
    val restoreStatus: LiveData<Result<Unit>> = _restoreStatus

    fun restoreVersion(context: android.content.Context, versionId: Long) {
        viewModelScope.launch {
            _restoreStatus.value = versionManager.restoreVersion(context, fileId, versionId)
        }
    }
}

class VersionHistoryViewModelFactory(
    private val versionRepository: VersionRepository,
    private val versionManager: VersionManager,
    private val fileId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VersionHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VersionHistoryViewModel(versionRepository, versionManager, fileId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
