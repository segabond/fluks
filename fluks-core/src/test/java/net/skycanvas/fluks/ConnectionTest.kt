package net.skycanvas.fluks

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ConnectionTest : TestBase() {

    @Test
    fun testScalars() {
        try {
            createTable()
            insertRequiredValues()

            assertEquals(strValue, db.scalar<String>(table.select(c_str)))
            assertEquals(intValue, db.scalar<Int>(table.select(c_intg)))
            assertEquals(longValue, db.scalar<Long>(table.select(c_lng)))
            assertArrayEquals(bytesValue, db.scalar<ByteArray>(table.select(c_bts)))
            assertEquals(true, db.scalar<Boolean>(table.select(c_bool)))
            assertEquals(floatValue, db.scalar<Float>(table.select(c_flt)))
            assertEquals(doubleValue, db.scalar<Double>(table.select(c_dbl)), 0.00001)


        } catch (exc: Throwable) {
            fail(exc.message)
        }


    }

}