package com.mealsmadeeasy.data.edamam.model

enum class DietLabels(val webLabel: String, val apiParameter: String) {

    BALANCED("Balanced", "balanced"),
    HIGH_FIBER("High-Fiber", "high-fiber"),
    HIGH_PROTEIN("High-Protein", "high-protein"),
    LOW_CARB("Low-Carb", "low-carb"),
    LOW_FAT("Low-Fat", "low-fat"),
    LOW_SODIUM("Low-Sodium", "low-sodium")

}