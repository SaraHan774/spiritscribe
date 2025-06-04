package com.august.spiritscribe.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "flavor_profiles",
    foreignKeys = [
        ForeignKey(
            entity = WhiskeyNoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["whiskeyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FlavorProfileEntity(
    @PrimaryKey
    val id: String,
    val whiskeyId: String,
    val sweetness: Int,
    val smokiness: Int,
    val spiciness: Int,
    val fruitiness: Int,
    val woodiness: Int,
    val notes: String
)