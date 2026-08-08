package com.example.minimobileapplicationmad.editor.syntax

import java.util.regex.Matcher
import java.util.regex.Pattern

class MarkdownSyntaxHighlighter(
    theme: SyntaxTheme = SyntaxTheme.createDefaultDark()
) : SyntaxHighlighter(theme) {

    private val pattern = RegexPatterns.getMarkdownPattern()

    override fun getPattern(): Pattern = pattern

    override fun mapGroupToType(matcher: Matcher): TokenType? {
        return when {
            matcher.group(1) != null -> TokenType.MD_HEADER
            matcher.group(2) != null -> TokenType.MD_BOLD
            matcher.group(3) != null -> TokenType.MD_ITALIC
            matcher.group(4) != null -> TokenType.MD_CODE
            matcher.group(5) != null -> TokenType.MD_CODE
            matcher.group(6) != null -> TokenType.MD_QUOTE
            matcher.group(7) != null -> TokenType.MD_LIST
            matcher.group(8) != null -> TokenType.MD_LIST
            matcher.group(9) != null -> TokenType.MD_IMAGE
            matcher.group(10) != null -> TokenType.MD_LINK
            matcher.group(11) != null -> TokenType.MD_RULE
            matcher.group(12) != null -> TokenType.MD_CODE // Use code color for HTML
            else -> null
        }
    }
}
