package com.example.minimobileapplicationmad

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minimobileapplicationmad.databinding.ActivityRecentFilesBinding

class RecentFilesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecentFilesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecentFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val dummyFiles = listOf(
            RecentFile("notes.md", "Last modified: 2 hours ago"),
            RecentFile("MainActivity.kt", "Last modified: Yesterday"),
            RecentFile("README.md", "Last modified: 3 days ago"),
            RecentFile("todo.txt", "Last modified: 1 week ago")
        )
        binding.rvRecentFiles.layoutManager = LinearLayoutManager(this)
        binding.rvRecentFiles.adapter = RecentFilesAdapter(dummyFiles)
    }
}
