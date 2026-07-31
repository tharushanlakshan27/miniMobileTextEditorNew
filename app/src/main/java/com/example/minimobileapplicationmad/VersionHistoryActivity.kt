package com.example.minimobileapplicationmad

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minimobileapplicationmad.databinding.ActivityVersionHistoryBinding

class VersionHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVersionHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVersionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val dummyVersions = listOf(
            VersionItem("Version 1", "Created Today"),
            VersionItem("Version 2", "Yesterday"),
            VersionItem("Version 3", "2 Days Ago")
        )
        binding.rvVersionHistory.layoutManager = LinearLayoutManager(this)
        binding.rvVersionHistory.adapter = VersionHistoryAdapter(dummyVersions)
    }
}
