package com.example.minimobileapplicationmad.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.minimobileapplicationmad.database.AppDatabase
import com.example.minimobileapplicationmad.databinding.ActivityVersionHistoryBinding
import com.example.minimobileapplicationmad.repository.FileRepository
import com.example.minimobileapplicationmad.repository.VersionRepository
import com.example.minimobileapplicationmad.storage.FileStorageManager
import com.example.minimobileapplicationmad.versioncontrol.DiffViewerActivity
import com.example.minimobileapplicationmad.versioncontrol.VersionManager

class VersionHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVersionHistoryBinding
    private var fileId: Long = -1

    private val viewModel: VersionHistoryViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val fileRepo = FileRepository(db.fileDao())
        val versionRepo = VersionRepository(db.versionDao())
        val storage = FileStorageManager(applicationContext)
        val manager = VersionManager(fileRepo, versionRepo, storage)
        VersionHistoryViewModelFactory(versionRepo, manager, fileId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVersionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileId = intent.getLongExtra("FILE_ID", -1)
        if (fileId == -1L) {
            Toast.makeText(this, "Invalid file ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        val adapter = VersionAdapter(
            onViewDiff = { version ->
                val intent = Intent(this, DiffViewerActivity::class.java).apply {
                    putExtra("DIFF_TEXT", version.diffPatch)
                }
                startActivity(intent)
            },
            onRestore = { version ->
                showRestoreConfirmation(version.id)
            }
        )
        binding.rvVersionHistory.adapter = adapter

        viewModel.versions.observe(this) { versions ->
            adapter.submitList(versions)
            binding.tvEmpty.visibility = if (versions.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun observeViewModel() {
        viewModel.restoreStatus.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Version restored successfully", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRestoreConfirmation(versionId: Long) {
        AlertDialog.Builder(this)
            .setTitle("Restore Version")
            .setMessage("Are you sure you want to restore this version? This will overwrite the current file.")
            .setPositiveButton("Restore") { _, _ ->
                viewModel.restoreVersion(this, versionId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
