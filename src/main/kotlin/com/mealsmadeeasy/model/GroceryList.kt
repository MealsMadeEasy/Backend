package com.mealsmadeeasy.model

data class GroceryList(
        val items: List<GroceryListEntry>
)

data class GroceryListEntry(
        val ingredient: Ingredient,
        val purchased: Boolean,
        val dependants: List<String>
)
