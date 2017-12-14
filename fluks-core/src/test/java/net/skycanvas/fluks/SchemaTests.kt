package net.skycanvas.fluks

import net.skycanvas.fluks.drivers.SQLiteDriver
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SchemaTests {
    @Rule
    @JvmField
    val tmp = TemporaryFolder()

    @Test
    fun testSchemaCreation() {
        try {
            /**
             * Create table with all supported datatypes (nullable/non-nullable variants)
             */

            val pkey = Column<Int>("id")
            val str = Column<String>("str")
            val strn = Column<String?>("strn")
            val intg = Column<Int>("intg")
            val intgn = Column<Int?>("intgn")
            val lng = Column<Long>("lng")
            val lngn = Column<Long?>("lngn")
            val dbl = Column<Double>("dbl")
            val dbln = Column<Double?>("dbln")
            val flt = Column<Float>("flt")
            val fltn = Column<Float?>("fltn")
            val bts = Column<ByteArray>("bts")
            val btsn = Column<ByteArray?>("btsn")

            val table = Table("TestTable")

            val db = Connection(SQLiteDriver(tmp.newFile("SchemaTestCreation.sqlite").path))

            db.exec(table.create {
                it.column(column = pkey, primaryKey = true, autoincrement = true)
                it.column(str)
                it.column(strn)
                it.column(intg)
                it.column(intgn)
                it.column(lng)
                it.column(lngn)
                it.column(dbl)
                it.column(dbln)
                it.column(flt)
                it.column(fltn)
                it.column(bts)
                it.column(btsn)
            })

            /**
             * Insert all non-nullable, keep nullable as null
             */

            val intValue = 2147483647
            val longValue = 9223372036854775807
            val doubleValue: Double = 3.141592653589793
            val floatValue: Float = 3.14159265F
            val bytesValue = ByteArray(1024 * 1024)
            Random(0).nextBytes(bytesValue)
            db.exec(
                    table.insert(
                            str to "str",
                            intg to intValue,
                            lng to longValue,
                            dbl to doubleValue,
                            flt to floatValue,
                            bts to bytesValue
                    )
            )

            /**
             * Validate data read from that row
             */

            var asserted = false
            db.query(table.select()).forEach {
                assertFalse("Only one row found", asserted)
                asserted = true
                assertEquals("str", it[str])
                assertEquals(intValue, it[intg])
                assertEquals(longValue, it[lng])
                assertEquals(doubleValue, it[dbl], 0.000001)
                assertEquals(floatValue, it[flt])
                assertArrayEquals(bytesValue, it[bts])
                assertNull(it[intgn])
                assertNull(it[lngn])
                assertNull(it[dbln])
                assertNull(it[fltn])
                assertNull(it[btsn])
            }

            assertTrue("Found at least one row", asserted)
            db.exec(table.delete())


            /**
             * Insert both nullable and non-nullable values
             */

            db.exec(
                    table.insert(
                            str to "str",
                            intg to intValue,
                            lng to longValue,
                            dbl to doubleValue,
                            flt to floatValue,
                            bts to bytesValue,
                            strn to "str",
                            intgn to intValue,
                            lngn to longValue,
                            dbln to doubleValue,
                            fltn to floatValue,
                            btsn to bytesValue

                    )
            )

            asserted = false
            db.query(table.select()).forEach {
                assertFalse("Only one row found", asserted)
                asserted = true
                assertEquals("str", it[strn]!!)
                assertEquals(intValue, it[intgn]!!)
                assertEquals(longValue, it[lngn]!!)
                assertEquals(doubleValue, it[dbln]!!, 0.000001)
                assertEquals(floatValue, it[fltn]!!)
                assertArrayEquals(bytesValue, it[btsn]!!)
            }

            assertTrue("Found at least one row", asserted)


        } catch (exc: Throwable) {
            fail(exc.message)
        }

    }
}