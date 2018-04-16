package com.mealsmadeeasy.utils

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

class LruCache<Request, Response>(
        private val maxEntries: Int,
        private val load: suspend Request.() -> Response
) {

    private val lock = Any()

    private var currentTimestamp: Long = 0
    private val cache = mutableMapOf<Request, CacheEntry<Response>>()

    fun getValue(request: Request): Deferred<Response> {
        synchronized(lock) {
            cache[request]?.let {
                // Response is in cache. Return it and update its timestamp.
                it.timestamp = currentTimestamp++
                return it.value
            }

            evictOldestIfNecessary()

            val response = async {
                try {
                    load(request)
                } catch (t: Throwable) {
                    cache.remove(request)
                    throw t
                }
            }
            cache[request] = CacheEntry(response, currentTimestamp++)
            return response
        }
    }

    private fun evictOldestIfNecessary() {
        if (cache.size > maxEntries) {
            cache.entries.minBy { it.value.timestamp }?.let {
                cache.remove(it.key)
            }
        }
    }

    private data class CacheEntry<out Response>(
            val value: Deferred<Response>,
            var timestamp: Long
    )

}
