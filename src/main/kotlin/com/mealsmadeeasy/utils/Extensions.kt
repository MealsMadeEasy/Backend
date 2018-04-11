package com.mealsmadeeasy.utils

infix fun Long.divisibleBy(other: Long) = this % other == 0L

infix fun Long.notDivisibleBy(other: Long) = !divisibleBy(other)

fun <T> MutableList<T>.replace(value: T, replacement: T)
        = replaceAll { if (it == value) replacement else value }

fun <T> List<T>.replace(old: T, new: T) = map { if (it == old) new else it }
