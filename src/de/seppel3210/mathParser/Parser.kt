package de.seppel3210.mathParser

import de.seppel3210.mathParser.TokenType.*
import de.seppel3210.mathParser.expression.*
import java.lang.RuntimeException

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun expression(): Expression {
        return term()
    }

    private fun term(): Expression {
        var expr = factor()

        while (matches(MINUS, PLUS)) {
            val operator = previous()
            val right = factor()
            expr = when (operator.type) {
                PLUS -> Addition(expr, right)
                MINUS -> Subtraction(expr, right)
                else -> throw RuntimeException("Unreachable")
            }
        }

        return expr
    }

    private fun factor(): Expression {
        var expr = unary()

        while (matches(STAR, SLASH)) {
            val operator = previous()
            val right = unary()
            expr = when (operator.type) {
                STAR -> Multiplication(expr, right)
                SLASH -> Division(expr, right)
                else -> throw RuntimeException("Unreachable")
            }
        }

        return expr
    }

    private fun unary(): Expression {
        return if (matches(MINUS)) {
            val right = unary()
            Minus(right)
        } else {
            power()
        }
    }

    private fun power(): Expression {
        var expr = function()

        if (matches(CARET)) {
            val right = power()
            expr = Power(expr, right)
        }
        return expr
    }

    private fun function(): Expression {
        return if (matches(FUNCTION_LN)) {
            val right = primary()
            NaturalLog(right)
        } else {
            primary()
        }
    }

    private fun primary(): Expression {
        val token = advance()
        return when (token.type) {
            NUMBER -> Constant(token.literal as Double)
            IDENT -> Variable(token.lexeme)
            LEFT_PAREN -> {
                val expression = expression()
                assert(RIGHT_PAREN, "Expect ')' after expression.")
                return expression
            }
            else -> throw ParserException("Unexpected token: `$token`")
        }
    }

    private fun matches(vararg types: TokenType): Boolean {
        for (type in types) {
            if (peek().type == type) {
                advance()
                return true
            }
        }
        return false
    }

    private fun advance(): Token {
        if (current < tokens.size) current++
        return previous()
    }

    private fun peek(): Token {
        return tokens[current]
    }

    private fun previous(): Token {
        return tokens[current - 1]
    }

    private fun assert(type: TokenType, errMsg: String) {
        if (peek().type == type) {
            advance()
        } else {
            throw ParserException(errMsg)
        }
    }
}