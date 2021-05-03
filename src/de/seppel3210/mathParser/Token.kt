package de.seppel3210.mathParser

class Token(val type: TokenType, val lexeme: String, val position: Pair<Int, Int>, val literal: Any?) {
    override fun toString(): String {

        return "$type $lexeme ${position.first}:${position.second}"
    }
}