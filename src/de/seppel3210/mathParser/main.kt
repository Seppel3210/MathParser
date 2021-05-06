package de.seppel3210.mathParser

import de.seppel3210.mathParser.expression.Expression
import de.seppel3210.mathParser.expression.Precedence
import kotlin.system.exitProcess

fun main() {
    println("""
        Math Parser CLI
        (c) Sebastian Widua 2021
    """.trimIndent())
    while (true) {
        println("Type an expression:")
        val expressionInput = readLine() ?: return
        val expr = try {
            Parser(Lexer(expressionInput).lex()).expression()
        } catch (e: LexerException) {
            println("Error while lexing: ${e.message}")
            continue
        } catch (e: ParserException) {
            println("Error while parsing: ${e.message}")
            continue
        }
        expressionMenu(expr)
    }
}

val actions: Map<String, (Expression) -> Expression> = mapOf(
        "derive" to {
            print("differentiate with respect to? ")
            val varName = readLine() ?: ""
            it.derive(varName).reduce()
        },
        "substitute" to {
            print("which variable? ")
            val varName = readLine() ?: ""
            print("which expression? ")
            val exprString = readLine() ?: ""
            val expr = Parser(Lexer(exprString).lex()).expression()
            it.substitute(varName, expr)
        },
        "reduce" to {
            it.reduce()
        }
)

fun expressionMenu(expression: Expression) {
    var expr = expression
    println(expr.prettyPrint(Precedence.Lowest))
    while (true) {
        println("actions: ${actions.keys} or \"exit\" to type another expression")
        val actionName = (readLine() ?: exitProcess(0)).trim()
        if (actionName == "exit") return
        val action = actions[actionName]
        if (action == null) {
            println("Unknown action")
            continue
        }
        expr = action(expr)
        println(expr.prettyPrint(Precedence.Lowest))
    }
}