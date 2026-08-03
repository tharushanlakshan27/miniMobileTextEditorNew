package com.example.minimobileapplicationmad

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.minimobileapplicationmad.databinding.ItemRecentFileBinding

data class RecentFile(val name: String, val date: String, val path: String)

class RecentFilesAdapter(private val files: List<RecentFile>, private val onItemClick: (String) -> Unit) : RecyclerView.Adapter<RecentFilesAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRecentFileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        holder.binding.tvFileName.text = file.name
        holder.binding.tvLastModified.text = file.date
        holder.binding.root.setOnClickListener { onItemClick(file.path) }
    }

    override fun getItemCount() = files.size
}
