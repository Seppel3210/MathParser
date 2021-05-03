package de.seppel3210.mathParser

import de.seppel3210.mathParser.parser.parse

fun main() {
    val expr = parse("(1^2) + 3")
    println(Lexer("(1^2) + 3.1 * x").lex())
    println(expr)
}

