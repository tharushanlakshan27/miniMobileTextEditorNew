package com.example.minimobileapplicationmad.editor.syntax

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import java.util.regex.Pattern

/**
 * A simplified, self-contained highlighter to ensure instant red keywords.
 */
class KotlinSyntaxHighlighter(
    private val context: Context,
    private val theme: SyntaxTheme
) : TextWatcher {

    private val KEYWORDS = setOf(
        "package", "import", "class", "interface", "fun", "val", "var", 
        "if", "else", "return", "when", "for", "while", "is", "as", "in", 
        "object", "typealias", "this", "super", "try", "catch", "finally", 
        "throw", "break", "continue", "do", "init", "constructor", "get", 
        "set", "true", "false", "null"
    )
    
    private val pattern: Pattern = Pattern.compile("\\b(${KEYWORDS.joinToString("|")})\\b")
    private var isWorking = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isWorking || s == null) return
        
        isWorking = true
        
        // Clear all foreground spans first
        val oldSpans = s.getSpans(0, s.length, ForegroundColorSpan::class.java)
        for (span in oldSpans) {
            s.removeSpan(span)
        }

        val text = s.toString()
        val matcher = pattern.matcher(text)
        
        while (matcher.find()) {
            s.setSpan(
                ForegroundColorSpan(Color.RED),
                matcher.start(),
                matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        isWorking = false
    }
}
