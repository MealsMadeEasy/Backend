package com.mealsmadeeasy.data

import com.mealsmadeeasy.FirebaseInstance
import com.mealsmadeeasy.auth.AuthManager
import com.mealsmadeeasy.auth.UserId
import com.mealsmadeeasy.endpoint.Response
import com.mealsmadeeasy.model.FirebaseMealPlan
import com.mealsmadeeasy.model.MealPlan
import com.mealsmadeeasy.model.toDate
import com.mealsmadeeasy.utils.block
import com.mealsmadeeasy.utils.firstBlocking
import com.mealsmadeeasy.utils.get
import com.mealsmadeeasy.utils.notDivisibleBy
import org.jetbrains.ktor.http.HttpStatusCode
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit

object MealPlanStore {

    private val db = FirebaseInstance.database

    fun getMealPlan(userToken: String?): Response {
        val yesterday = DateTime.now().minusDays(1).withTimeAtStartOfDay()
        return AuthManager.ensureValidUser(userToken) { userId ->
            Response.ofJson(getCurrentMealPlan(userId).let { plan ->
                plan.copy(meals = plan.meals.filter {
                    !it.date.toDate().isBefore(yesterday)
                })
            })
        }
    }

    fun updateMealPlan(userToken: String?, mealPlan: FirebaseMealPlan?): Response {
        if (mealPlan == null) {
            return Response.ofError(
                    "Missing meal plan parameter",
                    HttpStatusCode.BadRequest
            )
        }

        if (mealPlan.meals.any { it.date notDivisibleBy TimeUnit.DAYS.toMillis(1) }) {
            return Response.ofError(
                    "All meal plan entries must have a time of midnight in UTC",
                    HttpStatusCode.BadRequest
            )
        }

        val yesterday = DateTime.now().minusDays(1).withTimeAtStartOfDay()
        return AuthManager.ensureValidUser(userToken) { userId ->
            val current = getFirebaseMealPlan(userId)
            val updated = current.let { plan ->
                val oldMeals = plan.meals.filter {
                    it.date.toDate().isBefore(yesterday)
                }

                val newMeals = mealPlan.meals.filter {
                    !it.date.toDate().isBefore(yesterday)
                }

                plan.copy(meals = oldMeals + newMeals)
            }

            db["mealPlans/$userId"].setValue(updated).block()
            updateGroceryList(userId, current, updated)
            return@ensureValidUser Response.ofStatus("Ok")
        }
    }

    private fun updateGroceryList(userId: UserId, oldMealPlan: FirebaseMealPlan,
                                  newMealPlan: FirebaseMealPlan) {

        val today = DateTime.now().withTimeAtStartOfDay()
        val diff = newMealPlan - oldMealPlan

        diff.meals.filter { it.date.toDate() >= today }.forEach { entry ->
            for (meal in entry.meals) {
                when {
                    meal.servings > 0 -> {
                        GroceryListManager.addIngredients(
                                userId = userId,
                                serving = meal.toMealPortion()
                                        ?: throw RuntimeException("Failed to find meal" +
                                                " with id \"${meal.mealId}\""),
                                date = entry.date,
                                ingredients = MealStore.getIngredients(meal.mealId)
                                        ?: throw RuntimeException("Failed to find ingredients" +
                                                " for \"${meal.mealId}\"")
                        )
                    }
                    meal.servings < 0 -> {
                        GroceryListManager.removeMeal(
                                userId = userId,
                                serving = meal.toMealPortion()
                                        ?: throw RuntimeException("Failed to find meal" +
                                                " with id \"${meal.mealId}\""),
                                date = entry.date
                        )
                    }
                }
            }
        }
    }

    private fun getFirebaseMealPlan(userId: UserId): FirebaseMealPlan {
        return db["mealPlans/$userId"].firstBlocking<FirebaseMealPlan>()
                ?: FirebaseMealPlan()
    }

    private fun getCurrentMealPlan(userId: UserId): MealPlan {
        return getFirebaseMealPlan(userId).toMealPlan()
    }

}