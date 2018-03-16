package com.mealsmadeeasy.data.mercury.service

import com.mealsmadeeasy.data.mercury.ParseResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MercuryService {

    @GET("parser")
    fun parse(@Query("url") url: String): Call<ParseResult>

}