package com.mealsmadeeasy.data.edamam.service

import com.mealsmadeeasy.data.edamam.model.DietLabels
import com.mealsmadeeasy.data.edamam.model.HealthLabels
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val BASE_URL = "https://api.edamam.com"

fun createEdamamApi(): EdamamService= Retrofit.Builder()
        .apply {
            baseUrl(BASE_URL)
            addConverterFactory(MoshiConverterFactory.create(createMoshi()))
        }
        .build()
        .create(EdamamService::class.java)

private fun createMoshi() = Moshi.Builder()
        .add(DietLabels::class.java, object : JsonAdapter<DietLabels>() {
            override fun fromJson(reader: JsonReader): DietLabels? {
                return reader.nextString()?.let { name ->
                    DietLabels.values().firstOrNull {
                        it.apiParameter == name || it.webLabel == name
                    } ?: throw NoSuchElementException("No diet label exists for $name")
                }
            }

            override fun toJson(writer: JsonWriter, value: DietLabels?) {
                if (value != null) {
                    writer.value(value.apiParameter)
                } else {
                    writer.nullValue()
                }
            }
        })
        .add(HealthLabels::class.java, object : JsonAdapter<HealthLabels>() {
            override fun fromJson(reader: JsonReader): HealthLabels? {
                return reader.nextString()?.let { name ->
                    HealthLabels.values().firstOrNull {
                        it.apiParameter == name || it.webLabel == name
                    } ?: throw NoSuchElementException("No health label exists for $name")
                }
            }

            override fun toJson(writer: JsonWriter, value: HealthLabels?) {
                if (value != null) {
                    writer.value(value.apiParameter)
                } else {
                    writer.nullValue()
                }
            }
        })
        .build()
