package com.mealsmadeeasy.data.edamam.service

import com.mealsmadeeasy.data.edamam.model.EdamamRecipe
import com.mealsmadeeasy.data.edamam.model.EdamamSearchResults
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

private const val DEFAULT_RESULTS_SIZE = 30
fun appId() = System.getenv("EDAMAM_APP_ID")
fun appKey() = System.getenv("EDAMAM_APP_KEY")

interface EdamamService {

    @GET("search")
    fun search(
            @Query("q") query: String,
            @Query("app_id") appId: String = appId(),
            @Query("app_key") appKey: String = appKey(),
            @Query("from") firstIdx: Int = 0,
            @Query("to") lastIdx: Int = firstIdx + DEFAULT_RESULTS_SIZE
    ): Call<EdamamSearchResults>

    @GET("search")
    fun findById(
            @Query("r") id: String,
            @Query("app_id") appId: String = appId(),
            @Query("app_key") appKey: String = appKey()
    ): Call<List<EdamamRecipe>>

}