package com.mealsmadeeasy.data.edamam.model

data class EdamamRecipe(
        val uri: String,
        val label: String,
        val image: String,
        val source: String,
        val url: String?,
        val yield: Int,
        val calories: Float,
        val totalWeight: Float,
        val ingredients: List<EdamamIngredient>,
        val dietLabels: List<DietLabels>,
        val healthLabels: List<HealthLabels>
)