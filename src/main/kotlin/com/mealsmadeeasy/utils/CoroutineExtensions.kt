package com.mealsmadeeasy.utils

import kotlinx.coroutines.experimental.Deferred

fun <T> Deferred<T>.block(): T {
    start()

    while (!isCompleted) {
        Thread.yield()
    }

    getCompletionExceptionOrNull()?.let { throw it }
    return getCompleted()
}
