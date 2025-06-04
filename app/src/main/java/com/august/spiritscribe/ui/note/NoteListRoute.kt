package com.august.spiritscribe.ui.note

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoteListRoute(
    navigateToWhiskeyDetail: (String) -> Unit,
    navigateToAddWhiskey: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: NoteListViewModel = hiltViewModel()
) {
    val whiskeys = viewModel.whiskeys.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(whiskeys.value) { whiskey ->
                    NoteListItem(
                        modifier = Modifier.padding(
                            vertical = 4.dp, horizontal = 8.dp
                        ),
                        uim = NoteUIM(
                            id = whiskey.id,
                            name = whiskey.name,
                            year = whiskey.year?.toString() ?: "",
                            age = whiskey.age?.toString() ?: "",
                            abv = whiskey.abv.toString(),
                            description = whiskey.description
                        ),
                        onClickItem = { item -> navigateToWhiskeyDetail(item.id) },
                        sharedTransitionScope,
                        animatedContentScope,
                    )
                }
            }
        }
        
        // Add Whiskey FAB
        FloatingActionButton(
            onClick = navigateToAddWhiskey,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Whiskey"
            )
        }
    }
}