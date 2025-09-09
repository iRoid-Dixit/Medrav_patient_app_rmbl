package com.medrevpatient.mobile.app.util.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

/**
 * Gson adapter that can parse either epoch seconds (number or numeric string)
 * or ISO-8601 datetime strings with timezone into epoch seconds (Long).
 */
class DateOrEpochSecondsAdapter : JsonDeserializer<Long>, JsonSerializer<Long> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long? {
        if (json == null || json.isJsonNull) return null
        val primitive = json.asJsonPrimitive

        // If already a number, assume it is epoch seconds
        if (primitive.isNumber) {
            return try {
                primitive.asLong
            } catch (e: Exception) {
                throw JsonParseException("Invalid numeric epoch seconds: $primitive", e)
            }
        }

        if (primitive.isString) {
            val value = primitive.asString.trim()
            if (value.isEmpty()) return null

            // Try numeric string as epoch seconds
            try {
                return value.toLong()
            } catch (_: NumberFormatException) {
                // Not a plain number; continue
            }

            // Try to parse ISO-8601 using java.time (preferred)
            try {
                return OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toEpochSecond()
            } catch (_: Exception) { }
            try {
                return Instant.parse(value).epochSecond
            } catch (_: Exception) { }

            // Fallback: try common patterns with SimpleDateFormat
            val patterns = listOf(
                "yyyy-MM-dd'T'HH:mm:ssXXX",            // 2025-09-09T12:12:12+05:30
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",        // 2025-09-09T12:12:12.955+05:30
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",     // 2025-09-09T12:12:12.955844+05:30
                "yyyy-MM-dd"                            // 2025-09-09
            )
            for (pattern in patterns) {
                try {
                    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                    val date = sdf.parse(value)
                    if (date != null) return date.time / 1000
                } catch (_: ParseException) { }
            }

            throw JsonParseException("Unsupported date format: $value")
        }

        throw JsonParseException("Unsupported JSON type for date: ${json.javaClass}")
    }

    override fun serialize(src: Long?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return if (src == null) null else JsonPrimitive(src)
    }
}


