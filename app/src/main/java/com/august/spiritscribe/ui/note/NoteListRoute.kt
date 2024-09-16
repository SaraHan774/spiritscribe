package com.august.spiritscribe.ui.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NoteListRoute(navigateToNoteDetail: (String) -> Unit) {
    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(10) { i ->
            NoteListItem(
                modifier = Modifier.padding(
                    vertical = 4.dp, horizontal = 8.dp
                ),
                itemId = i,
                onClickItem = { id -> navigateToNoteDetail(id.toString()) }
            )
        }
    }
}