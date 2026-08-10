package com.example.minimobileapplicationmad.versioncontrol

import com.github.difflib.DiffUtils
import com.github.difflib.patch.Patch
import com.github.difflib.UnifiedDiffUtils

object DiffHelper {

    fun generateDiff(oldText: String, newText: String, fileName: String = "file"): String {
        val oldLines = oldText.lines()
        val newLines = newText.lines()
        val patch: Patch<String> = DiffUtils.diff(oldLines, newLines)
        val unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(fileName, fileName, oldLines, patch, 0)
        return unifiedDiff.joinToString("\n")
    }

    fun applyPatch(originalText: String, patchString: String): String {
        if (patchString.isEmpty()) return originalText
        return try {
            val originalLines = originalText.lines()
            val patch = UnifiedDiffUtils.parseUnifiedDiff(patchString.lines())
            val revisedLines = DiffUtils.patch(originalLines, patch)
            revisedLines.joinToString("\n")
        } catch (e: Exception) {
            // If patch fails, returning original text is safer than crashing
            originalText
        }
    }

    fun reversePatch(revisedText: String, patchString: String): String {
        if (patchString.isEmpty()) return revisedText
        val revisedLines = revisedText.lines()
        val patch = UnifiedDiffUtils.parseUnifiedDiff(patchString.lines())
        val originalLines = DiffUtils.unpatch(revisedLines, patch)
        return originalLines.joinToString("\n")
    }

    fun parseUnifiedDiffToLines(unifiedDiff: String): List<DiffLine> {
        val lines = unifiedDiff.lines()
        val result = mutableListOf<DiffLine>()
        
        for (line in lines) {
            when {
                line.startsWith("+") && !line.startsWith("+++") -> {
                    result.add(DiffLine(line, DiffLine.Type.ADDED))
                }
                line.startsWith("-") && !line.startsWith("---") -> {
                    result.add(DiffLine(line, DiffLine.Type.REMOVED))
                }
                line.startsWith("@@") || line.startsWith("---") || line.startsWith("+++") -> {
                    // Skip metadata for display if you want, or add as UNCHANGED
                }
                else -> {
                    result.add(DiffLine(line, DiffLine.Type.UNCHANGED))
                }
            }
        }
        return result
    }
}
