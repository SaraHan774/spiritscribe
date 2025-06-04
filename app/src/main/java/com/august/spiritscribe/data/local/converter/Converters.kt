package com.august.spiritscribe.data.local.converter

import androidx.room.TypeConverter
import com.august.spiritscribe.domain.model.WhiskeyType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromWhiskeyType(value: WhiskeyType): String {
        return value.name
    }

    @TypeConverter
    fun toWhiskeyType(value: String): WhiskeyType {
        return WhiskeyType.valueOf(value)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return json.decodeFromString(value)
    }
} 