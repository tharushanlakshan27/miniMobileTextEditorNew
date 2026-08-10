package com.example.minimobileapplicationmad

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.minimobileapplicationmad.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val openFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            val intent = Intent(this, EditorActivity::class.java).apply {
                data = it
                putExtra("action", "open")
            }
            startActivity(intent)
        }
    }

    private val createFileLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri: Uri? ->
        uri?.let {
            val intent = Intent(this, EditorActivity::class.java).apply {
                data = it
                putExtra("action", "new")
            }
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnNewFile.setOnClickListener {
            showNewFileDialog()
        }
        binding.btnOpenFile.setOnClickListener {
            openFileLauncher.launch(arrayOf("text/plain", "text/markdown", "application/octet-stream"))
        }
        binding.btnRecentFiles.setOnClickListener {
            startActivity(Intent(this, RecentFilesActivity::class.java))
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun showNewFileDialog() {
        val input = EditText(this)
        input.hint = "filename.txt"
        AlertDialog.Builder(this)
            .setTitle("New File")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val fileName = input.text.toString().ifEmpty { "untitled.txt" }
                createFileLauncher.launch(fileName)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
