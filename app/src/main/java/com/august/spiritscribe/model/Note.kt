package com.august.spiritscribe.model

data class Note(
    val id: String,
    val distillery: String,
    val bottler: String,
    val year: Int?,
    val age: Int?,
    val abv: Double,
    val nose: String,
    val palate: String,
    val finish: String,
    val rating: Float,
    val tastingDate: String,
    val notes: String
)
