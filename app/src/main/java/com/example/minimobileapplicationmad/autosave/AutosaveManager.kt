package com.example.minimobileapplicationmad.autosave

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.minimobileapplicationmad.storage.FileStorageManager
import kotlinx.coroutines.*

class AutosaveManager(
    private val fileStorageManager: FileStorageManager,
    private val fileName: String,
    private val getContent: () -> String,
    private val isReadOnly: Boolean = false
) : DefaultLifecycleObserver {

    private var autosaveJob: Job? = null

    override fun onStart(owner: LifecycleOwner) {
        if (!isReadOnly) {
            startAutosave(owner)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        stopAutosave()
        // Save one last time when stopping
        if (!isReadOnly) {
            owner.lifecycleScope.launch(Dispatchers.IO) {
                fileStorageManager.saveDraft(fileName, getContent())
            }
        }
    }

    private fun startAutosave(owner: LifecycleOwner) {
        autosaveJob = owner.lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(10000) // 10 seconds
                fileStorageManager.saveDraft(fileName, getContent())
            }
        }
    }

    private fun stopAutosave() {
        autosaveJob?.cancel()
        autosaveJob = null
    }
}
