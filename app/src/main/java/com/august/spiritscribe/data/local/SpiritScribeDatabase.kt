package com.august.spiritscribe.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.august.spiritscribe.data.local.converter.Converters
import com.august.spiritscribe.data.local.dao.FlavorProfileDao
import com.august.spiritscribe.data.local.dao.WhiskeyDao
import com.august.spiritscribe.data.local.dao.WhiskeyNoteDao
import com.august.spiritscribe.data.local.entity.FlavorProfileEntity
import com.august.spiritscribe.data.local.entity.WhiskeyEntity
import com.august.spiritscribe.data.local.entity.WhiskeyNoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WhiskeyEntity::class,
        WhiskeyNoteEntity::class,
        FlavorProfileEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SpiritScribeDatabase : RoomDatabase() {
    abstract fun whiskeyDao(): WhiskeyDao
    abstract fun whiskeyNoteDao(): WhiskeyNoteDao
    abstract fun flavorProfileDao(): FlavorProfileDao

    companion object {
        fun createCallback(context: Context) = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                instance?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateDatabase(database, context)
                    }
                }
            }
        }

        @Volatile
        private var instance: SpiritScribeDatabase? = null

        fun setInstance(database: SpiritScribeDatabase) {
            instance = database
        }

        private suspend fun prepopulateDatabase(database: SpiritScribeDatabase, context: Context) {
            try {
                // JSON 파일에서 시드 데이터 로드
                val seedData = SeedDataLoader.loadSeedData(context)

                val whiskeyDao = database.whiskeyDao()
                val whiskeyNoteDao = database.whiskeyNoteDao()
                val flavorProfileDao = database.flavorProfileDao()

                // 위스키 데이터 삽입
                seedData.whiskies.forEach { whiskey ->
                    whiskeyDao.insertWhiskey(whiskey)
                }

                // 위스키 노트 데이터 삽입
                seedData.whiskeyNotes.forEach { note ->
                    whiskeyNoteDao.insertNote(note)
                }

                // 풍미 프로파일 데이터 삽입
                seedData.flavorProfiles.forEach { profile ->
                    flavorProfileDao.insertFlavorProfile(profile)
                }
            } catch (e: Exception) {

                // JSON 로드 실패 시 최소한의 샘플 데이터 삽입
                insertMinimalSampleData(database)
            }
        }

        /**
         * JSON 로드 실패 시 사용할 최소한의 샘플 데이터
         */
        private suspend fun insertMinimalSampleData(database: SpiritScribeDatabase) {
            val whiskeyDao = database.whiskeyDao()

            val sampleWhiskey = WhiskeyEntity(
                id = "sample-1",
                name = "Sample Whiskey",
                distillery = "Sample Distillery",
                type = com.august.spiritscribe.domain.model.WhiskeyType.SCOTCH,
                age = 12,
                year = 2020,
                abv = 43.0,
                price = 100.0,
                region = "Scotland",
                description = "A sample whiskey for demonstration purposes.",
                rating = 85,
                imageUris = emptyList(),
                createdAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            )

            try {
                whiskeyDao.insertWhiskey(sampleWhiskey)
                android.util.Log.d("SpiritScribeDatabase", "Inserted minimal sample data")
            } catch (e: Exception) {
                android.util.Log.e(
                    "SpiritScribeDatabase",
                    "Failed to insert even minimal sample data",
                    e
                )
            }
        }
    }
} 