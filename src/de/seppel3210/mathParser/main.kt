package de.seppel3210.mathParser

fun main() {
    val expression = Subtraction(Multiplication(Constant(100.0), Constant(100.0)), Constant(680.0))
    println(expression)
    println(expression.reduce())
}
