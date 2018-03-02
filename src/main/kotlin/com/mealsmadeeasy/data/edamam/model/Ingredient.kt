package com.mealsmadeeasy.data.edamam.model

data class Ingredient(
        val uri: String,
        val quantity: Float,
        val measure: Measure,
        val weight: Float,
        val food: Food
)