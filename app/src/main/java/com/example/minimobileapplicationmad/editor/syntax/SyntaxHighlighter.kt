package com.example.minimobileapplicationmad.editor.syntax

import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import kotlinx.coroutines.*
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class SyntaxHighlighter(
    protected var theme: SyntaxTheme
) : TextWatcher {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)
    private var highlightJob: Job? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        s?.let {
            highlightJob?.cancel()
            highlightJob = scope.launch {
                delay(300) // Debounce
                applyHighlighting(it)
            }
        }
    }

    protected abstract fun getPattern(): Pattern
    protected abstract fun mapGroupToType(matcher: Matcher): TokenType?

    private suspend fun applyHighlighting(editable: Editable) {
        val text = editable.toString()
        val pattern = getPattern()
        val matcher = pattern.matcher(text)
        
        val tokens = mutableListOf<Token>()
        
        // Scan for tokens in background
        while (matcher.find()) {
            val type = mapGroupToType(matcher)
            if (type != null) {
                tokens.add(Token(type, matcher.start(), matcher.end()))
            }
        }

        // Apply spans on Main thread
        withContext(Dispatchers.Main) {
            // Check if text has changed significantly while we were processing
            // This is a simple check, in a real editor we might need more complex logic
            
            // Remove old spans efficiently
            val oldSpans = editable.getSpans(0, editable.length, ForegroundColorSpan::class.java)
            for (span in oldSpans) {
                editable.removeSpan(span)
            }

            // Apply new spans
            for (token in tokens) {
                // Ensure token is still within bounds
                if (token.start < editable.length && token.end <= editable.length) {
                    editable.setSpan(
                        ForegroundColorSpan(theme.getColor(token.type)),
                        token.start,
                        token.end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
    }

    fun cancel() {
        job.cancel()
    }
}
