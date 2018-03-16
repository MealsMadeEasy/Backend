package com.mealsmadeeasy.data.mercury.service

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://mercury.postlight.com"

fun createMercuryApi(): MercuryService = Retrofit.Builder()
        .apply {
            baseUrl(BASE_URL)
            addConverterFactory(MoshiConverterFactory.create(createMoshi()))
            client(createOkHttpClient())
        }
        .build()
        .create(MercuryService::class.java)

private fun createMoshi() = Moshi.Builder().build()

private fun createOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor {
            val request = it.request().newBuilder()
                    .addHeader("x-api-key", System.getenv("MERCURY_API_KEY"))
                    .build()

            it.proceed(request)
        }
        .build()
