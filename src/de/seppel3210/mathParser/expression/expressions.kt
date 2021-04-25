package de.seppel3210.mathParser.expression

import kotlin.math.ln
import kotlin.math.pow

abstract class Expression {
    abstract fun reduce(): Expression
    abstract fun derive(variableName: String): Expression
    operator fun plus(rhs: Expression) = Addition(this, rhs)
    operator fun minus(rhs: Expression) = Subtraction(this, rhs)
    operator fun times(rhs: Expression) = Multiplication(this, rhs)
    operator fun div(rhs: Expression) = Division(this, rhs)
}

class Constant(val value: Double) : Expression() {
    override fun reduce(): Expression {
        return this
    }

    override fun derive(variableName: String): Expression {
        return Constant(0.0)
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

    override fun toString(): String {
        return name
    }
}

class Multiplication(private val left: Expression, private val right: Expression) : Expression() {
    override fun reduce(): Expression {
        val reducedLeft = left.reduce()
        val reducedRight = right.reduce()

        return when {
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value * reducedRight.value)
            else -> Multiplication(reducedLeft, reducedRight)
        }
    }

    override fun derive(variableName: String): Expression {
        return (left.derive(variableName) * right) + (right.derive(variableName) * left)
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
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value + reducedRight.value)
            else -> Addition(reducedLeft, reducedRight)
        }
    }

    override fun derive(variableName: String): Expression {
        return left.derive(variableName) + right.derive(variableName)
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
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value - reducedRight.value)
            else -> Subtraction(reducedLeft, reducedRight)
        }
    }

    override fun derive(variableName: String): Expression {
        return left.derive(variableName) - right.derive(variableName)
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
            else -> Subtraction(reducedLeft, reducedRight)
        }
    }

    override fun derive(variableName: String): Expression {
        return ((left.derive(variableName) * right) - (right.derive(variableName) * left)) / Power(right, Constant( 2.0))
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
            else -> Power(reducedLeft, reducedRight)
        }
    }

    override fun derive(variableName: String): Expression {
        return (right.derive(variableName) * NaturalLog(left) * this) + (right * left.derive(variableName) * Power(left, right - Constant(1.0)))
    }

    override fun toString(): String {
        return "($left ^ $right)"
    }
}

class NaturalLog(private val arg: Expression) : Expression() {
    override fun reduce(): Expression {
        return when (val reducedArg = arg.reduce()) {
            is Constant -> Constant(ln(reducedArg.value))
            else -> NaturalLog(reducedArg)
        }
    }

    override fun derive(variableName: String): Expression {
        return (Constant(1.0) / arg) * arg.derive(variableName)
    }

    override fun toString(): String {
        return "ln($arg)"
    }
}