package net.skycanvas.fluks

import kotlin.experimental.and


inline fun <reified T> isMarkedNullable(): Boolean {
    return try {
        null as T
        true
    } catch (e: TypeCastException) {
        false
    }
}


inline fun <reified T> getTypedFromAny(value: Any?): T {
    return getTypedFromAny(value, T::class.java) as T
}


@Suppress("UNCHECKED_CAST")
fun <T> getTypedFromAny(value: Any?, clazz: Class<T>): T? {
    if (value == null) {
        return null
    }

    val ret = if (clazz.isAssignableFrom(Boolean::class.java)) {
        when (value) {
            is Int -> (value != 0) as T
            is Long -> (value != 0L) as T
            else -> throw UnsupportedTypeException("Cannot get boolean value from type ${value?.let { it::class.java }}")
        }
    } else if (clazz.isAssignableFrom(Integer::class.java)) {
        when (value) {
            is Int -> value as T
            is Long -> value.toInt() as T
            else -> throw UnsupportedTypeException("Cannot get boolean value from type ${value?.let { it::class.java }}")
        }

    } else {
        value as T
    }

    return ret
}

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

internal fun ByteArray.toHex() : String{
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}


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
