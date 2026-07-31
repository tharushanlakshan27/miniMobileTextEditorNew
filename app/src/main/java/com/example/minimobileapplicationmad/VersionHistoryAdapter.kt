package com.example.minimobileapplicationmad

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.minimobileapplicationmad.databinding.ItemVersionBinding

data class VersionItem(val name: String, val timestamp: String)

class VersionHistoryAdapter(private val versions: List<VersionItem>) : RecyclerView.Adapter<VersionHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemVersionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVersionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val version = versions[position]
        holder.binding.tvVersionName.text = version.name
        holder.binding.tvTimestamp.text = version.timestamp
    }

    override fun getItemCount() = versions.size
}
