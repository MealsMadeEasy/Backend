package com.mealsmadeeasy.model

data class Meal (
        val id: String,
        val name: String,
        val description: String,
        val thumbnailUrl: String?
) {

    @Suppress("unused")
    constructor(): this("", "", "", null)

}
