package com.example.minimobileapplicationmad.editor.syntax

import android.content.Context
import java.util.regex.Matcher
import java.util.regex.Pattern

class KotlinSyntaxHighlighter(
    context: Context,
    theme: SyntaxTheme = SyntaxTheme.createDefaultDark()
) : SyntaxHighlighter(theme) {

    private val keywords = KeywordLoader.getKotlinKeywords(context)
    private val pattern = RegexPatterns.getKotlinPattern(keywords)

    override fun getPattern(): Pattern = pattern

    override fun mapGroupToType(matcher: Matcher): TokenType? {
        return when {
            matcher.group(1) != null -> TokenType.COMMENT
            matcher.group(2) != null -> TokenType.STRING
            matcher.group(3) != null -> TokenType.CHARACTER
            matcher.group(4) != null -> {
                val word = matcher.group(4)
                when (word) {
                    "package" -> TokenType.PACKAGE
                    "import" -> TokenType.IMPORT
                    "true", "false" -> TokenType.BOOLEAN
                    "null" -> TokenType.NULL
                    else -> TokenType.KEYWORD
                }
            }
            matcher.group(5) != null -> TokenType.ANNOTATION
            matcher.group(6) != null -> TokenType.FUNCTION
            matcher.group(7) != null -> TokenType.CLASS_NAME
            matcher.group(8) != null -> TokenType.NUMBER
            matcher.group(9) != null -> TokenType.OPERATOR
            matcher.group(10) != null -> TokenType.BRACKET
            else -> null
        }
    }
}
