package com.example.minimobileapplicationmad.manager

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class RecentFileItem(val name: String, val path: String, val date: Long)

class RecentFilesManager(context: Context) {

    private val prefs = context.getSharedPreferences("recent_files_prefs", Context.MODE_PRIVATE)

    fun addFile(name: String, path: String) {
        val currentList = getRecentFiles().toMutableList()
        // Remove if exists to move to top
        currentList.removeAll { it.path == path }
        currentList.add(0, RecentFileItem(name, path, System.currentTimeMillis()))
        
        // Keep only top 20
        val limitedList = currentList.take(20)
        saveList(limitedList)
    }

    fun getRecentFiles(): List<RecentFileItem> {
        val jsonString = prefs.getString("recent_files_key", "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val list = mutableListOf<RecentFileItem>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.add(RecentFileItem(
                obj.getString("name"),
                obj.getString("path"),
                obj.getLong("date")
            ))
        }
        return list
    }

    private fun saveList(list: List<RecentFileItem>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("name", item.name)
            obj.put("path", item.path)
            obj.put("date", item.date)
            jsonArray.put(obj)
        }
        prefs.edit().putString("recent_files_key", jsonArray.toString()).apply()
    }
}
