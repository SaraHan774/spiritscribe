package com.august.spiritscribe.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.august.spiritscribe.domain.model.WhiskeyType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

@Entity(tableName = "whiskeys")
data class WhiskeyEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val distillery: String,
    val type: WhiskeyType,
    val age: Int?,
    val year: Int?,
    val abv: Double,
    val price: Double?,
    val region: String?,
    val description: String,
    val rating: Int?,
    val imageUris: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

class Converters {
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
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Json.decodeFromString(value)
    }
} 