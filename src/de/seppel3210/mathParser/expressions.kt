package de.seppel3210.mathParser

import kotlin.math.pow

interface Expression {
    fun reduce(): Expression
}

class Constant(val value: Double) : Expression {
    override fun reduce(): Expression {
        return this
    }

    override fun toString(): String {
        return "$value"
    }
}

class Variable(val name: String) : Expression {
    override fun reduce(): Expression {
        return this
    }

    override fun toString(): String {
        return name
    }
}

class Multiplication(private val left: Expression, private val right: Expression) : Expression {
    override fun reduce(): Expression {
        val reducedLeft = left.reduce()
        val reducedRight = right.reduce()

        return when {
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value * reducedRight.value)
            else -> Multiplication(reducedLeft, reducedRight)
        }
    }

    override fun toString(): String {
        return "($left * $right)"
    }
}

class Addition(private val left: Expression, private val right: Expression) : Expression {
    override fun reduce(): Expression {
        val reducedLeft = left.reduce()
        val reducedRight = right.reduce()

        return when {
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value + reducedRight.value)
            else -> Addition(reducedLeft, reducedRight)
        }
    }

    override fun toString(): String {
        return "($left + $right)"
    }
}

class Subtraction(private val left: Expression, private val right: Expression) : Expression {
    override fun reduce(): Expression {
        val reducedLeft = left.reduce()
        val reducedRight = right.reduce()

        return when {
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value - reducedRight.value)
            else -> Subtraction(reducedLeft, reducedRight)
        }
    }

    override fun toString(): String {
        return "($left - $right)"
    }
}

class Division(private val left: Expression, private val right: Expression) : Expression {
    override fun reduce(): Expression {
        val reducedLeft = left.reduce()
        val reducedRight = right.reduce()

        return when {
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value / reducedRight.value)
            else -> Subtraction(reducedLeft, reducedRight)
        }
    }

    override fun toString(): String {
        return "($left / $right)"
    }
}
class Power(private val left: Expression, private val right: Expression) : Expression {
    override fun reduce(): Expression {
        val reducedLeft = left.reduce()
        val reducedRight = right.reduce()

        return when {
            reducedLeft is Constant && reducedRight is Constant -> Constant(reducedLeft.value.pow(reducedRight.value))
            else -> Subtraction(reducedLeft, reducedRight)
        }
    }

    override fun toString(): String {
        return "($left ^ $right)"
    }
}
