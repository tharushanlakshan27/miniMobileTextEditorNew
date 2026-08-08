package com.example.minimobileapplicationmad.versioncontrol

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.minimobileapplicationmad.databinding.ActivityDiffViewerBinding
import com.example.minimobileapplicationmad.databinding.ItemDiffLineBinding

class DiffViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiffViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiffViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val diffText = intent.getStringExtra("DIFF_TEXT") ?: ""
        val diffLines = DiffHelper.parseUnifiedDiffToLines(diffText)

        setupToolbar()
        setupRecyclerView(diffLines)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView(diffLines: List<DiffLine>) {
        binding.rvDiff.adapter = DiffAdapter(diffLines)
    }

    inner class DiffAdapter(private val lines: List<DiffLine>) : RecyclerView.Adapter<DiffAdapter.DiffViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiffViewHolder {
            val binding = ItemDiffLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DiffViewHolder(binding)
        }

        override fun onBindViewHolder(holder: DiffViewHolder, position: Int) {
            holder.bind(lines[position])
        }

        override fun getItemCount(): Int = lines.size

        inner class DiffViewHolder(private val binding: ItemDiffLineBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(line: DiffLine) {
                binding.tvDiffLine.text = line.content
                when (line.type) {
                    DiffLine.Type.ADDED -> {
                        binding.tvDiffLine.setBackgroundColor(Color.parseColor("#E6FFEC")) // Light green
                        binding.tvDiffLine.setTextColor(Color.parseColor("#22863A"))
                    }
                    DiffLine.Type.REMOVED -> {
                        binding.tvDiffLine.setBackgroundColor(Color.parseColor("#FFEFEF")) // Light red
                        binding.tvDiffLine.setTextColor(Color.parseColor("#CB2431"))
                    }
                    DiffLine.Type.UNCHANGED -> {
                        binding.tvDiffLine.setBackgroundColor(Color.TRANSPARENT)
                        binding.tvDiffLine.setTextColor(Color.BLACK)
                    }
                }
            }
        }
    }
}
