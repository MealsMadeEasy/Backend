package com.mealsmadeeasy.data.edamam.model

enum class DietLabels(
        val webLabel: String,
        val apiParameter: String,
        val isPremium: Boolean = true
) {

    BALANCED("Balanced", "balanced", isPremium = false),
    HIGH_FIBER("High-Fiber", "high-fiber"),
    HIGH_PROTEIN("High-Protein", "high-protein", isPremium = false),
    LOW_CARB("Low-Carb", "low-carb", isPremium = false),
    LOW_FAT("Low-Fat", "low-fat", isPremium = false),
    LOW_SODIUM("Low-Sodium", "low-sodium")

}