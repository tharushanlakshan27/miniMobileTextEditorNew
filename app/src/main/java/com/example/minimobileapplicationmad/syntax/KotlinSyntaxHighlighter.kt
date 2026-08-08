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
    private val STRINGS = Pattern.compile("\"(?:\\\\.|[^\"])*\"")
    private val COMMENTS = Pattern.compile("//.*|/\\*(?:.|[\\n\\r])*?\\*/")
    private val ANNOTATIONS = Pattern.compile("@\\w+")

    private val KEYWORD_COLOR = Color.parseColor("#CF8E6D") // Orange-ish
    private val STRING_COLOR = Color.parseColor("#6A8759")  // Green
    private val COMMENT_COLOR = Color.parseColor("#808080") // Grey
    private val ANNOTATION_COLOR = Color.parseColor("#BBB529") // Yellow-ish

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (s == null) return
        
        // Remove existing spans
        val spans = s.getSpans(0, s.length, ForegroundColorSpan::class.java)
        for (span in spans) {
            s.removeSpan(span)
        }

        applyHighlighting(s)
    }

    private fun applyHighlighting(s: Editable) {
        val text = s.toString()

        // Apply keywords
        val keywordMatcher = KEYWORDS.matcher(text)
        while (keywordMatcher.find()) {
            s.setSpan(ForegroundColorSpan(KEYWORD_COLOR), keywordMatcher.start(), keywordMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Apply annotations
        val annotationMatcher = ANNOTATIONS.matcher(text)
        while (annotationMatcher.find()) {
            s.setSpan(ForegroundColorSpan(ANNOTATION_COLOR), annotationMatcher.start(), annotationMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Apply strings
        val stringMatcher = STRINGS.matcher(text)
        while (stringMatcher.find()) {
            s.setSpan(ForegroundColorSpan(STRING_COLOR), stringMatcher.start(), stringMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Apply comments (should be last to override others)
        val commentMatcher = COMMENTS.matcher(text)
        while (commentMatcher.find()) {
            s.setSpan(ForegroundColorSpan(COMMENT_COLOR), commentMatcher.start(), commentMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}
