package net.skycanvas.fluks

interface Statement {
    fun asSQL(): String
}

class SelectStatement(vararg val expression: Expression): Statement {
    var table: Expression? = null
    var predicate: Predicate? = null

    override fun asSQL(): String {
        val fields = expression.joinToString { it.render() }
        val where = predicate?.let { " WHERE ${it.render()} " } ?: ""
        val from = table?.let { " FROM ${it.render()} " } ?: ""

        return "SELECT $fields $from $where "
    }

    fun from(table: Expression): SelectStatement {
        this.table = table
        return this
    }

    fun where(predicate: Predicate): SelectStatement {
        this.predicate = predicate
        return this
    }
}

class CreateStatement(val builder: TableBuilder): Statement {
    override fun asSQL(): String {
        return "CREATE TABLE ${builder.table.render()} (${builder.columns.joinToString { it.render() }})"
    }

}

class InsertStatement(vararg val setters: Setter): Statement {
    lateinit var table: Expression

    override fun asSQL(): String {
        val fields = setters.joinToString { it.field.render() }
        val values = setters.joinToString { it.value.render() }
        return "INSERT INTO ${table.render()}($fields) VALUES ($values)"
    }

    fun into(table: Expression): InsertStatement {
        this.table = table
        return this
    }

}

class UpdateStatement(vararg val setters: Setter): Statement {
    lateinit var table: Expression
    var predicate: Predicate? = null

    override fun asSQL(): String {
        val updates = setters.joinToString { it.field.render() + "=" + it.value.render() }
        val where = predicate?.let { "WHERE " + it.render() } ?: ""
        return "UPDATE ${table.render()} SET $updates $where"
    }

    fun table(table: Expression): UpdateStatement {
        this.table = table
        return this
    }

    fun where(predicate: Predicate): UpdateStatement {
        this.predicate = predicate
        return this
    }
}


class DeleteStatement: Statement {
    lateinit var table: Expression
    var predicate: Predicate? = null

    override fun asSQL(): String {
        val where = predicate?.let { "WHERE " + it.render() } ?: ""
        return "DELETE FROM ${table.render()} $where"
    }

    fun from(table: Expression): DeleteStatement {
        this.table = table
        return this
    }

    fun where(predicate: Predicate): DeleteStatement {
        this.predicate = predicate
        return this
    }

}
