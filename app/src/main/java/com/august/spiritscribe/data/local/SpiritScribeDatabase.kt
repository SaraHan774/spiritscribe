package com.august.spiritscribe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.august.spiritscribe.data.local.converter.Converters
import com.august.spiritscribe.data.local.dao.FlavorProfileDao
import com.august.spiritscribe.data.local.dao.WhiskeyDao
import com.august.spiritscribe.data.local.dao.WhiskeyNoteDao
import com.august.spiritscribe.data.local.entity.FlavorProfileEntity
import com.august.spiritscribe.data.local.entity.WhiskeyEntity
import com.august.spiritscribe.data.local.entity.WhiskeyNoteEntity

@Database(
    entities = [
        WhiskeyEntity::class,
        WhiskeyNoteEntity::class,
        FlavorProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SpiritScribeDatabase : RoomDatabase() {
    abstract fun whiskeyDao(): WhiskeyDao
    abstract fun whiskeyNoteDao(): WhiskeyNoteDao
    abstract fun flavorProfileDao(): FlavorProfileDao
} 