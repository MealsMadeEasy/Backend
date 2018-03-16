package com.mealsmadeeasy.data.edamam.model

data class RecipeResult(
        val recipe: EdamamRecipe,
        val bookmarked: Boolean,
        val bought: Boolean
)