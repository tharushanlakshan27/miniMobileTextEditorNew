package com.example.minimobileapplicationmad.editor.syntax

import java.util.regex.Pattern

object RegexPatterns {
    private const val COMMENT_MULTILINE = "/\\*[\\s\\S]*?\\*/"
    private const val COMMENT_SINGLELINE = "//.*"
    private const val STRING = "\"(?:\\\\.|[^\"])*\""
    private const val CHAR = "'(?:\\\\.|[^'])*'"
    private const val NUMBER = "\\b\\d+L?\\b|\\b0x[\\da-fA-F]+L?\\b|\\b\\d*\\.\\d+(?:[eE][+-]?\\d+)?[fFdD]?\\b"
    private const val ANNOTATION = "@[a-zA-Z_]\\w*"
    private const val FUNCTION = "\\b[a-zA-Z_]\\w*(?=\\s*\\()"
    private const val CLASS_NAME = "\\b[A-Z]\\w*\\b"
    private const val OPERATOR = "[\\+\\-\\*\\/\\%\\=\\!\\<\\>\\&\\|\\^\\?\\:\\~]+"
    private const val BRACKET = "[\\(\\)\\[\\]\\{\\}]"
    
    fun getKotlinPattern(keywords: Set<String>): Pattern {
        val keywordPattern = "\\b(${keywords.joinToString("|")})\\b"
        
        val patternString = StringBuilder()
        // Use non-capturing groups for internal parts, capturing groups for main tokens
        patternString.append("($COMMENT_MULTILINE|$COMMENT_SINGLELINE)") // 1
        patternString.append("|($STRING)") // 2
        patternString.append("|($CHAR)") // 3
        patternString.append("|($keywordPattern)") // 4
        patternString.append("|($ANNOTATION)") // 5
        patternString.append("|($FUNCTION)") // 6
        patternString.append("|($CLASS_NAME)") // 7
        patternString.append("|($NUMBER)") // 8
        patternString.append("|($OPERATOR)") // 9
        patternString.append("|($BRACKET)") // 10
        
        return Pattern.compile(patternString.toString(), Pattern.DOTALL)
    }

    fun getMarkdownPattern(): Pattern {
        val patternString = StringBuilder()
        patternString.append("^(#{1,6}\\s.*$)") // 1: Headers
        patternString.append("|(\\*\\*.*?\\*\\*|__.*?__)") // 2: Bold
        patternString.append("|(\\*.*?\\*|_.*?_)") // 3: Italic
        patternString.append("|(`[^`]+`)") // 4: Inline Code
        patternString.append("|(```[\\s\\S]*?```)") // 5: Code Block
        patternString.append("|(^>\\s.*$)") // 6: Block Quote
        patternString.append("|(^\\s*[\\*\\-\\+]\\s.*$)") // 7: Unordered List
        patternString.append("|(^\\s*\\d+\\.\\s.*$)") // 8: Ordered List
        patternString.append("|(!\\[.*?\\]\\(.*?\\))") // 9: Image
        patternString.append("|(\\[.*?\\]\\(.*?\\))") // 10: Link
        patternString.append("|(^-{3,}|\\* {3,}|_{3,})") // 11: Horizontal Rule
        patternString.append("|(<[^>]*>)") // 12: HTML Tag
        
        return Pattern.compile(patternString.toString(), Pattern.MULTILINE)
    }
}
