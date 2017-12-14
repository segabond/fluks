package net.skycanvas.fluks

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SchemaTests: TestBase() {
    @Test
    fun testSchemaCreation() {
        try {
            /**
             * Create table with all supported datatypes (nullable/non-nullable variants)
             */
            createTable()

            /**
             * Insert all non-nullable, keep nullable as null
             */
            insertRequiredValues()

            /**
             * Validate data read from that row
             */

            var asserted = false
            db.query(table.select()).forEach {
                assertFalse("Only one row found", asserted)
                asserted = true
                assertEquals(strValue, it[c_str])
                assertEquals(intValue, it[c_intg])
                assertEquals(longValue, it[c_lng])
                assertEquals(doubleValue, it[c_dbl], 0.000001)
                assertEquals(floatValue, it[c_flt])
                assertTrue(it[c_bool])
                assertArrayEquals(bytesValue, it[c_bts])
                assertNull(it[c_intgn])
                assertNull(it[c_lngn])
                assertNull(it[c_dbln])
                assertNull(it[c_fltn])
                assertNull(it[c_btsn])
                assertNull(it[c_booln])
            }

            assertTrue("Found at least one row", asserted)
            db.exec(table.delete())


            /**
             * Insert both nullable and non-nullable values
             */
            insertAllValues()
            asserted = false
            db.query(table.select()).forEach {
                assertFalse("Only one row found", asserted)
                asserted = true
                assertEquals(strValue, it[c_strn]!!)
                assertEquals(intValue, it[c_intgn]!!)
                assertTrue(it[c_booln]!!)
                assertEquals(longValue, it[c_lngn]!!)
                assertEquals(doubleValue, it[c_dbln]!!, 0.000001)
                assertEquals(floatValue, it[c_fltn]!!)
                assertArrayEquals(bytesValue, it[c_btsn]!!)
            }

            assertTrue("Found at least one row", asserted)


        } catch (exc: Throwable) {
            fail(exc.message)
        }

    }
}