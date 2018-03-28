package com.mealsmadeeasy.data

import org.jetbrains.ktor.http.HttpStatusCode

class SearchQueryException(message: String, val responseCode: HttpStatusCode) : Exception(message)
