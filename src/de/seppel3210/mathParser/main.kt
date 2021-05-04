package de.seppel3210.mathParser

import de.seppel3210.mathParser.parser.parse

fun main() {
    val expr = Parser(Lexer("e^(-x)*(-x^2+x+3)").lex()).expression()
    println(expr)
    println(expr.reduce())
    println(expr.reduce().derive("x").reduce())
}

