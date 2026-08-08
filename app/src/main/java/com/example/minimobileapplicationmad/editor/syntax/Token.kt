package com.example.minimobileapplicationmad.editor.syntax

data class Token(
    val type: TokenType,
    val start: Int,
    val end: Int
)
