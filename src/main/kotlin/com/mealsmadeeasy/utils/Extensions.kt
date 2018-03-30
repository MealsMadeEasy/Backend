package com.mealsmadeeasy.utils

infix fun Long.divisibleBy(other: Long) = this % other == 0L

infix fun Long.notDivisibleBy(other: Long) = !divisibleBy(other)
