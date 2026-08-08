package com.example.minimobileapplicationmad.syntax

import android.graphics.Color
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import java.util.regex.Pattern

class KotlinSyntaxHighlighter : TextWatcher {

    private val KEYWORDS = Pattern.compile(
        "\\b(package|import|class|interface|fun|val|var|if|else|return|when|for|while|is|as|in|object|typealias|this|super|try|catch|finally|throw|break|continue|do|init|constructor|get|set|field|property|receiver|param|sparam|delegate|file|expect|actual|companion|header|impl)\\b"
    )

    private val KEYWORD_COLOR = Color.RED

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (s == null) return
        
        // Remove existing ForegroundColorSpans to reset to black
        val spans = s.getSpans(0, s.length, ForegroundColorSpan::class.java)
        for (span in spans) {
            s.removeSpan(span)
        }

        applyHighlighting(s)
    }

    private fun applyHighlighting(s: Editable) {
        val text = s.toString()

        // Apply keywords in green
        val keywordMatcher = KEYWORDS.matcher(text)
        while (keywordMatcher.find()) {
            s.setSpan(ForegroundColorSpan(KEYWORD_COLOR), keywordMatcher.start(), keywordMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}
