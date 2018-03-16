package com.mealsmadeeasy.data.mercury

import com.squareup.moshi.Json

data class ParseResult(
        val title: String,
        val content: String,
        @Json(name = "date_published") val datePublished: String?,
        @Json(name = "lead_image_url") val imageUrl: String?,
        val dek: String?,
        val url: String,
        val domain: String,
        val excerpt: String?,
        val direction: String,
        @Json(name = "word_count") val wordCount: Int,
        @Json(name = "total_pages") val totalPages: Int,
        @Json(name = "rendered_pages") val renderedPages: Int,
        @Json(name = "next_page_url") val nextPageUrl: String?
)
