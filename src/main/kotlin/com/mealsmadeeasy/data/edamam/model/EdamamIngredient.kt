package com.mealsmadeeasy.data.edamam.model

data class EdamamIngredient(
        val uri: String,
        val quantity: Float,
        val measure: Measure,
        val weight: Float,
        val food: Food
)