package com.mealsmadeeasy.data.mercury

import com.mealsmadeeasy.data.mercury.service.createMercuryApi
import java.io.IOException

object MercuryParser {

    private val service = createMercuryApi()

    fun parseWebsite(url: String): ParseResult {
        val result = service.parse(url).execute()

        return result.takeIf { it.isSuccessful }?.body()
                ?: throw IOException("Error ${result.code()} while parsing url")
    }

}