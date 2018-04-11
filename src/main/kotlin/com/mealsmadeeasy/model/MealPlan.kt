package com.mealsmadeeasy.model

import com.mealsmadeeasy.utils.replace

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

    operator fun minus(mealPlan: FirebaseMealPlan): FirebaseMealPlan {
        return mealPlan.meals.fold(this, FirebaseMealPlan::minus)
    }

    operator fun minus(mealPlanEntry: FirebaseMealPlanEntry): FirebaseMealPlan {
        val entry = meals.filter { it.date.toDate().dayOfMonth() == mealPlanEntry.date.toDate().dayOfMonth() }
                .firstOrNull { it.mealPeriod == mealPlanEntry.mealPeriod }

        return if (entry != null) {
            copy(meals = meals.replace(old = entry, new = entry - mealPlanEntry.meals))
        } else {
            copy(meals = meals + mealPlanEntry.copy(meals = mealPlanEntry.meals.map { it.copy(servings = -it.servings) }))
        }
    }

}
