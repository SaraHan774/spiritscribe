package com.august.spiritscribe.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.august.spiritscribe.domain.model.*

@Entity(tableName = "whiskey_notes")
data class WhiskeyNoteEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val distillery: String,
    val origin: String,
    val type: String,
    val age: Int?,
    val year: Int?,
    val abv: Double,
    val price: Double?,
    val sampled: Boolean,
    val colorHue: String,
    val colorIntensity: Int,
    val flavors: List<FlavorProfileEntity>,
    val additionalNotes: String,
    val ratingAppearance: Int,
    val ratingNose: Int,
    val ratingTaste: Int,
    val ratingFinish: Int,
    val ratingOverall: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val userId: String?
) {
    fun toDomain(): WhiskeyNote = WhiskeyNote(
        id = id,
        name = name,
        distillery = distillery,
        origin = origin,
        type = type,
        age = age,
        year = year,
        abv = abv,
        price = price,
        sampled = sampled,
        color = ColorMeter(
            hue = colorHue,
            intensity = colorIntensity
        ),
        flavors = flavors.map { it.toDomain() },
        additionalNotes = additionalNotes,
        finalRating = FinalRating(
            appearance = ratingAppearance,
            nose = ratingNose,
            taste = ratingTaste,
            finish = ratingFinish,
            overall = ratingOverall
        ),
        createdAt = createdAt,
        updatedAt = updatedAt,
        userId = userId
    )

    companion object {
        fun fromDomain(note: WhiskeyNote) = WhiskeyNoteEntity(
            id = note.id,
            name = note.name,
            distillery = note.distillery,
            origin = note.origin,
            type = note.type,
            age = note.age,
            year = note.year,
            abv = note.abv,
            price = note.price,
            sampled = note.sampled,
            colorHue = note.color.hue,
            colorIntensity = note.color.intensity,
            flavors = note.flavors.map { FlavorProfileEntity.fromDomain(it) },
            additionalNotes = note.additionalNotes,
            ratingAppearance = note.finalRating.appearance,
            ratingNose = note.finalRating.nose,
            ratingTaste = note.finalRating.taste,
            ratingFinish = note.finalRating.finish,
            ratingOverall = note.finalRating.overall,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt,
            userId = note.userId
        )
    }
}

data class FlavorProfileEntity(
    val flavor: String,
    val intensity: Int
) {
    fun toDomain() = FlavorProfile(
        flavor = Flavor.valueOf(flavor),
        intensity = intensity
    )

    companion object {
        fun fromDomain(profile: FlavorProfile) = FlavorProfileEntity(
            flavor = profile.flavor.name,
            intensity = profile.intensity
        )
    }
} 