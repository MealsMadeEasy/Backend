package com.mealsmadeeasy.model

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

typealias Timestamp = Long // Milliseconds since January 1, 1970 (UTC)

fun Timestamp.toDate(): DateTime = DateTime(this, DateTimeZone.UTC)

typealias Pounds = Int

typealias Inches = Int
