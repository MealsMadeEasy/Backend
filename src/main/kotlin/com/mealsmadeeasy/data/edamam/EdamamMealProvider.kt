package com.mealsmadeeasy.data.edamam

import com.mealsmadeeasy.FirebaseInstance
import com.mealsmadeeasy.data.MealStore
import com.mealsmadeeasy.data.edamam.model.EdamamRecipe
import com.mealsmadeeasy.data.edamam.service.createEdamamApi
import com.mealsmadeeasy.data.mercury.MercuryParser
import com.mealsmadeeasy.model.Meal
import com.mealsmadeeasy.model.Recipe
import com.mealsmadeeasy.utils.firstBlocking
import com.mealsmadeeasy.utils.get

object EdamamMealProvider : MealStore.MealProvider {

    private const val ID_PREFIX = "edamam/"
    private val service = createEdamamApi()

    private val enableApiRequests: Boolean
        get() = FirebaseInstance.database["enableEdamam"].firstBlocking() ?: false

    override fun getRandomMeals(count: Int): List<Meal> {
        if (!enableApiRequests) {
            return emptyList()
        }

        return service.search(query = "Tacos", firstIdx = 0, lastIdx = count)
                .execute()
                .body()
                ?.hits
                .orEmpty()
                .map { it.recipe }
                .map { it.toMeal() }
    }

    override fun findMealById(mealId: String): Meal? {
        if (!mealId.startsWith(ID_PREFIX) || !enableApiRequests) {
            return null
        }

        return service.findById(id = mealId.substringAfter(ID_PREFIX))
                .execute()
                .body()
                ?.firstOrNull()
                ?.toMeal()
    }

    override fun getRecipeForMeal(mealId: String): Recipe? {
        if (!mealId.startsWith(ID_PREFIX) || !enableApiRequests) {
            return null
        }

        return service.findById(id = mealId.substringAfter(ID_PREFIX))
                .execute()
                .body()
                ?.firstOrNull()
                ?.url
                ?.let {
                    Recipe(MercuryParser.parseWebsite(it).content)
                }
    }

    private fun EdamamRecipe.toMeal(): Meal {
        return Meal(
                id = "$ID_PREFIX$uri",
                name = label,
                description = healthLabels.map { it.webLabel }.joinToString(),
                thumbnailUrl = image
        )
    }

}