package de.seppel3210.mathParser

import de.seppel3210.mathParser.parser.parse

fun main() {
    val expr = parse("( ( x * 2 ) ^ 4 ) + 3")
    println(expr)
    println(expr.reduce())
    println(expr.derive("x").reduce())
}

