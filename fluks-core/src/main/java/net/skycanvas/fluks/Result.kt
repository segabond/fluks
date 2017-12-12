package net.skycanvas.fluks


class Result(val cursor: DatabaseCursor) : Iterable<Row> {
    override fun iterator(): Iterator<Row> = rows.iterator()

    val rows: Array<Row>

    init {
        rows = Array(cursor.count) {
            cursor.moveToPosition(it)
            return@Array Row(cursor.getData())
        }
    }
}


class Row(val data: HashMap<String, Any?>) {
    inline operator fun <reified V> get(column: Column<V>): V {
        val value = data[column.name]
        return if (value == null) {
            if (isMarkedNullable<V>()) {
                null as V
            } else {
                throw NullPointerException()
            }
        } else {
            column.convert(value)
        }

    }
}
