package com.mealsmadeeasy.model

import com.mealsmadeeasy.data.MealStore

data class MealPortion(
        val meal: Meal,
        val servings: Int
)

data class FirebaseMealPortion(
        val mealId: String,
        val servings: Int
) {

    @Suppress("unused")
    constructor(): this("", 0)

    fun toMealPortion(): MealPortion? {
        return MealStore.findMealById(mealId)?.let {
            MealPortion(meal = it, servings = servings)
        }
    }

}
