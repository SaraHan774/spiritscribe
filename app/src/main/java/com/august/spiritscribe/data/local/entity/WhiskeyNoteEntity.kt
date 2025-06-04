package com.august.spiritscribe.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.august.spiritscribe.domain.model.ColorMeter
import com.august.spiritscribe.domain.model.FinalRating
import com.august.spiritscribe.domain.model.WhiskeyNote

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
    val colorHue: String?,
    val colorIntensity: Int?,
    val additionalNotes: String,
    val appearance: Int?,
    val nose: Int?,
    val taste: Int?,
    val finish: Int?,
    val overall: Int?
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
            hue = colorHue ?: "",
            intensity = colorIntensity ?: 0
        ),
        flavors = emptyList(), // Flavors are loaded separately through FlavorProfileDao
        additionalNotes = additionalNotes,
        finalRating = FinalRating(
            appearance = appearance ?: 0,
            nose = nose ?: 0,
            taste = taste ?: 0,
            finish = finish ?: 0,
            overall = overall ?: 0
        )
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
            additionalNotes = note.additionalNotes,
            appearance = note.finalRating.appearance,
            nose = note.finalRating.nose,
            taste = note.finalRating.taste,
            finish = note.finalRating.finish,
            overall = note.finalRating.overall
        )
    }
}
