package de.seppel3210.mathParser

enum class TokenType {
    LEFT_PAREN, RIGHT_PAREN,
    MINUS, PLUS, SLASH, STAR, CARET,
    IDENT, NUMBER,
    EOF,
}