package com.august.spiritscribe.ui.note

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.august.spiritscribe.R
import com.august.spiritscribe.data.FakeDataSource

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

    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
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
            FlavorProfileGraph()
        }
    }
}