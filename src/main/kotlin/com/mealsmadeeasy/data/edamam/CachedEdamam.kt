package com.mealsmadeeasy.data.edamam

import com.mealsmadeeasy.data.edamam.model.EdamamRecipe
import com.mealsmadeeasy.data.edamam.model.EdamamSearchResults
import com.mealsmadeeasy.data.edamam.service.EdamamService
import com.mealsmadeeasy.utils.LruCache
import retrofit2.Response
import java.io.IOException

class CachedEdamam(private val service: EdamamService) {

    private val searchCache = LruCache<SearchQuery, EdamamSearchResults>(100) {
        service.search(query)
                .execute()
                .unwrap()
    }

    private val dietSearchCache = LruCache<DietSearchQuery, EdamamSearchResults>(100) {
        service.searchDietFilter(query = query, dietLabel = dietLabel)
                .execute()
                .unwrap()
    }

    private val healthSearchCache = LruCache<HealthSearchQuery, EdamamSearchResults>(100) {
        service.searchHealthFilter(query = query, healthLabel = healthLabel)
                .execute()
                .unwrap()
    }

    private val dietHealthSearchCache = LruCache<DietHealthSearchQuery, EdamamSearchResults>(100) {
        service.search(query = query, dietLabel = dietLabel, healthLabel = healthLabel)
                .execute()
                .unwrap()
    }

    private val mealLookupCache = LruCache<MealLookupQuery, List<EdamamRecipe>>(100) {
        service.findById(id = id)
                .execute()
                .unwrap()
    }

    fun search(query: String)
            = searchCache.getValue(SearchQuery(query))

    fun searchDietFilter(query: String, dietLabel: String)
            = dietSearchCache.getValue(DietSearchQuery(query, dietLabel))

    fun searchHealthFilter(query: String, healthLabel: String)
            = healthSearchCache.getValue(HealthSearchQuery(query, healthLabel))

    fun search(query: String, healthLabel: String, dietLabel: String)
            = dietHealthSearchCache.getValue(DietHealthSearchQuery(query, dietLabel, healthLabel))

    fun findById(id: String)
            = mealLookupCache.getValue(MealLookupQuery(id))

    private data class SearchQuery(val query: String)
    private data class DietSearchQuery(val query: String, val dietLabel: String)
    private data class HealthSearchQuery(val query: String, val healthLabel: String)
    private data class DietHealthSearchQuery(val query: String, val dietLabel: String, val healthLabel: String)
    private data class MealLookupQuery(val id: String)

    private fun <T> Response<T>.unwrap(): T {
        if (!isSuccessful) {
            throw IOException("${code()}: ${errorBody()}")
        } else {
            return body() ?: throw IOException("No data was returned")
        }
    }

}
