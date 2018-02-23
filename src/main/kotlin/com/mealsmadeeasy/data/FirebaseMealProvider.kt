package com.mealsmadeeasy.data

import com.mealsmadeeasy.FirebaseInstance
import com.mealsmadeeasy.model.Meal
import com.mealsmadeeasy.utils.firstBlocking
import com.mealsmadeeasy.utils.firstBlockingList
import com.mealsmadeeasy.utils.get

object FirebaseMealProvider : MealStore.MealProvider {

    private val db = FirebaseInstance.database

    override fun getRandomMeals(count: Int): List<Meal> {
        return db["meals"].firstBlockingList<Meal>().orEmpty()
    }

    override fun findMealById(mealId: String): Meal? {
        return db["meals/$mealId"].firstBlocking<Meal>()
    }

}