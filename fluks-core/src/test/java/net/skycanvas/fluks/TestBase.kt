package net.skycanvas.fluks

import net.skycanvas.fluks.drivers.SQLiteDriver
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.util.*

open class TestBase {
    val strValue = "String Value"
    val intValue = 2147483647
    val longValue = 9223372036854775807
    val doubleValue: Double = 3.141592653589793
    val floatValue: Float = 3.14159265F
    val bytesValue = ByteArray(1024 * 1024)

    lateinit var db: Connection

    @Rule
    @JvmField
    val tmp = TemporaryFolder()

    @Before
    fun setup() {
        db = Connection(SQLiteDriver(tmp.newFile(this.javaClass.simpleName + ".sqlite").path))
    }

    fun createTable() {
        db.exec(table.create {
            it.column(column = c_pkey, primaryKey = true, autoincrement = true)
            it.column(c_str)
            it.column(c_strn)
            it.column(c_intg)
            it.column(c_intgn)
            it.column(c_bool)
            it.column(c_booln)
            it.column(c_lng)
            it.column(c_lngn)
            it.column(c_dbl)
            it.column(c_dbln)
            it.column(c_flt)
            it.column(c_fltn)
            it.column(c_bts)
            it.column(c_btsn)
        })
    }

    fun insertAllValues() {
        db.exec(
                table.insert(
                        c_str to strValue,
                        c_intg to intValue,
                        c_bool to true,
                        c_lng to longValue,
                        c_dbl to doubleValue,
                        c_flt to floatValue,
                        c_bts to bytesValue,
                        c_strn to strValue,
                        c_intgn to intValue,
                        c_booln to true,
                        c_lngn to longValue,
                        c_dbln to doubleValue,
                        c_fltn to floatValue,
                        c_btsn to bytesValue
                )
        )

    }

    fun insertRequiredValues() {
        db.exec(
                table.insert(
                        c_str to strValue,
                        c_intg to intValue,
                        c_lng to longValue,
                        c_dbl to doubleValue,
                        c_flt to floatValue,
                        c_bool to true,
                        c_bts to bytesValue
                )
        )

    }

    // columns
    companion object {
        val c_pkey = Column<Int>("id")
        val c_str = Column<String>("str")
        val c_strn = Column<String?>("strn")
        val c_intg = Column<Int>("intg")
        val c_bool = Column<Boolean>("bool")
        val c_booln = Column<Boolean?>("booln")
        val c_intgn = Column<Int?>("intgn")
        val c_lng = Column<Long>("lng")
        val c_lngn = Column<Long?>("lngn")
        val c_dbl = Column<Double>("dbl")
        val c_dbln = Column<Double?>("dbln")
        val c_flt = Column<Float>("flt")
        val c_fltn = Column<Float?>("fltn")
        val c_bts = Column<ByteArray>("bts")
        val c_btsn = Column<ByteArray?>("btsn")

        val table = Table("TestTable")
    }


    init {
        Random(0).nextBytes(bytesValue)
    }

}