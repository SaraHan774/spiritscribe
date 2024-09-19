package com.august.spiritscribe.ui.note

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.august.spiritscribe.R
import com.august.spiritscribe.data.FakeDataSource
import com.august.spiritscribe.ui.poc.NewThreadItem

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoteDetailRoute(
    id: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val scrollState = rememberScrollState()
    // FIXME : viewModel
    val note = requireNotNull(FakeDataSource.getNoteUIM().find { it.id == id })
    var isNoteBoxVisible by remember { mutableStateOf(false) }

    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .verticalScroll(state = scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .sharedElement(
                        state = sharedTransitionScope.rememberSharedContentState(key = "image$id"),
                        animatedVisibilityScope = animatedContentScope,
                    )
            )
            Text(text = "NAME ${note.name}")
            Text(text = "AGE ${note.age}")
            Text(text = "YEAR ${note.year}")
            Text(text = "DESCRIPTION ${note.description}")
            Text(text = "ABV ${note.abv}")
            Button(onClick = { isNoteBoxVisible = !isNoteBoxVisible }) {
                Row {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    Text(text = "ADD NOTE")
                }
            }
            // TODO : don't let this toggle,
            //  make this like a infinite thread
            if (isNoteBoxVisible) {
                Spacer(Modifier.height(16.dp))
                NoteBox()
            }
        }
    }
}

@Composable
fun NoteBox(modifier: Modifier = Modifier) {
     NewThreadItem()
}