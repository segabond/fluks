package net.skycanvas.fluks

/**
 * Syntactic sugar constructor
 */
inline fun <reified T> Column(name: String): Column<T> {
    return Column(name, T::class.java, isMarkedNullable<T>())
}

/**
 * Columns
 */
@Suppress("UNCHECKED_CAST")
class Column<T>(val name: String, val clazz: Class<T>, val isNullable: Boolean) : Expression() {
    fun convert(mixed: Any): T {
        return getTypedFromAny(mixed, clazz) as T
    }

    val type: ColumnType
        get() {
            return if (clazz.isAssignableFrom(String::class.java)) {
                ColumnType.TEXT
            } else if (clazz.isAssignableFrom(Double::class.java)) {
                ColumnType.REAL
            } else if (clazz.isAssignableFrom(ByteArray::class.java)) {
                ColumnType.BLOB
            } else if (clazz.isAssignableFrom(java.lang.Integer::class.java) ||
                    clazz.isAssignableFrom(java.lang.Long::class.java) ||
                    clazz.isAssignableFrom(java.lang.Boolean::class.java)) {
                ColumnType.INTEGER
            } else {
                throw UnsupportedTypeException("Class $clazz is not supported")
            }
        }

    fun eq(value: Any): Predicate = EqualityPredicate(this, ScalarExpression(value))
    fun contains(value: Any): Predicate = PatternPredicate(this, PatternExpression(prefix = true, suffix = true, value = value))
    fun startsWith(value: Any): Predicate = PatternPredicate(this, PatternExpression(prefix = true, suffix = false, value = value))
    fun endsWith(value: Any): Predicate = PatternPredicate(this, PatternExpression(prefix = true, suffix = false, value = value))
    override fun render(): String {
        return quotedIdentifier(name)
    }

    infix fun to(value: T): Setter {
        return Setter(this, ScalarExpression(value))
    }

}


class Setter(val field: Expression, var value: Expression)

/**
 * Tables
 */
class Table(val name: String) : Expression() {
    override fun render(): String {
        return quotedIdentifier(name)
    }

    fun create(function: (builder: TableBuilder) -> Unit): Statement {
        val builder = TableBuilder(this)
        function(builder)
        return CreateStatement(builder)
    }

    fun select(vararg column: Column<*>): SelectStatement {
        return SelectStatement(* column).from(this)
    }

    fun select(): SelectStatement {
        return SelectStatement(Star()).from(this)
    }

    fun insert(vararg values: Setter): InsertStatement {
        return InsertStatement(* values).into(this)
    }

    fun update(vararg values: Setter): UpdateStatement {
        return UpdateStatement(* values).table(this)
    }

    fun delete(): DeleteStatement {
        return DeleteStatement().from(this)
    }

    fun exists(): Statement {
        return SelectStatement(SQLiteFunction("count", Star()))
                .from(Table("sqlite_master"))
                .where(
                        ConjunctionPredicate(
                                Column<String>("type").eq("table"),
                                Column<String>("name").eq(name))
                )
    }


}


/**
 * Table schema maintenance
 */
enum class ColumnType {
    INTEGER, TEXT, REAL, BLOB
}

class TableBuilder(val table: Expression) {

    var columns = emptyArray<ColumnDefinition>()

    fun <T> column(column: Column<T>,
                   primaryKey: Boolean = false,
                   unique: Boolean = false,
                   autoincrement: Boolean = false): ColumnDefinition {
        val c = ColumnDefinition(column, column.type, primaryKey, unique, autoincrement, !column.isNullable)
        columns += c
        return c
    }
}

class ColumnDefinition(val name: Expression,
                       val type: ColumnType,
                       val primaryKey: Boolean,
                       val unique: Boolean,
                       val autoincrement: Boolean,
                       val notNull: Boolean) : Expression() {
    override fun render(): String {
        val pk = if (primaryKey) " PRIMARY KEY" else ""
        val nn = if (notNull) " NOT NULL" else ""
        val un = if (unique) " UNIQUE" else ""
        val ai = if (autoincrement) " AUTOINCREMENT" else ""

        return "${name.render()} $type$pk$un$ai$nn"
    }

}

