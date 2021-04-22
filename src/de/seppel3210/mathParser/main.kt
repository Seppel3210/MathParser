package de.seppel3210.mathParser

fun main() {
    val expr = parse("( ( 1 * 2 ) ^ 4 ) + 3")
    println(expr)
    println(expr.reduce())
}
