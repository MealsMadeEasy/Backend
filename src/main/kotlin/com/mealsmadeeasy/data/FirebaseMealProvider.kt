package com.mealsmadeeasy.data

import com.mealsmadeeasy.FirebaseInstance
import com.mealsmadeeasy.model.FilterGroup
import com.mealsmadeeasy.model.Meal
import com.mealsmadeeasy.model.Recipe
import com.mealsmadeeasy.utils.firstBlocking
import com.mealsmadeeasy.utils.firstBlockingList
import com.mealsmadeeasy.utils.get

object FirebaseMealProvider : MealStore.MealProvider {

    private val db = FirebaseInstance.database

    private val ILLEGAL_CHARS = listOf('.', '#', '$', '[', ']')

    override fun getRandomMeals(count: Int): List<Meal> {
        return db["meals"].firstBlockingList<Meal>().orEmpty().shuffled().take(count)
    }

    override fun findMealById(mealId: String): Meal? {
        if (mealId.any { it in ILLEGAL_CHARS }) {
            return null
        }

        return db["meals/$mealId"].firstBlocking<Meal>()
    }

    override fun getRecipeForMeal(mealId: String): Recipe? {
        if (mealId.any { it in ILLEGAL_CHARS }) {
            return null
        }

        return db["recipes$mealId"].firstBlocking<Recipe>()
    }

    override fun search(query: String, filters: List<String>): List<Meal> {
        if (filters.isNotEmpty()) {
            return emptyList()
        }

        val queryParts = query.split(" ").filter { it.isNotBlank() }.map { it.toLowerCase() }
        return db["meals"].firstBlockingList<Meal>().orEmpty().filter { it.matches(queryParts) }
    }

    override fun getAvailableFilters(): List<FilterGroup> = emptyList()

    private fun Meal.matches(words: List<String>): Boolean {
        return words.all { it in name.toLowerCase() }
    }

}