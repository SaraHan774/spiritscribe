package com.august.spiritscribe.ui.note

import com.august.spiritscribe.domain.model.WhiskeyNote

data class NoteUIM(
    val id: String,
    val name: String,
    val year: String,
    val age: String,
    val abv: String,
    val description: String,
    val imageUrl: String? = null
)

fun List<WhiskeyNote>.toNoteUIMs(): List<NoteUIM> {
    return this.map {
        NoteUIM(
            id = it.id,
            name = it.name,
            year = it.year?.toString() ?: "",
            age = it.age?.toString() ?: "",
            abv = it.abv.toString(),
            description = it.additionalNotes,
        )
    }
}
