package com.example.minimobileapplicationmad.editor.syntax

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

object KeywordLoader {
    private var kotlinKeywords: Set<String>? = null

    fun getKotlinKeywords(context: Context): Set<String> {
        if (kotlinKeywords == null) {
            kotlinKeywords = loadKeywordsFromAssets(context, "kotlin_keywords.json")
        }
        return kotlinKeywords!!
    }

    private fun loadKeywordsFromAssets(context: Context, fileName: String): Set<String> {
        return try {
            context.assets.open(fileName).use { inputStream ->
                val reader = InputStreamReader(inputStream)
                val data = Gson().fromJson(reader, KeywordData::class.java)
                data.keywords.toSet()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptySet()
        }
    }

    private data class KeywordData(val keywords: List<String>)
}
