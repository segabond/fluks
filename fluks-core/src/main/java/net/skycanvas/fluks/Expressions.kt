package net.skycanvas.fluks


abstract class Expression {
    abstract fun render(): String

    internal fun escapedString(value: Any, escape: String = "'"): String {
        val ret: String = when (value) {
            is String -> value.replace(Regex("[\\\\$escape]"), "\\$0")
            is Int -> value.toString()
            else -> throw UnsupportedTypeException()
        }

        return ret
    }

    internal fun quotedIdentifier(value: Any): String {
        val ret: String = when (value) {
            is String -> value.replace("\"", "\"\"")
            else -> throw UnsupportedTypeException()
        }

        return "\"$ret\""
    }
}


class ScalarExpression<T>(private val value: T) : Expression() {
    override fun render(): String {
        return when (value) {
            is String -> "\'" + escapedString(value) + "\'"
            is Int -> value.toString()
            is Long -> value.toString()
            is Boolean -> if (value) "1" else "0"
            else -> throw UnsupportedTypeException()
        }
    }
}

class SQLiteFunction(private val name: String, vararg val arguments: Expression) : Expression() {
    override fun render(): String {
        return "$name(${arguments.joinToString { it.render() }})"
    }

}

class PatternExpression(private val prefix: Boolean,
                        private val suffix: Boolean,
                        private val value: Any) : Expression() {
    override fun render(): String = "'${if (suffix) "%" else ""}" +
            escapedString(value) +
            "${if (prefix) "%" else ""}')"

}

class Star : Expression() {
    override fun render(): String {
        return "*"
    }

}