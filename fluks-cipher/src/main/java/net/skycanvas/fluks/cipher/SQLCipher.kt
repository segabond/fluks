package net.skycanvas.fluks.drivers

import net.skycanvas.fluks.ConnectivityException
import net.skycanvas.fluks.DatabaseCursor
import net.skycanvas.fluks.DatabaseDriver
import net.skycanvas.fluks.UnsupportedTypeException
import net.sqlcipher.Cursor
import net.sqlcipher.database.SQLiteDatabase

class SQLCipherDriver(path: String, password: () -> String) : DatabaseDriver {
    val db: SQLiteDatabase

    init {
        try {
            db = SQLiteDatabase.openOrCreateDatabase(path, password(), null, null)
        } catch (ex: Throwable) {
            throw ConnectivityException(ex)
        }

    }

    override fun execSQL(sql: String) {
        db.execSQL(sql)
    }

    override fun rawQuery(sql: String): DatabaseCursor {
        return SQLiteCursorWrapper(db.rawQuery(sql, emptyArray()))
    }

    class SQLiteCursorWrapper(val cursor: Cursor) : DatabaseCursor() {
        override fun getColumnName(columnIndex: Int): String = cursor.getColumnName(columnIndex)

        override fun getAny(columnIndex: Int): Any? {
            return when (cursor.getType(columnIndex)) {
                Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(columnIndex)
                Cursor.FIELD_TYPE_STRING -> cursor.getString(columnIndex)
                Cursor.FIELD_TYPE_FLOAT -> cursor.getFloat(columnIndex)
                Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(columnIndex)
                Cursor.FIELD_TYPE_NULL -> null
                else -> throw UnsupportedTypeException()
            }
        }

        override fun moveToFirst(): Boolean {
            return cursor.moveToFirst()
        }

        override fun close() {
            cursor.close()
        }

        override val count: Int
            get() = cursor.count

        override val columnCount: Int
            get() = cursor.columnCount

        override fun moveToPosition(position: Int) {
            cursor.moveToPosition(position)
        }
    }
}
