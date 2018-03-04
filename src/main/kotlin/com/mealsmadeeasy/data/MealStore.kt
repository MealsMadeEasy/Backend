package com.mealsmadeeasy.data

import com.mealsmadeeasy.auth.AuthManager
import com.mealsmadeeasy.data.edamam.EdamamMealProvider
import com.mealsmadeeasy.endpoint.Response
import com.mealsmadeeasy.model.Meal
import org.jetbrains.ktor.http.HttpStatusCode

object MealStore {

    private val providers = listOf(FirebaseMealProvider, EdamamMealProvider)

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

    fun getMeal(id: String?): Response {
        if (id == null) {
            return Response.ofError(
                    message = "No ID was provided",
                    code = HttpStatusCode.BadRequest)
        }

        findMealById(id)?.let {
            return Response.ofJson(it)
        }

        return Response.ofError(
                message = "No meal with ID \"$id\" was found",
                code = HttpStatusCode.NotFound
        )
    }

    interface MealProvider {

        fun getRandomMeals(count: Int): List<Meal>

        fun findMealById(mealId: String): Meal?

    }

}