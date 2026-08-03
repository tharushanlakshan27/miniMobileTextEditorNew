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
import com.example.minimobileapplicationmad.syntax.KotlinSyntaxHighlighter
import com.example.minimobileapplicationmad.syntax.MarkdownSyntaxHighlighter
import java.io.*

class EditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditorBinding
    private lateinit var undoRedoManager: UndoRedoManager
    private lateinit var recentFilesManager: RecentFilesManager
    private var currentUri: Uri? = null

    private val saveAsLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri: Uri? ->
        uri?.let {
            currentUri = it
            saveFile()
            updateFileInfo()
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

        intent.data?.let {
            currentUri = it
            loadFileContent(it)
            updateFileInfo()
        }

        setupListeners()
        setupTextStats()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        
        binding.btnSave.setOnClickListener {
            if (currentUri != null) {
                saveFile()
            } else {
                saveAsLauncher.launch("untitled.txt")
            }
        }

        binding.btnUndo.setOnClickListener { undoRedoManager.undo() }
        binding.btnRedo.setOnClickListener { undoRedoManager.redo() }

        binding.btnSearch.setOnClickListener { showSearchDialog() }
        binding.btnReplace.setOnClickListener { showReplaceDialog() }

        binding.switchWordWrap.setOnCheckedChangeListener { _, isChecked ->
            binding.editor.setHorizontallyScrolling(!isChecked)
        }

        binding.switchReadOnly.setOnCheckedChangeListener { _, isChecked ->
            binding.editor.isEnabled = !isChecked
            binding.editor.isFocusable = !isChecked
            binding.editor.isFocusableInTouchMode = !isChecked
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
    }

    private fun loadFileContent(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, "UTF-8")).use { reader ->
                    val content = reader.readText()
                    binding.editor.setText(content)
                    undoRedoManager.clearHistory()
                    setupSyntaxHighlighter(uri)
                    
                    val fileName = getFileName(uri)
                    recentFilesManager.addFile(fileName, uri.toString())
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSyntaxHighlighter(uri: Uri) {
        val fileName = getFileName(uri).lowercase()
        // For simplicity, we can't easily remove anonymous TextWatchers without keeping references.
        // In a real app, we'd manage these better.
        if (fileName.endsWith(".kt")) {
            binding.editor.addTextChangedListener(KotlinSyntaxHighlighter())
        } else if (fileName.endsWith(".md")) {
            binding.editor.addTextChangedListener(MarkdownSyntaxHighlighter())
        }
    }

    private fun saveFile() {
        currentUri?.let { uri ->
            try {
                contentResolver.openOutputStream(uri, "wt")?.use { outputStream ->
                    BufferedWriter(OutputStreamWriter(outputStream, "UTF-8")).use { writer ->
                        writer.write(binding.editor.text.toString())
                        Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show()
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
        return uri.path?.substringAfterLast('/') ?: "untitled.txt"
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
        binding.editor.setText(spannable)
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
                val newText = text.replace(findInput.text.toString(), replaceInput.text.toString(), ignoreCase = true)
                binding.editor.setText(newText)
            }
            .show()
    }
}
