package com.mealsmadeeasy.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

inline fun <reified T> String?.parseJson(): T? {
    return Json.converterOf<T>().fromJson(this.orEmpty())
}

object Json {

    val moshi: Moshi = Moshi.Builder().build()

    val converters = mutableMapOf<Class<*>, JsonAdapter<*>>()

    inline fun <reified T> converterOf(): JsonAdapter<T> {
        if (T::class.java !in converters.keys) {
            val converter = moshi.adapter(T::class.java)
            converters += T::class.java to converter
            return converter
        } else {
            @Suppress("UNCHECKED_CAST")
            return converters[T::class.java] as JsonAdapter<T>
        }
    }

    inline fun <reified T> convertToJson(value: T?): String {
        return converterOf<T>().toJson(value)
    }

}
