package com.august.spiritscribe.ui.note

import com.august.spiritscribe.model.Note

data class NoteUIM(
    val id: String,
    val distillery: String,
    val bottler: String,
    val year: String,
    val age: String,
    val abv: String,
    val description: String,
)

fun List<Note>.toNoteUIMs(): List<NoteUIM> {
    return this.map {
        NoteUIM(
            id = it.id,
            distillery = it.distillery,
            bottler = it.bottler,
            year = it.year?.toString() ?: "",
            age = it.age?.toString() ?: "",
            abv = it.abv.toString(),
            description = it.notes,
        )
    }
}
