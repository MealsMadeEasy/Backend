package com.mealsmadeeasy.data

import com.mealsmadeeasy.FirebaseInstance
import com.mealsmadeeasy.auth.AuthManager
import com.mealsmadeeasy.endpoint.Response
import com.mealsmadeeasy.model.*
import com.mealsmadeeasy.utils.block
import com.mealsmadeeasy.utils.firstBlockingList
import com.mealsmadeeasy.utils.get
import org.jetbrains.ktor.http.HttpStatusCode
import org.joda.time.DateTime

object GroceryListManager {

    private val db = FirebaseInstance.database

    fun addIngredients(userId: String, serving: MealPortion, date: Timestamp,
                       ingredients: List<Ingredient>) {
        val groceryList = db["groceries/$userId"]
                .firstBlockingList<GroceryListItem>()
                .orEmpty()
                .toMutableList()

        val dependant = MealPlanItem(serving.meal.name, serving.meal.id, serving.servings, date)

        for (ingredient in ingredients) {
            if (groceryList.any { it.offer(ingredient) }) {
                val mergeIndex = groceryList.indexOfFirst { it.offer(ingredient) }
                groceryList[mergeIndex] = groceryList[mergeIndex].add(ingredient, dependant)
            } else {
                groceryList.add(GroceryListItem(
                        name = ingredient.name,
                        purchasedQuantity = 0f,
                        requiredQuantity = ingredient.quantity,
                        unit = ingredient.unit,
                        dependants = listOf(GroceryListDependant(
                                meal = dependant,
                                contribution = serving.servings * ingredient.quantity
                        ))
                ))
            }
        }

        db["groceries/$userId"].setValue(groceryList).block()
    }

    fun removeMeal(userId: String, serving: MealPortion, date: Timestamp) {
        val dependant = MealPlanItem(serving.meal.name, serving.meal.id, serving.servings, date)

        val groceryList = db["groceries/$userId"]
                .firstBlockingList<GroceryListItem>()
                .orEmpty()
                .map { if (it.contains(dependant)) it.remove(dependant) else it }

        db["groceries/$userId"].setValue(groceryList).block()
    }

    fun getGroceryList(userToken: String?): Response {
        return AuthManager.ensureValidUser(userToken) { userId ->
            val groceryList = db["groceries/$userId"]
                    .firstBlockingList<GroceryListItem>()
                    .orEmpty()

            val culledGroceryList = groceryList.cull()

            if (groceryList != culledGroceryList) {
                db["groceries/$userId"].setValue(culledGroceryList).block()
            }

            return@ensureValidUser Response.ofJson(GroceryList(
                    items = groceryList.map {
                        GroceryListEntry(
                                ingredient = Ingredient(
                                        name = it.name,
                                        unit = it.unit,
                                        quantity = it.requiredQuantity - it.purchasedQuantity
                                ),
                                purchased = it.requiredQuantity <= it.purchasedQuantity,
                                dependants = it.dependants.map { it.meal.mealName }.distinct()
                        )
                    }
            ))
        }
    }

    private fun List<GroceryListItem>.cull(): List<GroceryListItem> {
        val now = DateTime.now().millis
        return map { ingredient -> ingredient.trim { it.date < now } }
                .filter { it.requiredQuantity > 0.001f }
    }

    fun markPurchased(userToken: String?, ingredient: Ingredient?): Response {
        if (ingredient == null) {
            return Response.ofError("No ingredient was provided", HttpStatusCode.BadRequest)
        }

        return AuthManager.ensureValidUser(userToken) { userId ->
            val groceryList = db["groceries/$userId"]
                    .firstBlockingList<GroceryListItem>()
                    .orEmpty()
                    .map { item ->
                        if (item.offer(ingredient)) {
                            item.copy(purchasedQuantity = item.requiredQuantity)
                        } else {
                            item
                        }
                    }

            db["groceries/$userId"].setValue(groceryList).block()

            return@ensureValidUser Response.ofStatus("Ok")
        }
    }

    fun markUnpurchased(userToken: String?, ingredient: Ingredient?): Response {
        if (ingredient == null) {
            return Response.ofError("No ingredient was provided", HttpStatusCode.BadRequest)
        }

        return AuthManager.ensureValidUser(userToken) { userId ->
            val groceryList = db["groceries/$userId"]
                    .firstBlockingList<GroceryListItem>()
                    .orEmpty()
                    .map { item ->
                        if (item.offer(ingredient)) {
                            item.copy(purchasedQuantity = 0f)
                        } else {
                            item
                        }
                    }

            db["groceries/$userId"].setValue(groceryList).block()

            return@ensureValidUser Response.ofStatus("Ok")
        }
    }

    private data class GroceryListItem(
            val name: String,
            val purchasedQuantity: Float,
            val requiredQuantity: Float,
            val unit: String,
            val dependants: List<GroceryListDependant>
    ) {

        @Suppress("unused")
        constructor(): this("", 0f, 0f, "", emptyList())

        fun offer(ingredient: Ingredient) = ingredient.name == name && ingredient.unit == unit

        fun contains(dependant: MealPlanItem) = dependants.any { it.meal == dependant }

        fun add(ingredient: Ingredient, dependant: MealPlanItem): GroceryListItem {
            require(ingredient.name == name) {
                "Attempting to merge different ingredients"
            }

            require(ingredient.unit == unit) {
                "Incompatible units: cannot add \"${ingredient.unit}\" to \"$unit\""
            }

            return copy(
                    requiredQuantity = requiredQuantity + ingredient.quantity * dependant.servings,
                    dependants = dependants + GroceryListDependant(
                            meal = dependant,
                            contribution = ingredient.quantity * dependant.servings
                    )
            )
        }

        fun remove(dependant: MealPlanItem): GroceryListItem {
            val dependantContribution = dependants.firstOrNull { it.meal == dependant }?.contribution
                    ?: throw IllegalArgumentException("Cannot update ingredient quantity" +
                            " because \"$dependant\" is not a dependant")

            return copy(
                    requiredQuantity = requiredQuantity - dependantContribution,
                    dependants = dependants.filter { it.meal != dependant }
            )
        }

        fun trim(predicate: (MealPlanItem) -> Boolean): GroceryListItem {
            val removedQuantity = dependants
                    .filter { predicate(it.meal) }
                    .map { it.contribution }
                    .sum()

            return copy(
                    requiredQuantity = requiredQuantity - removedQuantity,
                    purchasedQuantity = Math.max(0f, purchasedQuantity - removedQuantity),
                    dependants = dependants.filter { !predicate(it.meal) }
            )
        }
    }

    private data class GroceryListDependant(
            val meal: MealPlanItem,
            val contribution: Float
    ) {

        @Suppress("unused")
        constructor(): this(MealPlanItem(), 0f)

    }

    private data class MealPlanItem(
            val mealName: String,
            val mealId: String,
            val servings: Int,
            val date: Timestamp
    ) {

        @Suppress("unused")
        constructor(): this("", "", 0, 0)

    }

    private operator fun List<MealPlanItem>.get(mealId: String) = first { it.mealId == mealId }

}