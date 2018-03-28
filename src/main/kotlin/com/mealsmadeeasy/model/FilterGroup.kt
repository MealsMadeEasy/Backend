package com.mealsmadeeasy.model

data class FilterGroup(
        val groupId: String,
        val groupName: String,
        val filters: List<Filter>,
        val maximumActive: Int = filters.size
)
