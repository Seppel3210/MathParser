package de.seppel3210.mathParser

import de.seppel3210.mathParser.parser.parse

fun main() {
    val expr = parse("((x * 2) ^ 4) + 3")
    println(expr)
    println(expr.reduce())
    println(expr.derive("x").reduce())
    val expr2 = parse("(2 * (x ^ 4)) + (3 * (x ^ 2)) + 3")
    println(expr2)
    println(expr2.reduce())
    println(expr2.derive("x").reduce())
}

