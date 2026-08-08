package com.example.minimobileapplicationmad.editor.syntax

enum class TokenType {
    KEYWORD,
    STRING,
    CHARACTER,
    NUMBER,
    COMMENT,
    ANNOTATION,
    FUNCTION,
    CLASS_NAME,
    VARIABLE,
    OPERATOR,
    BRACKET,
    IMPORT,
    PACKAGE,
    BOOLEAN,
    NULL,
    PLAIN_TEXT,
    
    // Markdown
    MD_HEADER,
    MD_BOLD,
    MD_ITALIC,
    MD_CODE,
    MD_QUOTE,
    MD_LIST,
    MD_LINK,
    MD_IMAGE,
    MD_RULE
}
