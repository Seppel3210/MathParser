package de.seppel3210.mathParser

import de.seppel3210.mathParser.parser.parse

fun main() {
    val expr = Parser(Lexer("ln x").lex()).expression()
    println(expr)
    println(expr.reduce())
    println(expr.reduce().derive("x").reduce())
    println(expr.reduce().derive("x"))
}

