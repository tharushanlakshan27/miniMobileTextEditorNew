package com.example.minimobileapplicationmad.editor.syntax

import android.graphics.Color

data class SyntaxTheme(
    val colors: Map<TokenType, Int>
) {
    companion object {
        fun createDefaultDark(): SyntaxTheme {
            return SyntaxTheme(
                mapOf(
                    TokenType.KEYWORD to Color.parseColor("#CF8E6D"),     // Blue/Orange (IntelliJ Dark)
                    TokenType.STRING to Color.parseColor("#6A8759"),      // Green
                    TokenType.CHARACTER to Color.parseColor("#6A8759"),   // Green
                    TokenType.NUMBER to Color.parseColor("#6897BB"),      // Light Blue/Purple
                    TokenType.COMMENT to Color.parseColor("#808080"),     // Gray
                    TokenType.ANNOTATION to Color.parseColor("#BBB529"),  // Yellow
                    TokenType.FUNCTION to Color.parseColor("#FFC66D"),    // Yellow/Orange
                    TokenType.CLASS_NAME to Color.parseColor("#A9B7C6"),  // Light Gray
                    TokenType.VARIABLE to Color.parseColor("#A9B7C6"),    // Light Gray
                    TokenType.OPERATOR to Color.parseColor("#BCBEC4"),    // Whiteish
                    TokenType.BRACKET to Color.parseColor("#BCBEC4"),     // Whiteish
                    TokenType.IMPORT to Color.parseColor("#CF8E6D"),      // Same as keyword
                    TokenType.PACKAGE to Color.parseColor("#CF8E6D"),     // Same as keyword
                    TokenType.BOOLEAN to Color.parseColor("#CF8E6D"),     // Same as keyword
                    TokenType.NULL to Color.parseColor("#CF8E6D"),        // Same as keyword
                    
                    // Markdown Dark
                    TokenType.MD_HEADER to Color.parseColor("#CC7832"),
                    TokenType.MD_BOLD to Color.parseColor("#A9B7C6"),
                    TokenType.MD_ITALIC to Color.parseColor("#A9B7C6"),
                    TokenType.MD_CODE to Color.parseColor("#6A8759"),
                    TokenType.MD_QUOTE to Color.parseColor("#808080"),
                    TokenType.MD_LIST to Color.parseColor("#CC7832"),
                    TokenType.MD_LINK to Color.parseColor("#589DF6"),
                    TokenType.MD_IMAGE to Color.parseColor("#589DF6"),
                    TokenType.MD_RULE to Color.parseColor("#808080")
                )
            )
        }

        fun createDefaultLight(): SyntaxTheme {
            return SyntaxTheme(
                mapOf(
                    TokenType.KEYWORD to Color.parseColor("#0033B3"),     // Blue
                    TokenType.STRING to Color.parseColor("#067D17"),      // Green
                    TokenType.CHARACTER to Color.parseColor("#067D17"),   // Green
                    TokenType.NUMBER to Color.parseColor("#1750EB"),      // Blue
                    TokenType.COMMENT to Color.parseColor("#8C8C8C"),     // Gray
                    TokenType.ANNOTATION to Color.parseColor("#9E880D"),  // Dark Yellow
                    TokenType.FUNCTION to Color.parseColor("#00627A"),    // Teal
                    TokenType.CLASS_NAME to Color.parseColor("#000000"),  // Black
                    TokenType.VARIABLE to Color.parseColor("#000000"),    // Black
                    TokenType.OPERATOR to Color.parseColor("#000000"),    // Black
                    TokenType.BRACKET to Color.parseColor("#000000"),     // Black
                    TokenType.IMPORT to Color.parseColor("#0033B3"),
                    TokenType.PACKAGE to Color.parseColor("#0033B3"),
                    TokenType.BOOLEAN to Color.parseColor("#0033B3"),
                    TokenType.NULL to Color.parseColor("#0033B3"),
                    
                    // Markdown Light
                    TokenType.MD_HEADER to Color.parseColor("#0033B3"),
                    TokenType.MD_BOLD to Color.parseColor("#000000"),
                    TokenType.MD_ITALIC to Color.parseColor("#000000"),
                    TokenType.MD_CODE to Color.parseColor("#067D17"),
                    TokenType.MD_QUOTE to Color.parseColor("#8C8C8C"),
                    TokenType.MD_LIST to Color.parseColor("#0033B3"),
                    TokenType.MD_LINK to Color.parseColor("#287BDE"),
                    TokenType.MD_IMAGE to Color.parseColor("#287BDE"),
                    TokenType.MD_RULE to Color.parseColor("#8C8C8C")
                )
            )
        }
    }

    fun getColor(type: TokenType): Int {
        return colors[type] ?: Color.BLACK
    }
}
