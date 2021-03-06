package de.seppel3210.mathParser.expression

import kotlin.math.ln
import kotlin.math.pow

enum class Precedence {
    Lowest,
    Sum,
    Product,
    Power,
    PowerLeft,
    Highest,
}

abstract class Expression {
    abstract fun reduce(): Expression
    abstract fun derive(variableName: String): Expression
    abstract fun substitute(variableName: String, expr: Expression): Expression
    operator fun plus(rhs: Expression) = Addition(this, rhs)
    operator fun minus(rhs: Expression) = Subtraction(this, rhs)
    operator fun unaryMinus() = Minus(this)
    operator fun times(rhs: Expression) = Multiplication(this, rhs)
    operator fun div(rhs: Expression) = Division(this, rhs)
    abstract fun prettyPrint(outerPrecedence: Precedence): String
}

class Constant(val value: Double) : Expression() {
    override fun reduce(): Expression {
        return this
    }

    override fun derive(variableName: String): Expression {
        return Constant(0.0)
    }

    override fun substitute(variableName: String, expr: Expression): Expression {
        return this
    }

    override fun prettyPrint(outerPrecedence: Precedence): String {
        return toString()
    }

    override fun toString(): String {
        return "$value"
    }
}

class Variable(val name: String) : Expression() {
    override fun reduce(): Expression {
        return this
    }

    override fun derive(variableName: String): Expression {
        return if (variableName == this.name) {
            Constant(1.0)
        } else {
            Constant(0.0)
        }
    }

    override fun substitute(variableName: String, expr: Expression): Expression {
        return if (variableName == name) {
            expr
        } else {
            this
        }
    }

    override fun prettyPrint(outerPrecedence: Precedence): String {
        return toString()
    }

    override fun toString(): String {
        return name
    }
}

class Minus(private val expr: Expression) : Expression() {
    override fun reduce(): Expression {
        val expr = expr.reduce()
        return if (expr is Constant) {
            Constant(-expr.value)
        } else {
            Minus(expr)
        }
    }

    override fun derive(variableName: String): Expression {
        return Minus(expr.derive(variableName))
    }

    override fun substitute(variableName: String, expr: Expression): Expression {
        return -expr.substitute(variableName, expr)
    }

    override fun prettyPrint(outerPrecedence: Precedence): String {
        return "(-${expr.prettyPrint(Precedence.Highest)})"
    }

    override fun toString(): String {
        return "(-$expr)"
    }
}

class Multiplication(private val left: Expression, private val right: Expression) : Expression() {
    override fun reduce(): Expression {
        val reducedLeft = left.reduce()
        val reducedRight = right.reduce()

        return when {
            reducedLeft is Constant && reducedLeft.value == 0.0 -> Constant(0.0)
            reducedRight is Constant && reducedRight.value == 0.0 -> Constant(0.0)
            reducedLeft is Constant && reducedLeft.value == 1.0 -> reducedRight
            reducedRight is Constant && reducedRight.value == 1.0 -> reducedLeft
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value * reducedRight.value)

            reducedRight is Constant
                    && reducedLeft is Multiplication
                    && reducedLeft.left is Constant
            ->
                Constant(reducedRight.value * reducedLeft.left.value) * reducedLeft.right

            reducedRight is Constant
                    && reducedLeft is Multiplication
                    && reducedLeft.right is Constant
            ->
                Constant(reducedRight.value * reducedLeft.right.value) * reducedLeft.left

            reducedLeft is Constant
                    && reducedRight is Multiplication
                    && reducedRight.left is Constant
            ->
                Constant(reducedLeft.value * reducedRight.left.value) * reducedRight

            reducedLeft is Constant
                    && reducedRight is Multiplication
                    && reducedRight.right is Constant
            ->
                Constant(reducedLeft.value * reducedRight.right.value) * reducedRight

            else -> Multiplication(reducedLeft, reducedRight)
        }
    }

    override fun derive(variableName: String): Expression {
        return (left.derive(variableName) * right) + (right.derive(variableName) * left)
    }

    override fun substitute(variableName: String, expr: Expression): Expression {
        return left.substitute(variableName, expr) * right.substitute(variableName, expr)
    }

    override fun prettyPrint(outerPrecedence: Precedence): String {
        val precedence = Precedence.Product
        val inner = "${left.prettyPrint(precedence)} * ${right.prettyPrint(precedence)}"
        return if (outerPrecedence <= precedence) {
            inner
        } else {
            "($inner)"
        }
    }

    override fun toString(): String {
        return "($left * $right)"
    }
}

