package net.skycanvas.fluks


inline fun <reified T> isMarkedNullable(): Boolean {
    return try {
        null as T
        true
    } catch (e: TypeCastException) {
        false
    }
}



inline fun <reified T> getTypedFromAny(value: Any?): T {
    if (value == null && isMarkedNullable<T>()) {
        return null as T
    }

    val ret = when (T::class) {
        Boolean::class ->
            when (value) {
                is Int -> (value != 0) as T
                is Long -> (value != 0L) as T
                else -> throw UnsupportedTypeException("Cannot get boolean value from type ${value?.let{it::class.java}}")
            }
        Integer::class -> {
            when (value) {
                is Int -> value as T
                is Long -> value.toInt() as T
                else -> throw UnsupportedTypeException("Cannot get boolean value from type ${value?.let{it::class.java}}")
            }
        }
        else -> value as T
    }
    return ret
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
            else -> throw UnsupportedTypeException("Cannot get boolean value from type ${value?.let{it::class.java}}")
        }
    } else if (clazz.isAssignableFrom(Integer::class.java)) {
        when (value) {
            is Int -> value as T
            is Long -> value.toInt() as T
            else -> throw UnsupportedTypeException("Cannot get boolean value from type ${value?.let{it::class.java}}")
        }

    } else {
        value as T
    }

    return ret
}