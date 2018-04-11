package com.mealsmadeeasy.data

import com.mealsmadeeasy.FirebaseInstance
import com.mealsmadeeasy.model.FilterGroup
import com.mealsmadeeasy.model.Ingredient
import com.mealsmadeeasy.model.Meal
import com.mealsmadeeasy.model.Recipe
import com.mealsmadeeasy.utils.firstBlocking
import com.mealsmadeeasy.utils.firstBlockingList
import com.mealsmadeeasy.utils.get

object FirebaseMealProvider : MealStore.MealProvider {

    private val db = FirebaseInstance.database

    private val ILLEGAL_CHARS = listOf('.', '#', '$', '[', ']')

    private val enableApiRequests: Boolean
        get() = FirebaseInstance.database["enableFirebaseMeals"].firstBlocking() ?: false

    override fun getRandomMeals(count: Int): List<Meal> {
        if (!enableApiRequests) {
            return emptyList()
        }

        return db["meals"].firstBlockingList<Meal>().orEmpty().shuffled().take(count)
    }

    override fun findMealById(mealId: String): Meal? {
        if (!enableApiRequests || mealId.any { it in ILLEGAL_CHARS }) {
            return null
        }

        return db["meals/$mealId"].firstBlocking<Meal>()
    }

    override fun getRecipeForMeal(mealId: String): Recipe? {
        if (!enableApiRequests || mealId.any { it in ILLEGAL_CHARS }) {
            return null
        }

        return db["recipes$mealId"].firstBlocking<Recipe>()
    }

    override fun search(query: String, filters: List<String>): List<Meal> {
        if (!enableApiRequests || filters.isNotEmpty()) {
            return emptyList()
        }

        val queryParts = query.split(" ").filter { it.isNotBlank() }.map { it.toLowerCase() }
        return db["meals"].firstBlockingList<Meal>().orEmpty().filter { it.matches(queryParts) }
    }

    override fun getAvailableFilters(): List<FilterGroup> = emptyList()

    override fun getIngredients(mealId: String): List<Ingredient>? {
        if (!enableApiRequests) {
            return null
        }

        return when {
            mealId.any { it in ILLEGAL_CHARS } -> return null
            db["meals/$mealId"].firstBlocking<Meal>() != null -> emptyList()
            else -> null
        }
    }

    private fun Meal.matches(words: List<String>): Boolean {
        return words.all { it in name.toLowerCase() }
    }

}