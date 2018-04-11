package com.mealsmadeeasy.model

data class MealPlanEntry(
        val date: Timestamp,
        val mealPeriod: MealPeriod,
        val meals: List<MealPortion>
)

data class FirebaseMealPlanEntry(
        val date: Timestamp,
        val mealPeriod: MealPeriod,
        val meals: List<FirebaseMealPortion>
) {

    @Suppress("unused")
    constructor(): this(0, MealPeriod.BREAKFAST, emptyList())

    fun toMealPlanEntry(): MealPlanEntry {
        return MealPlanEntry(date = date, mealPeriod = mealPeriod,
                meals = meals.mapNotNull { it.toMealPortion() })
    }

    operator fun minus(mealPortions: List<FirebaseMealPortion>): FirebaseMealPlanEntry {
        val newServings = mealPortions.map { it.mealId to it.servings }.toMap()
                .mapValues { (meal, servings) ->
                    servings.toLong() - mealPortions.filter { it.mealId == meal }.sumBy { it.servings }
                }

        return copy(meals = newServings.map { (mealId, servings) ->
            FirebaseMealPortion(mealId, servings.toInt()) }
        )
    }

}
