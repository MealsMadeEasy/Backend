package com.mealsmadeeasy.data

import com.mealsmadeeasy.auth.AuthManager
import com.mealsmadeeasy.endpoint.Response
import com.mealsmadeeasy.model.Meal

object MealStore {

    private val providers: List<MealProvider> = listOf(FirebaseMealProvider)

    fun findMealById(mealId: String): Meal? {
        return providers.asSequence()
                .mapNotNull { it.findMealById(mealId) }
                .firstOrNull()
    }

    fun getSuggestedMeals(userToken: String?): Response {
        return AuthManager.ensureValidUser(userToken) { userId ->
            getRandomMeals(count = 10)
        }
    }

    fun getRandomMeals(count: Int): Response {
        val meals = mutableListOf<Meal>()

        for (provider in providers) {
            meals += provider.getRandomMeals(count / providers.size)
        }

        return Response.ofJson(meals)
    }

    interface MealProvider {

        fun getRandomMeals(count: Int): List<Meal>

        fun findMealById(mealId: String): Meal?

    }

}