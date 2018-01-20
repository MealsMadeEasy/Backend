package com.mealsmadeeasy.model

data class UserProfile(
        val gender: Gender,
        val birthday: Timestamp,
        val height: Inches,
        val weight: Pounds
) {

    // Used by Firebase reflectively
    @Suppress("unused")
    constructor(): this(Gender.UNDISCLOSED, 0, 0, 0)

}
