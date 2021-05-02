package de.seppel3210.mathParser.parser

import de.seppel3210.mathParser.expression.*

const val openParen = '('
const val closingParen = ')'

class InvalidSyntax(message: String) : Exception("Invalid syntax: $message")

fun parse(mathExpression: String): Expression {
    val tokens = lex(mathExpression)
    return parse(tokens)
}

private fun lex(mathExpression: String): List<String> {
    val tokens = mutableListOf<String>()
    var nextToken = ""
    for (c in mathExpression) {
        when (c) {
            in listOf(openParen, closingParen, '+', '-', '*', '^', '/') -> {
                if (nextToken.isNotEmpty()) {
                    tokens.add(nextToken)
                    nextToken = ""
                }
                tokens.add("$c")
            }
            in listOf(' ', '\t', '\n') -> {
                if (nextToken.isNotEmpty()) {
                    tokens.add(nextToken)
                    nextToken = ""
                }
            }
            else -> nextToken += c
        }
    }
    if (nextToken.isNotEmpty()) {
        tokens.add(nextToken)
    }
    return tokens
}

private fun parse(tokenList: List<String>): Expression {
    var tokens = tokenList

    // strip unnecessary parentheses
    if (tokens.first() == "$openParen" && tokens.last() == "$closingParen") {
        tokens = tokens.drop(1).dropLast(1)
    }

    if (tokens.size == 1) {
        val value = tokens.first().toDoubleOrNull()
        return if (value != null) {
            Constant(value)
        } else {
            Variable(tokens.first())
        }
    }

    val leftTokens = nextExpressionTokens(tokens)
    tokens = tokens.drop(leftTokens.size) // advance tokens by one expression
    val operation = tokens.first()
    val rightTokens = tokens.drop(1)

    val leftExpression = parse(leftTokens)
    val rightExpression = parse(rightTokens)
    return when (operation) {
        "+" -> Addition(leftExpression, rightExpression)
        "-" -> Subtraction(leftExpression, rightExpression)
        "*" -> Multiplication(leftExpression, rightExpression)
        "/" -> Division(leftExpression, rightExpression)
        "^" -> Power(leftExpression, rightExpression)
        else -> throw InvalidSyntax(operation)
    }
}

private fun nextExpressionTokens(tokens: List<String>): List<String> {
    var unclosedParens = 0
    var index = 0
    do {
        when (tokens[index]) {
            "$openParen" -> unclosedParens++
            "$closingParen" -> unclosedParens--
        }
        index++
    } while (unclosedParens != 0)
    return tokens.slice(0 until index)
}