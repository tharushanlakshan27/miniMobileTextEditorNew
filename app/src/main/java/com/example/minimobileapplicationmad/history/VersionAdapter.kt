package com.example.minimobileapplicationmad.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.minimobileapplicationmad.database.entities.VersionEntity
import com.example.minimobileapplicationmad.databinding.ItemVersionBinding
import java.text.SimpleDateFormat
import java.util.*

class VersionAdapter(
    private val onViewDiff: (VersionEntity) -> Unit,
    private val onRestore: (VersionEntity) -> Unit
) : ListAdapter<VersionEntity, VersionAdapter.VersionViewHolder>(VersionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VersionViewHolder {
        val binding = ItemVersionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VersionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VersionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VersionViewHolder(private val binding: ItemVersionBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        fun bind(version: VersionEntity) {
            binding.tvVersionName.text = version.versionName
            binding.tvTimestamp.text = dateFormat.format(Date(version.timestamp))
            
            binding.btnViewDiff.setOnClickListener { onViewDiff(version) }
            binding.btnRestore.setOnClickListener { onRestore(version) }
        }
    }

    class VersionDiffCallback : DiffUtil.ItemCallback<VersionEntity>() {
        override fun areItemsTheSame(oldItem: VersionEntity, newItem: VersionEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VersionEntity, newItem: VersionEntity): Boolean {
            return oldItem == newItem
        }
    }
}
