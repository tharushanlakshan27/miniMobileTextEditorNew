package com.example.minimobileapplicationmad

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minimobileapplicationmad.databinding.ActivityRecentFilesBinding
import com.example.minimobileapplicationmad.manager.RecentFilesManager
import java.text.SimpleDateFormat
import java.util.*

class RecentFilesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecentFilesBinding
    private lateinit var recentFilesManager: RecentFilesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecentFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        recentFilesManager = RecentFilesManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val files = recentFilesManager.getRecentFiles().map {
            RecentFile(it.name, "Last opened: ${dateFormat.format(Date(it.date))}", it.path)
        }

        binding.rvRecentFiles.layoutManager = LinearLayoutManager(this)
        binding.rvRecentFiles.adapter = RecentFilesAdapter(files) { path ->
            val intent = Intent(this, EditorActivity::class.java).apply {
                data = Uri.parse(path)
                putExtra("action", "open")
            }
            startActivity(intent)
        }
    }
}
