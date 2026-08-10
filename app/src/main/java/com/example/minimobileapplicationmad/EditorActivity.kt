package com.example.minimobileapplicationmad

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.minimobileapplicationmad.databinding.ActivityEditorBinding
import com.example.minimobileapplicationmad.editor.UndoRedoManager
import com.example.minimobileapplicationmad.manager.RecentFilesManager
import com.example.minimobileapplicationmad.editor.syntax.KotlinSyntaxHighlighter
import com.example.minimobileapplicationmad.editor.syntax.MarkdownSyntaxHighlighter
import com.example.minimobileapplicationmad.editor.syntax.SyntaxTheme
import java.io.*

import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope

import com.example.minimobileapplicationmad.autosave.AutosaveManager
import com.example.minimobileapplicationmad.database.AppDatabase
import com.example.minimobileapplicationmad.history.VersionHistoryActivity
import com.example.minimobileapplicationmad.repository.FileRepository
import com.example.minimobileapplicationmad.repository.VersionRepository
import com.example.minimobileapplicationmad.storage.FileStorageManager
import com.example.minimobileapplicationmad.versioncontrol.VersionManager
import com.example.minimobileapplicationmad.viewmodel.EditorViewModel
import com.example.minimobileapplicationmad.viewmodel.EditorViewModelFactory
import androidx.activity.viewModels
import android.content.Intent

class EditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditorBinding
    private lateinit var undoRedoManager: UndoRedoManager
    private lateinit var recentFilesManager: RecentFilesManager
    private var currentUri: Uri? = null
    private var currentHighlighter: TextWatcher? = null
    private var currentEncoding = "UTF-8"
    
    private lateinit var fileStorageManager: FileStorageManager
    private var autosaveManager: AutosaveManager? = null

    private val viewModel: EditorViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val fileRepo = FileRepository(db.fileDao())
        val versionRepo = VersionRepository(db.versionDao())
        fileStorageManager = FileStorageManager(applicationContext)
        val versionManager = VersionManager(fileRepo, versionRepo, fileStorageManager)
        EditorViewModelFactory(fileRepo, versionRepo, fileStorageManager, versionManager)
    }

    private val saveAsLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri: Uri? ->
        uri?.let { newUri ->
            currentUri = newUri
            saveFile()
            updateFileInfo()
            
            // Update ViewModel and recent files for the new file
            val fileName = getFileName(newUri)
            recentFilesManager.addFile(fileName, newUri.toString())
            viewModel.loadOrCreateFile(fileName, newUri.toString(), currentEncoding)
            
            // Re-setup syntax highlighter for the new file extension if it changed
            setupSyntaxHighlighter(newUri)
        }
    }

    private val versionHistoryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        currentUri?.let {
            loadFileContent(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        undoRedoManager = UndoRedoManager(binding.editor)
        recentFilesManager = RecentFilesManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            intent.data?.let {
                currentUri = it
                loadFileContent(it)
                updateFileInfo()
            } ?: run {
                // Handle case where activity is started without data (should not happen based on current logic)
                setupSyntaxHighlighter(Uri.EMPTY)
            }
        } else {
            // Restore state if needed
            currentUri = intent.data
            updateFileInfo()
            currentUri?.let { setupSyntaxHighlighter(it) }
        }

        setupListeners()
        setupTextStats()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.currentFile.observe(this) { file ->
            file?.let {
                binding.switchReadOnly.isChecked = it.isReadOnly
                updateEditorReadOnlyState(it.isReadOnly)
                
                // Update encoding from database
                currentEncoding = it.encoding
                updateEncodingUI()
                
                // Initialize/Restart Autosave
                autosaveManager?.let { am -> lifecycle.removeObserver(am) }
                autosaveManager = AutosaveManager(
                    fileStorageManager,
                    it.fileName,
                    { binding.editor.text.toString() },
                    it.isReadOnly
                )
                lifecycle.addObserver(autosaveManager!!)
                
                if (it.isReadOnly) {
                    Toast.makeText(this, "This file is read-only.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.statusMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateEditorReadOnlyState(isReadOnly: Boolean) {
        binding.editor.isEnabled = !isReadOnly
        binding.editor.isFocusable = !isReadOnly
        binding.editor.isFocusableInTouchMode = !isReadOnly
        
        binding.btnSave.isEnabled = !isReadOnly
        binding.btnSaveAs.isEnabled = true // Save As should always be allowed
        binding.btnSaveVersion.isEnabled = !isReadOnly
        binding.btnReplace.isEnabled = !isReadOnly
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener { 
            if (binding.drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
            }
        }
        
        binding.btnSave.setOnClickListener {
            if (currentUri != null) {
                saveFile()
            } else {
                saveAsLauncher.launch("untitled.txt")
            }
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        }

        binding.btnSaveAs.setOnClickListener {
            val fileName = currentUri?.let { getFileName(it) } ?: "untitled.txt"
            showEncodingSelector { encoding ->
                currentEncoding = encoding
                updateEncodingUI()
                saveAsLauncher.launch(fileName)
            }
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        }

        binding.btnUndo.setOnClickListener { undoRedoManager.undo() }
        binding.btnRedo.setOnClickListener { undoRedoManager.redo() }

        binding.btnSearch.setOnClickListener { 
            showSearchDialog()
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        }
        binding.btnReplace.setOnClickListener { 
            showReplaceDialog()
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        }

        binding.switchWordWrap.setOnCheckedChangeListener { _, isChecked ->
            binding.editor.setHorizontallyScrolling(!isChecked)
        }

        binding.switchReadOnly.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateReadOnly(isChecked)
        }

        binding.btnSaveVersion.setOnClickListener {
            showSaveVersionDialog()
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        }

        binding.btnHistory.setOnClickListener {
            viewModel.currentFile.value?.let { file ->
                val intent = Intent(this, VersionHistoryActivity::class.java).apply {
                    putExtra("FILE_ID", file.id)
                }
                versionHistoryLauncher.launch(intent)
            }
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        }
    }

    private fun showSaveVersionDialog() {
        val input = EditText(this)
        input.hint = "Version name (e.g. Added Login)"
        AlertDialog.Builder(this)
            .setTitle("Save Version")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val versionName = input.text.toString().ifEmpty { "v${System.currentTimeMillis()}" }
                viewModel.saveVersion(versionName, binding.editor.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkForDraft(fileName: String) {
        if (fileStorageManager.hasDraft(fileName)) {
            AlertDialog.Builder(this)
                .setTitle("Recover Draft")
                .setMessage("We found an unsaved draft for this file. Would you like to restore it?")
                .setPositiveButton("Restore") { _, _ ->
                    fileStorageManager.loadDraft(fileName).onSuccess { content ->
                        binding.editor.setText(content)
                        Toast.makeText(this, "Draft restored", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Discard") { _, _ ->
                    fileStorageManager.deleteDraft(fileName).onSuccess {
                        autosaveManager?.isEnabled = false
                        binding.editor.setText("") // Clear the editor content as requested
                        Toast.makeText(this, "Draft discarded and editor cleared", Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        Toast.makeText(this, "Error discarding draft", Toast.LENGTH_SHORT).show()
                    }
                }
                .show()
        }
    }

    private fun setupTextStats() {
        binding.editor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString() ?: ""
                binding.tvCharCount.text = "Chars: ${text.length}"
                binding.tvLineCount.text = "Lines: ${if (text.isEmpty()) 0 else text.split("\n").size}"
            }
        })
        updateEncodingUI()
    }

    private fun updateEncodingUI() {
        binding.tvEncoding.text = "Encoding: $currentEncoding"
    }

    private fun showEncodingSelector(onSelected: (String) -> Unit) {
        val encodings = arrayOf("UTF-8", "UTF-16", "US-ASCII", "ISO-8859-1")
        AlertDialog.Builder(this)
            .setTitle("Select Encoding")
            .setItems(encodings) { _, which ->
                onSelected(encodings[which])
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadFileContent(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                // We default to UTF-8 for loading as per requirements
                val encodingToUse = "UTF-8"
                BufferedReader(InputStreamReader(inputStream, encodingToUse)).use { reader ->
                    val content = reader.readText()
                    binding.editor.setText(content)
                    undoRedoManager.clearHistory()
                    
                    currentEncoding = encodingToUse
                    updateEncodingUI()
                    
                    val fileName = getFileName(uri)
                    recentFilesManager.addFile(fileName, uri.toString())
                    
                    viewModel.loadOrCreateFile(fileName, uri.toString(), encodingToUse)
                    checkForDraft(fileName)

                    // Set up highlighter AFTER text is set
                    setupSyntaxHighlighter(uri)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSyntaxHighlighter(uri: Uri) {
        val fileName = getFileName(uri).lowercase()
        
        currentHighlighter?.let {
            binding.editor.removeTextChangedListener(it)
        }

        val theme = if (isDarkTheme()) SyntaxTheme.createDefaultDark() else SyntaxTheme.createDefaultLight()

        // Force Kotlin highlighting as default if no other known format matches
        currentHighlighter = when {
            fileName.endsWith(".md") || fileName.endsWith(".markdown") -> MarkdownSyntaxHighlighter(theme)
            else -> KotlinSyntaxHighlighter(this, theme)
        }

        currentHighlighter?.let {
            binding.editor.addTextChangedListener(it)
            // Trigger initial highlight
            it.afterTextChanged(binding.editor.text)
        }
    }

    private fun isDarkTheme(): Boolean {
        return (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == 
                android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    private fun saveFile() {
        currentUri?.let { uri ->
            try {
                contentResolver.openOutputStream(uri, "wt")?.use { outputStream ->
                    BufferedWriter(OutputStreamWriter(outputStream, currentEncoding)).use { writer ->
                        writer.write(binding.editor.text.toString())
                        Toast.makeText(this, "Saved successfully as $currentEncoding", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error saving file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFileInfo() {
        currentUri?.let {
            binding.toolbar.title = getFileName(it)
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "untitled.txt"
    }

    private fun showSearchDialog() {
        val input = EditText(this)
        input.hint = "Text to find"
        AlertDialog.Builder(this)
            .setTitle("Search")
            .setView(input)
            .setPositiveButton("Find All") { _, _ ->
                highlightSearch(input.text.toString())
            }
            .setNeutralButton("Clear Highlight") { _, _ ->
                clearSearchHighlight()
            }
            .show()
    }

    private fun highlightSearch(query: String) {
        if (query.isEmpty()) return
        val fullText = binding.editor.text.toString()
        val spannable = SpannableString(fullText)
        
        var index = fullText.indexOf(query, 0, true)
        while (index >= 0) {
            spannable.setSpan(
                BackgroundColorSpan(Color.YELLOW),
                index,
                index + query.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            index = fullText.indexOf(query, index + query.length, true)
        }
        
        val start = binding.editor.selectionStart
        val end = binding.editor.selectionEnd
        binding.editor.setText(spannable)
        // Safely restore selection
        try {
            binding.editor.setSelection(start.coerceAtMost(spannable.length), end.coerceAtMost(spannable.length))
        } catch (e: Exception) {
            binding.editor.setSelection(spannable.length)
        }
    }

    private fun clearSearchHighlight() {
        // This is tricky because it might remove syntax highlighting too
        // For now, let's just re-set the text which will trigger highlighters again
        val text = binding.editor.text.toString()
        binding.editor.setText(text)
    }

    private fun showReplaceDialog() {
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        val findInput = EditText(this)
        findInput.hint = "Find text"
        val replaceInput = EditText(this)
        replaceInput.hint = "Replace with"
        layout.addView(findInput)
        layout.addView(replaceInput)

        AlertDialog.Builder(this)
            .setTitle("Replace")
            .setView(layout)
            .setPositiveButton("Replace All") { _, _ ->
                val text = binding.editor.text.toString()
                val findText = findInput.text.toString()
                if (findText.isEmpty()) return@setPositiveButton
                
                val newText = text.replace(findText, replaceInput.text.toString(), ignoreCase = true)
                binding.editor.setText(newText)
                binding.editor.setSelection(binding.editor.text.length)
            }
            .show()
    }
}
