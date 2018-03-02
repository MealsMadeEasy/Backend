package com.mealsmadeeasy.data.edamam.model

data class RecipeResult(
        val recipe: Recipe,
        val bookmarked: Boolean,
        val bought: Boolean
)