class Addition(private val left: Expression, private val right: Expression) : Expression() {
    override fun reduce(): Expression {
        val reducedLeft = left.reduce()
        val reducedRight = right.reduce()

        return when {
            reducedLeft is Constant && reducedLeft.value == 0.0 -> reducedRight
            reducedRight is Constant && reducedRight.value == 0.0 -> reducedLeft
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value + reducedRight.value)

            reducedRight is Constant
                    && reducedLeft is Addition
                    && reducedLeft.left is Constant
            ->
                Constant(reducedRight.value + reducedLeft.left.value) + reducedLeft.right

            reducedRight is Constant
                    && reducedLeft is Addition
                    && reducedLeft.right is Constant
            ->
                Constant(reducedRight.value + reducedLeft.right.value) + reducedLeft.left

            reducedLeft is Constant
                    && reducedRight is Addition
                    && reducedRight.left is Constant
            ->
                Constant(reducedLeft.value + reducedRight.left.value) + reducedRight

            reducedLeft is Constant
                    && reducedRight is Addition
                    && reducedRight.right is Constant
            ->
                Constant(reducedLeft.value + reducedRight.right.value) + reducedRight
            else -> Addition(reducedLeft, reducedRight)
        }
    }

    override fun derive(variableName: String): Expression {
        return left.derive(variableName) + right.derive(variableName)
    }

    override fun substitute(variableName: String, expr: Expression): Expression {
        return left.substitute(variableName, expr) + right.substitute(variableName, expr)
    }

    override fun prettyPrint(outerPrecedence: Precedence): String {
        val precedence = Precedence.Sum
        val inner = "${left.prettyPrint(precedence)} + ${right.prettyPrint(precedence)}"
        return if (outerPrecedence <= precedence) {
            inner
        } else {
            "($inner)"
        }
    }

    override fun toString(): String {
        return "($left + $right)"
    }
}

class Subtraction(private val left: Expression, private val right: Expression) : Expression() {
    override fun reduce(): Expression {
        val reducedLeft = left.reduce()
        val reducedRight = right.reduce()

        return when {
            reducedLeft is Constant && reducedLeft.value == 0.0 -> (Constant(-1.0) * reducedRight).reduce()
            reducedRight is Constant && reducedRight.value == 0.0 -> reducedLeft
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value - reducedRight.value)
            else -> Subtraction(reducedLeft, reducedRight)
        }
    }

    override fun derive(variableName: String): Expression {
        return left.derive(variableName) - right.derive(variableName)
    }

    override fun substitute(variableName: String, expr: Expression): Expression {
        return left.substitute(variableName, expr) - right.substitute(variableName, expr)
    }

    override fun prettyPrint(outerPrecedence: Precedence): String {
        val precedence = Precedence.Sum
        val inner = "${left.prettyPrint(precedence)} - ${right.prettyPrint(precedence)}"
        return if (outerPrecedence <= precedence) {
            inner
        } else {
            "($inner)"
        }
    }

    override fun toString(): String {
        return "($left - $right)"
    }
}

class Division(private val left: Expression, private val right: Expression) : Expression() {
    override fun reduce(): Expression {
        val reducedLeft = left.reduce()
        val reducedRight = right.reduce()

        return when {
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value / reducedRight.value)
            reducedLeft is Constant && reducedLeft.value == 0.0 -> Constant(0.0)
            reducedRight is Constant && reducedRight.value == 1.0 -> reducedLeft
            else -> Division(reducedLeft, reducedRight)
        }
    }

    override fun derive(variableName: String): Expression {
        return ((left.derive(variableName) * right) - (right.derive(variableName) * left)) / Power(right, Constant(2.0))
    }

    override fun substitute(variableName: String, expr: Expression): Expression {
        return left.substitute(variableName, expr) / right.substitute(variableName, expr)
    }

    override fun prettyPrint(outerPrecedence: Precedence): String {
        val precedence = Precedence.Product
        val inner = "${left.prettyPrint(precedence)} / ${right.prettyPrint(precedence)}"
        return if (outerPrecedence <= precedence) {
            inner
        } else {
            "($inner)"
        }
    }

    override fun toString(): String {
        return "($left / $right)"
    }
}

class Power(private val left: Expression, private val right: Expression) : Expression() {
    override fun reduce(): Expression {
        val reducedLeft = left.reduce()
        val reducedRight = right.reduce()

        return when {
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value.pow(reducedRight.value))
            reducedRight is Constant && reducedRight.value == 1.0 -> reducedLeft
            reducedLeft is Power -> Power(reducedLeft.left, (reducedLeft.right * reducedRight).reduce())
            else -> Power(reducedLeft, reducedRight)
        }
    }

    override fun derive(variableName: String): Expression {
        return (right.derive(variableName) * NaturalLog(left) * this) +
                (right * left.derive(variableName) * Power(left, right - Constant(1.0)))
    }

    override fun substitute(variableName: String, expr: Expression): Expression {
        return Power(left.substitute(variableName, expr), right.substitute(variableName, expr))
    }

    override fun prettyPrint(outerPrecedence: Precedence): String {
        val precedence = Precedence.Power
        val inner = "${left.prettyPrint(Precedence.PowerLeft)} ^ ${right.prettyPrint(precedence)}"
        return if (outerPrecedence <= precedence) {
            inner
        } else {
            "($inner)"
        }
    }

    override fun toString(): String {
        return "($left ^ $right)"
    }
}

class NaturalLog(private val arg: Expression) : Expression() {
    override fun reduce(): Expression {
        return when (val reducedArg = arg.reduce()) {
            is Constant -> Constant(ln(reducedArg.value))
            // Hack: special case for variables called "e"
            // TODO: maybe special case constants like e and pi in the lexer or parser?
            is Variable -> if (reducedArg.name == "e") Constant(1.0) else NaturalLog(reducedArg)
            else -> NaturalLog(reducedArg)
        }
    }

    override fun derive(variableName: String): Expression {
        return (Constant(1.0) / arg) * arg.derive(variableName)
    }

    override fun substitute(variableName: String, expr: Expression): Expression {
        return NaturalLog(arg.substitute(variableName, expr))
    }

    override fun prettyPrint(outerPrecedence: Precedence): String {
        return "ln(${arg.prettyPrint(Precedence.Lowest)})"
    }

    override fun toString(): String {
        return "ln($arg)"
    }
}