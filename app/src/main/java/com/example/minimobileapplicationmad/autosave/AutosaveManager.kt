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
    private var initialContent: String = getContent()
    var isEnabled: Boolean = true

    override fun onStart(owner: LifecycleOwner) {
        if (!isReadOnly && isEnabled) {
            startAutosave(owner)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        stopAutosave()
        // Save only if enabled and content changed
        if (!isReadOnly && isEnabled) {
            val currentContent = getContent()
            if (currentContent != initialContent) {
                owner.lifecycleScope.launch(Dispatchers.IO) {
                    fileStorageManager.saveDraft(fileName, currentContent)
                }
            }
        }
    }

    private fun startAutosave(owner: LifecycleOwner) {
        autosaveJob = owner.lifecycleScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(10000) // 10 seconds
                val currentContent = withContext(Dispatchers.Main) {
                    getContent()
                }
                if (currentContent != initialContent) {
                    fileStorageManager.saveDraft(fileName, currentContent)
                }
            }
        }
    }

    private fun stopAutosave() {
        autosaveJob?.cancel()
        autosaveJob = null
    }
}
