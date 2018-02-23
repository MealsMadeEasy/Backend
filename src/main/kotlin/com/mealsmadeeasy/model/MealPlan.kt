package com.mealsmadeeasy.model

data class MealPlan(
        val meals: List<MealPlanEntry>
)

data class FirebaseMealPlan(
        val meals: List<FirebaseMealPlanEntry>
) {

    @Suppress("unused")
    constructor(): this(emptyList())

    fun toMealPlan(): MealPlan {
        return MealPlan(meals = meals.map { it.toMealPlanEntry() }
                .filter { it.meals.isNotEmpty() })
    }

}
