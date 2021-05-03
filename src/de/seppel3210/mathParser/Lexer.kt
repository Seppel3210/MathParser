package de.seppel3210.mathParser

class Lexer(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var startLine = 1
    private var startColumn = 0
    private var currentLine = 1
    private var currentColumn = 0

    private fun peek(): Char = if (atEnd()) {
        '\u0000'
    } else {
        source[current]
    }

    private fun peekNext(): Char = if (current + 1 >= source.length) {
        '\u0000'
    } else {
        source[current + 1]
    }

    private fun advance(): Char {
        currentColumn++
        return source[current++]
    }

    private fun atEnd() = current >= source.length

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        tokens.add(Token(type, source.substring(start until current), Pair(startLine, startColumn), literal))
    }

    private fun lexToken() {
        when (advance()) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            '*' -> addToken(TokenType.STAR)
            '/' -> addToken(TokenType.SLASH)
            '^' -> addToken(TokenType.CARET)
            '\n' -> {
                currentColumn = 0
                currentLine++
            }
            in 'a'..'z', in 'A'..'Z' -> ident()
            in '0'..'9' -> number()
            '\t', ' ' -> Unit
        }
    }

    private fun ident() {
        while (peek().isLetterOrDigit() || peek() == '_')
            advance()
        addToken(TokenType.IDENT)
    }

    private fun number() {
        while (peek().isDigit()) advance()

        // Look for a fractional part.
        if (peek() == '.' && peekNext().isDigit()) {
            // Consume the "."
            advance()

            while (peek().isDigit()) advance()
        }

        addToken(TokenType.NUMBER, source.substring(start until current).toDouble())
    }

    fun lex(): List<Token> {
        while (!atEnd()) {
            start = current
            startColumn = currentColumn
            startLine = currentLine
            lexToken()
        }
        tokens.add(Token(TokenType.EOF, "", Pair(currentLine, currentColumn), null))
        return tokens.toList()
    }
}