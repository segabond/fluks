package net.skycanvas.fluks


interface Expression {
    fun render(): String
}


class ScalarExpression<T>(private val value: T) : Expression {

    override fun render(): String {
        return when (value) {
            is String -> "\'" + escapedString(value) + "\'"
            is Int -> value.toString()
            is Long -> value.toString()
            is Boolean -> if (value) "1" else "0"
            is Double -> value.toString()
            is Float -> value.toString()
            is ByteArray -> "X'" + value.toHex() + "'"

            else -> throw UnsupportedTypeException("Type of value ${(value as Any)::class.java} is unsupported")
        }
    }
}

class SQLiteFunction(private val name: String, vararg val arguments: Expression) : Expression {
    override fun render(): String {
        return "$name(${arguments.joinToString { it.render() }})"
    }

}

class PatternExpression(private val prefix: Boolean,
                        private val suffix: Boolean,
                        private val value: Any) : Expression {
    override fun render(): String = "'${if (suffix) "%" else ""}" +
            escapedString(value) +
            "${if (prefix) "%" else ""}')"

}

class Star : Expression {
    override fun render(): String {
        return "*"
    }

}