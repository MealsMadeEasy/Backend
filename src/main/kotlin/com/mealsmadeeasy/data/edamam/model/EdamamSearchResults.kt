package com.mealsmadeeasy.data.edamam.model

data class EdamamSearchResults(
        val from: Int,
        val to: Int,
        val more: Boolean,
        val count: Int,
        val hits: List<RecipeResult>
)