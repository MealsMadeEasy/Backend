package com.mealsmadeeasy.data

import com.mealsmadeeasy.FirebaseInstance
import com.mealsmadeeasy.auth.AuthManager
import com.mealsmadeeasy.auth.UserId
import com.mealsmadeeasy.endpoint.Response
import com.mealsmadeeasy.model.FirebaseMealPlan
import com.mealsmadeeasy.model.MealPlan
import com.mealsmadeeasy.model.toDate
import com.mealsmadeeasy.utils.firstBlocking
import com.mealsmadeeasy.utils.get
import org.jetbrains.ktor.http.HttpStatusCode
import org.joda.time.DateTime

object MealPlanStore {

    private val db = FirebaseInstance.database

    fun getMealPlan(userToken: String?): Response {
        val today = DateTime.now().withTimeAtStartOfDay()
        return AuthManager.ensureValidUser(userToken) { userId ->
            Response.ofJson(getCurrentMealPlan(userId).let { plan ->
                plan.copy(meals = plan.meals.filter {
                    !it.date.toDate().isBefore(today)
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

        val today = DateTime.now().withTimeAtStartOfDay()
        return AuthManager.ensureValidUser(userToken) { userId ->
            val updated = getFirebaseMealPlan(userId).let { plan ->
                val oldMeals = plan.meals.filter {
                    it.date.toDate().isBefore(today)
                }

                val newMeals = mealPlan.meals.filter {
                    !it.date.toDate().isBefore(today)
                }

                plan.copy(meals = oldMeals + newMeals)
            }

            db["mealPlans/$userId"].setValue(updated)
            return@ensureValidUser Response.ofStatus("Ok")
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