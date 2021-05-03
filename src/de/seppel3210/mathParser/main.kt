package de.seppel3210.mathParser

import de.seppel3210.mathParser.parser.parse

fun main() {
    val expr = Parser(Lexer("1^5 + 3 * -1^2").lex()).expression()
    println(expr)
    println(expr.reduce())
}

