package com.august.spiritscribe.data.local

import android.content.Context
import com.august.spiritscribe.data.local.entity.FlavorProfileEntity
import com.august.spiritscribe.data.local.entity.WhiskeyEntity
import com.august.spiritscribe.data.local.entity.WhiskeyNoteEntity
import com.august.spiritscribe.domain.model.WhiskeyType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

/**
 * JSON 파일에서 위스키 시드 데이터를 로드하고 엔티티로 변환하는 클래스
 */
object SeedDataLoader {

    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }

    /**
     * assets/whiskey_seed.json 파일을 읽고 파싱하여 엔티티 리스트를 반환
     */
    suspend fun loadSeedData(context: Context): SeedData {
        val jsonString = context.assets.open("whiskey_seed.json").bufferedReader().use { it.readText() }
        val seedDataJson = json.decodeFromString<SeedDataJson>(jsonString)
        
        return SeedData(
            whiskies = seedDataJson.whiskeys.map { it.toEntity() },
            whiskeyNotes = seedDataJson.whiskey_notes.map { it.toEntity() },
            flavorProfiles = seedDataJson.flavor_profiles.map { it.toEntity() }
        )
    }

    /**
     * 변환된 시드 데이터를 담는 데이터 클래스
     */
    data class SeedData(
        val whiskies: List<WhiskeyEntity>,
        val whiskeyNotes: List<WhiskeyNoteEntity>,
        val flavorProfiles: List<FlavorProfileEntity>
    )

    // JSON 구조와 매칭되는 직렬화 가능한 데이터 클래스들
    @Serializable
    private data class SeedDataJson(
        val whiskeys: List<WhiskeyJson>,
        val whiskey_notes: List<WhiskeyNoteJson>,
        val flavor_profiles: List<FlavorProfileJson>
    )

    @Serializable
    private data class WhiskeyJson(
        val id: String,
        val name: String,
        val distillery: String,
        val type: String,
        val age: Int?,
        val year: Int?,
        val abv: Double,
        val price: Double?,
        val region: String?,
        val description: String,
        val rating: Int?,
        val imageUris: String, // JSON에서는 문자열로 저장됨
        val createdAt: String,
        val updatedAt: String
    ) {
        fun toEntity(): WhiskeyEntity = WhiskeyEntity(
            id = id,
            name = name,
            distillery = distillery,
            type = WhiskeyType.fromString(type),
            age = age,
            year = year,
            abv = abv,
            price = price,
            region = region,
            description = description,
            rating = rating,
            imageUris = parseImageUris(imageUris),
            createdAt = LocalDateTime.parse(createdAt),
            updatedAt = LocalDateTime.parse(updatedAt)
        )

        private fun parseImageUris(imageUrisString: String): List<String> {
            return try {
                json.decodeFromString<List<String>>(imageUrisString)
            } catch (e: Exception) {
                // JSON 파싱 실패 시 빈 리스트 반환
                android.util.Log.w("SeedDataLoader", "Failed to parse imageUris: $imageUrisString", e)
                emptyList()
            }
        }
    }

    @Serializable
    private data class WhiskeyNoteJson(
        val id: String,
        val name: String,
        val distillery: String,
        val origin: String,
        val type: String,
        val age: Int?,
        val year: Int?,
        val abv: Double,
        val price: Double?,
        val sampled: Int, // JSON에서는 0/1 정수
        val colorHue: String?,
        val colorIntensity: Int?,
        val additionalNotes: String,
        val appearance: Int?,
        val nose: Int?,
        val taste: Int?,
        val finish: Int?,
        val overall: Int?,
        val imageUrl: String?
    ) {
        fun toEntity(): WhiskeyNoteEntity = WhiskeyNoteEntity(
            id = id,
            name = name,
            distillery = distillery,
            origin = origin,
            type = type,
            age = age,
            year = year,
            abv = abv,
            price = price,
            sampled = sampled == 1, // 1이면 true, 0이면 false
            colorHue = colorHue,
            colorIntensity = colorIntensity,
            additionalNotes = additionalNotes,
            appearance = appearance,
            nose = nose,
            taste = taste,
            finish = finish,
            overall = overall,
            imageUrl = imageUrl
        )
    }

    @Serializable
    private data class FlavorProfileJson(
        val id: String,
        val whiskeyId: String,
        val sweetness: Int,
        val smokiness: Int,
        val spiciness: Int,
        val fruitiness: Int,
        val woodiness: Int,
        val notes: String
    ) {
        fun toEntity(): FlavorProfileEntity = FlavorProfileEntity(
            id = id,
            whiskeyId = whiskeyId,
            sweetness = sweetness,
            smokiness = smokiness,
            spiciness = spiciness,
            fruitiness = fruitiness,
            woodiness = woodiness,
            notes = notes
        )
    }
}
