package com.mealsmadeeasy.data.edamam.model

enum class HealthLabels(val webLabel: String, val apiParameter: String) {

    ALCOHOL_FREE("Alcohol-Free", "alcohol-free"),
    CELERY_FREE("Celery-Free", "celery-free"),
    CRUSTACEAN_FREE("Crustacean-Free", "crustacean-free"),
    DAIRY_FREE("Dairy", "dairy-free"),
    EGG_FREE("Eggs", "egg-free"),
    FISH_FREE("Fish", "fish-free"),
    GLUTEN_FREE("Gluten", "gluten-free"),
    KIDNEY_FRIENDLY("Kidney friendly", "kidney-friendly"),
    KOSHER("Kosher", "kosher"),
    LOW_POTASSIUM("Low potassium", "low-potassium"),
    LUPINE_FREE("Lupine-Free", "lupine-free"),
    MUSTARD_FREE("Mustard-Free", "mustard-free"),
    NO_OUL_ADDED("No oil added", "No-oil-added"),
    LOW_SUGAR("No-sugar", "low-sugar"),
    PALEO("Paleo", "paleo"),
    PEANUT_FREE("Peanut-Free", "peanut-free"),
    PESCATARIAN("Pescatarian", "pescatarian"),
    PORK_FREE("Pork-Free", "pork-free"),
    RED_MEAT_FREE("Red meat-Free", "red-meat-free"),
    SESAME_FREE("Sesame-Free", "sesame-free"),
    SHELLFISH_FREE("Shelfish", "shellfish-free"),
    SOY_FREE("Soy", "soy-free"),
    SUGAR_CONSCIOUS("Sugar-Conscious", "sugar-conscious"),
    TREE_NUT_FREE("Tree-Nut-Free", "tree-nut-free"),
    VEGAN("Vegan", "vegan"),
    VEGETARIAN("Vegetarian", "vegetarian"),
    WHEAT_FREE("Wheat-Free", "wheat-free"),

}