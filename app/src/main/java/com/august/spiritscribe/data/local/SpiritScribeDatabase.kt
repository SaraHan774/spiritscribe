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
    version = 4,
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
            android.util.Log.d("SpiritScribeDatabase", "🚀 prepopulateDatabase 시작")
            try {
                // JSON 파일에서 시드 데이터 로드
                android.util.Log.d("SpiritScribeDatabase", "📂 시드 데이터 로드 시작")
                val seedData = SeedDataLoader.loadSeedData(context)
                android.util.Log.d("SpiritScribeDatabase", "✅ 시드 데이터 로드 완료: 위스키 ${seedData.whiskies.size}개, 노트 ${seedData.whiskeyNotes.size}개, 플레이버 ${seedData.flavorProfiles.size}개")

                val whiskeyDao = database.whiskeyDao()
                val whiskeyNoteDao = database.whiskeyNoteDao()
                val flavorProfileDao = database.flavorProfileDao()

                // 위스키 데이터 삽입
                android.util.Log.d("SpiritScribeDatabase", "🍺 위스키 데이터 삽입 시작")
                seedData.whiskies.forEachIndexed { index, whiskey ->
                    try {
                        whiskeyDao.insertWhiskey(whiskey)
                        android.util.Log.d("SpiritScribeDatabase", "✅ 위스키 ${index + 1}/${seedData.whiskies.size} 삽입: ${whiskey.name}")
                    } catch (e: Exception) {
                        android.util.Log.e("SpiritScribeDatabase", "❌ 위스키 삽입 실패: ${whiskey.name}", e)
                    }
                }

                // 위스키 노트 데이터 삽입
                android.util.Log.d("SpiritScribeDatabase", "📝 위스키 노트 데이터 삽입 시작")
                seedData.whiskeyNotes.forEachIndexed { index, note ->
                    try {
                        whiskeyNoteDao.insertNote(note)
                        android.util.Log.d("SpiritScribeDatabase", "✅ 노트 ${index + 1}/${seedData.whiskeyNotes.size} 삽입: ${note.name}")
                    } catch (e: Exception) {
                        android.util.Log.e("SpiritScribeDatabase", "❌ 노트 삽입 실패: ${note.name}", e)
                    }
                }

                // 풍미 프로파일 데이터 삽입
                android.util.Log.d("SpiritScribeDatabase", "🌿 풍미 프로파일 데이터 삽입 시작")
                seedData.flavorProfiles.forEachIndexed { index, profile ->
                    try {
                        flavorProfileDao.insertFlavorProfile(profile)
                        android.util.Log.d("SpiritScribeDatabase", "✅ 플레이버 ${index + 1}/${seedData.flavorProfiles.size} 삽입")
                    } catch (e: Exception) {
                        android.util.Log.e("SpiritScribeDatabase", "❌ 플레이버 삽입 실패", e)
                    }
                }

                android.util.Log.d("SpiritScribeDatabase", "🎉 데이터베이스 초기화 완료!")
            } catch (e: Exception) {
                android.util.Log.e("SpiritScribeDatabase", "❌ 시드 데이터 로드 실패", e)
                // JSON 로드 실패 시 최소한의 샘플 데이터 삽입
                insertMinimalSampleData(database)
            }
        }

        /**
         * JSON 로드 실패 시 사용할 최소한의 샘플 데이터
         */
        private suspend fun insertMinimalSampleData(database: SpiritScribeDatabase) {
            android.util.Log.d("SpiritScribeDatabase", "🔄 최소한의 샘플 데이터 삽입 시작")
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
                android.util.Log.d("SpiritScribeDatabase", "✅ 최소한의 샘플 데이터 삽입 완료: ${sampleWhiskey.name}")
            } catch (e: Exception) {
                android.util.Log.e(
                    "SpiritScribeDatabase",
                    "❌ 최소한의 샘플 데이터 삽입도 실패",
                    e
                )
            }
        }
    }
} 