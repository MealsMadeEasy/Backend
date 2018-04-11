package com.mealsmadeeasy.data

import com.mealsmadeeasy.auth.AuthManager
import com.mealsmadeeasy.data.edamam.EdamamMealProvider
import com.mealsmadeeasy.endpoint.Response
import com.mealsmadeeasy.model.FilterGroup
import com.mealsmadeeasy.model.Ingredient
import com.mealsmadeeasy.model.Meal
import com.mealsmadeeasy.model.Recipe
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

    fun getRecipe(id: String?): Response {
        if (id == null) {
            return Response.ofError(
                    message = "No ID was provided",
                    code = HttpStatusCode.BadRequest
            )
        }

        providers.asSequence()
                .mapNotNull { it.getRecipeForMeal(id) }
                .firstOrNull()
                ?.let { return Response.ofJson(it) }

        return Response.ofError(
                message = "No recipe was found for meal with ID \"$id\"",
                code = HttpStatusCode.NotFound
        )
    }

    fun getSearchResults(query: String?, filterArgs: String?): Response {
        if (query == null) {
            return Response.ofError("Missing query argument", HttpStatusCode.BadRequest)
        }
        val filters = filterArgs.orEmpty().split(",").map { it.trim() }.filter { it.isNotBlank() }

        return try {
            Response.ofJson(providers.flatMap { it.search(query, filters) })
        } catch (e: SearchQueryException) {
            Response.ofError(e.message!!, e.responseCode)
        }
    }

    fun getAvailableFilters(): Response {
        return Response.ofJson(
                providers.flatMap { it.getAvailableFilters() }
                        .also { groups ->
                            require(groups.distinctBy { it.groupId } == groups) {
                                "Multiple groups exist with the same ID"
                            }
                        }
        )
    }

    fun getIngredients(mealId: String): List<Ingredient>? {
        return providers.asSequence()
                .mapNotNull { it.getIngredients(mealId) }
                .firstOrNull()
    }

    interface MealProvider {

        fun getRandomMeals(count: Int): List<Meal>

        fun findMealById(mealId: String): Meal?

        fun getRecipeForMeal(mealId: String): Recipe?

        fun search(query: String, filters: List<String>): List<Meal>

        fun getAvailableFilters(): List<FilterGroup>

        fun getIngredients(mealId: String): List<Ingredient>?

    }

}