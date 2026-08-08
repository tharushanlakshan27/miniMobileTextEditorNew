package com.example.minimobileapplicationmad.storage

import android.content.Context
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.File

class FileStorageManagerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Mock
    private lateinit var mockContext: Context

    private lateinit var fileStorageManager: FileStorageManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockContext.filesDir).thenReturn(tempFolder.newFolder("files"))
        fileStorageManager = FileStorageManager(mockContext)
    }

    @Test
    fun testCreateAndLoadFile() {
        val fileName = "test.txt"
        val content = "Hello Storage"
        
        val createResult = fileStorageManager.createFile(fileName, content)
        assertTrue(createResult.isSuccess)
        
        val loadResult = fileStorageManager.loadFile(fileName)
        assertTrue(loadResult.isSuccess)
        assertEquals(content, loadResult.getOrNull())
    }

    @Test
    fun testSaveAndLoadDraft() {
        val fileName = "draft.txt"
        val content = "Draft Content"
        
        val saveResult = fileStorageManager.saveDraft(fileName, content)
        assertTrue(saveResult.isSuccess)
        assertTrue(fileStorageManager.hasDraft(fileName))
        
        val loadResult = fileStorageManager.loadDraft(fileName)
        assertEquals(content, loadResult.getOrNull())
    }
}
