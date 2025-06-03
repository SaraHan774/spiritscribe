package com.august.spiritscribe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.august.spiritscribe.data.local.dao.WhiskeyNoteDao
import com.august.spiritscribe.data.local.entity.FlavorProfileEntity
import com.august.spiritscribe.data.local.entity.WhiskeyNoteEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(
    entities = [WhiskeyNoteEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class SpiritScribeDatabase : RoomDatabase() {
    abstract fun whiskeyNoteDao(): WhiskeyNoteDao
}

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromFlavorProfileList(value: List<FlavorProfileEntity>): String = json.encodeToString(value)

    @TypeConverter
    fun toFlavorProfileList(value: String): List<FlavorProfileEntity> = json.decodeFromString(value)

    @TypeConverter
    fun fromStringList(value: List<String>): String = json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = json.decodeFromString(value)
} 