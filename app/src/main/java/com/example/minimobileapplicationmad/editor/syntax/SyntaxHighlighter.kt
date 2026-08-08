package com.example.minimobileapplicationmad.editor.syntax

import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class SyntaxHighlighter(
    protected var theme: SyntaxTheme
) : TextWatcher {

    private var isHighlighting = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isHighlighting || s == null) return
        
        try {
            isHighlighting = true
            applyHighlighting(s)
        } finally {
            isHighlighting = false
        }
    }

    protected abstract fun getPattern(): Pattern
    protected abstract fun mapGroupToType(matcher: Matcher): TokenType?

    private fun applyHighlighting(editable: Editable) {
        val text = editable.toString()
        val pattern = getPattern()
        val matcher = pattern.matcher(text)
        
        // Remove old spans efficiently
        val oldSpans = editable.getSpans(0, editable.length, ForegroundColorSpan::class.java)
        for (span in oldSpans) {
            editable.removeSpan(span)
        }

        // Apply new spans
        while (matcher.find()) {
            val type = mapGroupToType(matcher)
            if (type != null) {
                editable.setSpan(
                    ForegroundColorSpan(theme.getColor(type)),
                    matcher.start(),
                    matcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    fun cancel() {
        // No-op in sync version
    }
}
