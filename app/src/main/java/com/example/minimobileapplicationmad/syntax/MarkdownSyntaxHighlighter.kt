package com.example.minimobileapplicationmad.syntax

import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import java.util.regex.Pattern

class MarkdownSyntaxHighlighter : TextWatcher {

    private val HEADERS = Pattern.compile("(?m)^#{1,6}\\s.*$")
    private val BOLD = Pattern.compile("\\*\\*.*?\\*\\*|__.*?__")
    private val ITALIC = Pattern.compile("\\*.*?\\*|_.*?_")
    private val LISTS = Pattern.compile("(?m)^\\s*[-*+]\\s.*$")
    private val CODE_BLOCKS = Pattern.compile("```(?:.|[\\n\\r])*?```")

    private val HEADER_COLOR = Color.parseColor("#4285F4") // Blue
    private val CODE_COLOR = Color.parseColor("#A9B7C6")   // Light grey
    private val LIST_COLOR = Color.parseColor("#9876AA")   // Purple

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (s == null) return

        // Remove existing spans
        val colorSpans = s.getSpans(0, s.length, ForegroundColorSpan::class.java)
        for (span in colorSpans) {
            s.removeSpan(span)
        }
        val styleSpans = s.getSpans(0, s.length, StyleSpan::class.java)
        for (span in styleSpans) {
            s.removeSpan(span)
        }

        applyHighlighting(s)
    }

    private fun applyHighlighting(s: Editable) {
        val text = s.toString()

        // Headers
        val headerMatcher = HEADERS.matcher(text)
        while (headerMatcher.find()) {
            s.setSpan(ForegroundColorSpan(HEADER_COLOR), headerMatcher.start(), headerMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            s.setSpan(StyleSpan(Typeface.BOLD), headerMatcher.start(), headerMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Bold
        val boldMatcher = BOLD.matcher(text)
        while (boldMatcher.find()) {
            s.setSpan(StyleSpan(Typeface.BOLD), boldMatcher.start(), boldMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Italic
        val italicMatcher = ITALIC.matcher(text)
        while (italicMatcher.find()) {
            s.setSpan(StyleSpan(Typeface.ITALIC), italicMatcher.start(), italicMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Lists
        val listMatcher = LISTS.matcher(text)
        while (listMatcher.find()) {
            s.setSpan(ForegroundColorSpan(LIST_COLOR), listMatcher.start(), listMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Code Blocks
        val codeMatcher = CODE_BLOCKS.matcher(text)
        while (codeMatcher.find()) {
            s.setSpan(ForegroundColorSpan(CODE_COLOR), codeMatcher.start(), codeMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}
