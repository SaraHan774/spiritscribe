package com.august.spiritscribe.ui.note

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.august.spiritscribe.data.FakeDataSource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoteListRoute(
    navigateToNoteDetail: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val uimList = FakeDataSource.getNoteUIM()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(uimList) { uim ->
            NoteListItem(
                modifier = Modifier.padding(
                    vertical = 4.dp, horizontal = 8.dp
                ),
                uim = uim,
                onClickItem = { item -> navigateToNoteDetail(item.id) },
                sharedTransitionScope,
                animatedContentScope,
            )
        }
    }
}