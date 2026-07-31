package com.example.minimobileapplicationmad

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.minimobileapplicationmad.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
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
            startActivity(Intent(this, EditorActivity::class.java))
        }
        binding.btnOpenFile.setOnClickListener {
            // TODO: Implement open file
        }
        binding.btnRecentFiles.setOnClickListener {
            startActivity(Intent(this, RecentFilesActivity::class.java))
        }
        binding.btnVersionHistory.setOnClickListener {
            startActivity(Intent(this, VersionHistoryActivity::class.java))
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
