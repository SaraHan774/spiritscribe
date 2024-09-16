package com.august.spiritscribe.ui.note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NoteDetailRoute(id: String) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(state = scrollState)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Distillery Aberlour")
        Text("Note Detail_$id")
        Text("Note Detail_$id")
        Text("Note Detail_$id")
        Text("Note Detail_$id")
    }
}