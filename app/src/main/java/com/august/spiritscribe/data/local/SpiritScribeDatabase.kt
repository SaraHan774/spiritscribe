package com.august.spiritscribe.data.local

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
import com.august.spiritscribe.domain.model.ColorMeter
import com.august.spiritscribe.domain.model.Flavor
import com.august.spiritscribe.domain.model.FlavorIntensity
import com.august.spiritscribe.domain.model.WhiskeyNote
import com.august.spiritscribe.domain.model.WhiskeyType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

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

    companion object {
        val callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                android.util.Log.d("SpiritScribeDatabase", "onCreate called")
                instance?.let { database ->
                    android.util.Log.d("SpiritScribeDatabase", "Instance found, launching coroutine")
                    CoroutineScope(Dispatchers.IO).launch {
                        android.util.Log.d("SpiritScribeDatabase", "Starting database population")
                        prepopulateDatabase(database)
                        android.util.Log.d("SpiritScribeDatabase", "Finished database population")
                    }
                } ?: android.util.Log.e("SpiritScribeDatabase", "Instance is null in onCreate")
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                android.util.Log.d("SpiritScribeDatabase", "onDestructiveMigration called")
            }
        }

        private var instance: SpiritScribeDatabase? = null

        fun setInstance(database: SpiritScribeDatabase) {
            android.util.Log.d("SpiritScribeDatabase", "Setting database instance")
            instance = database
        }

        private suspend fun prepopulateDatabase(database: SpiritScribeDatabase) {
            android.util.Log.d("SpiritScribeDatabase", "Starting prepopulateDatabase")
            val whiskeyDao = database.whiskeyDao()
            val whiskeyNoteDao = database.whiskeyNoteDao()

            // Sample whiskies
            val whiskies = listOf(
                WhiskeyEntity(
                    id = "1",
                    name = "Macallan 12",
                    distillery = "The Macallan",
                    type = WhiskeyType.SCOTCH,
                    age = 12,
                    year = 2021,
                    abv = 43.0,
                    price = 89.99,
                    region = "Speyside",
                    description = "Rich and complex with notes of dried fruits, vanilla, and spice.",
                    rating = 92,
                    imageUris = listOf(),
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now()
                ),
                WhiskeyEntity(
                    id = "2",
                    name = "Highland Park 18",
                    distillery = "Highland Park",
                    type = WhiskeyType.SCOTCH,
                    age = 18,
                    year = 2020,
                    abv = 43.0,
                    price = 159.99,
                    region = "Highland",
                    description = "Balanced sweetness with heather honey, dark chocolate, and gentle smoke.",
                    rating = 94,
                    imageUris = listOf(),
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now()
                ),
                WhiskeyEntity(
                    id = "3",
                    name = "Lagavulin 16",
                    distillery = "Lagavulin",
                    type = WhiskeyType.SCOTCH,
                    age = 16,
                    year = 2019,
                    abv = 43.0,
                    price = 109.99,
                    region = "Islay",
                    description = "Intense peat smoke with iodine, rich sweetness and hints of salt and wood.",
                    rating = 93,
                    imageUris = listOf(),
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now()
                )
            )

            // Insert whiskies
            whiskies.forEach { whiskey ->
                try {
                    whiskeyDao.insertWhiskey(whiskey)
                    android.util.Log.d("SpiritScribeDatabase", "Successfully inserted whiskey: ${whiskey.name}")
                } catch (e: Exception) {
                    android.util.Log.e("SpiritScribeDatabase", "Failed to insert whiskey: ${whiskey.name}", e)
                    e.printStackTrace()
                }
            }

            // Sample notes for each whiskey
            val notes = listOf(
                // Macallan 12 notes
                WhiskeyNote(
                    id = UUID.randomUUID().toString(),
                    name = whiskies[0].name,
                    distillery = whiskies[0].distillery,
                    origin = whiskies[0].region ?: "",
                    type = whiskies[0].type.name,
                    age = whiskies[0].age,
                    year = whiskies[0].year,
                    abv = whiskies[0].abv,
                    price = whiskies[0].price,
                    sampled = true,
                    color = ColorMeter("Amber", 4),
                    flavors = listOf(
                        FlavorIntensity(Flavor.DRIED, 3),
                        FlavorIntensity(Flavor.WOOD, 2),
                        FlavorIntensity(Flavor.VANILLA, 4)
                    ),
                    additionalNotes = "First impression: Lovely sherry influence with prominent vanilla notes.",
                    finalRating = com.august.spiritscribe.domain.model.FinalRating(
                        appearance = 85,
                        nose = 86,
                        taste = 85,
                        finish = 84,
                        overall = 85
                    ),
                    createdAt = System.currentTimeMillis() - (3 * 86400000) // 3 days ago
                ),
                // Highland Park 18 notes
                WhiskeyNote(
                    id = UUID.randomUUID().toString(),
                    name = whiskies[1].name,
                    distillery = whiskies[1].distillery,
                    origin = whiskies[1].region ?: "",
                    type = whiskies[1].type.name,
                    age = whiskies[1].age,
                    year = whiskies[1].year,
                    abv = whiskies[1].abv,
                    price = whiskies[1].price,
                    sampled = true,
                    color = ColorMeter("Deep Gold", 5),
                    flavors = listOf(
                        FlavorIntensity(Flavor.HONEY, 4),
                        FlavorIntensity(Flavor.VANILLA, 3),
                        FlavorIntensity(Flavor.HERB, 2),
                        FlavorIntensity(Flavor.CHAR, 3)
                    ),
                    additionalNotes = "Exceptional balance between sweetness and gentle smoke. The honey notes are particularly impressive.",
                    finalRating = com.august.spiritscribe.domain.model.FinalRating(
                        appearance = 92,
                        nose = 94,
                        taste = 95,
                        finish = 93,
                        overall = 94
                    ),
                    createdAt = System.currentTimeMillis() - (2 * 86400000) // 2 days ago
                ),
                // Lagavulin 16 notes
                WhiskeyNote(
                    id = UUID.randomUUID().toString(),
                    name = whiskies[2].name,
                    distillery = whiskies[2].distillery,
                    origin = whiskies[2].region ?: "",
                    type = whiskies[2].type.name,
                    age = whiskies[2].age,
                    year = whiskies[2].year,
                    abv = whiskies[2].abv,
                    price = whiskies[2].price,
                    sampled = true,
                    color = ColorMeter("Deep Amber", 5),
                    flavors = listOf(
                        FlavorIntensity(Flavor.HONEY, 4),
                        FlavorIntensity(Flavor.VANILLA, 3),
                        FlavorIntensity(Flavor.HERB, 2),
                        FlavorIntensity(Flavor.CHAR, 3)
                    ),
                    additionalNotes = "Classic Islay character. The peat smoke is beautifully integrated with maritime notes.",
                    finalRating = com.august.spiritscribe.domain.model.FinalRating(
                        appearance = 91,
                        nose = 93,
                        taste = 94,
                        finish = 92,
                        overall = 93
                    ),
                    createdAt = System.currentTimeMillis() - 86400000 // 1 day ago
                )
            )

            // Insert notes
            notes.forEach { note ->
                try {
                    val entity = WhiskeyNoteEntity.fromDomain(note)
                    android.util.Log.d("SpiritScribeDatabase", "Converting note to entity: ${note.name}")
                    whiskeyNoteDao.insertNote(entity)
                    android.util.Log.d("SpiritScribeDatabase", "Successfully inserted note for: ${note.name}")
                } catch (e: Exception) {
                    android.util.Log.e("SpiritScribeDatabase", "Failed to insert note for: ${note.name}", e)
                    e.printStackTrace()
                }
            }
        }
    }
} 