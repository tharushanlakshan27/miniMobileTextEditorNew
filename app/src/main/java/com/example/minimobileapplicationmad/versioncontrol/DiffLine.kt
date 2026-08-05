package com.example.minimobileapplicationmad.versioncontrol

data class DiffLine(
    val content: String,
    val type: Type
) {
    enum class Type {
        ADDED, REMOVED, UNCHANGED
    }
}
