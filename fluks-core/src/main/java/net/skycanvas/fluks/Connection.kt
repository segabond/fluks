package net.skycanvas.fluks

import java.io.Closeable


interface DatabaseDriver {
    fun execSQL(sql: String)
    fun rawQuery(sql: String): DatabaseCursor

}

abstract class DatabaseCursor: Closeable {
    abstract val count: Int
    abstract fun moveToPosition(position: Int)
    abstract fun moveToFirst(): Boolean
    abstract fun getColumnName(columnIndex: Int): String
    abstract fun getAny(columnIndex: Int): Any?
    abstract val columnCount: Int

    fun getData(): HashMap<String, Any?> {
        val ret = HashMap<String, Any?>(columnCount)
        for (i in 0 until columnCount) {
            ret[getColumnName(i)] = getAny(i)
        }
        return ret
    }

    inline fun <reified T> getTyped(columnIndex: Int): T {
        return getTypedFromAny(getAny(columnIndex))
    }

}


class Connection(val db: DatabaseDriver) {

    fun exec(statement: Statement) {
        try {
            db.execSQL(statement.asSQL())
        } catch (exc: Throwable) {
            throw QueryErrorException(exc)
        }
    }

    fun query(statement: Statement): Result {
        try {
            db.rawQuery(statement.asSQL()).use {
                return Result(it)
            }
        } catch (exc: Throwable) {
            throw QueryErrorException(exc)
        }
    }

    inline fun <reified T> scalar(statement: Statement): T {
        return try {
            db.rawQuery(statement.asSQL()).use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getTyped(0)
                } else {
                    if (isMarkedNullable<T>()) {
                        return null as T
                    } else {
                        throw NullPointerException()
                    }

                }
            }
        } catch (exc: Throwable) {
            throw QueryErrorException(exc)
        }
    }



    fun lastInsertId(): Int = scalar(SelectStatement(SQLiteFunction("last_insert_rowid")))
}