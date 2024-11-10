package com.august.spiritscribe.ui.note

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.august.spiritscribe.data.FakeDataSource
import com.august.spiritscribe.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoteListRoute(
    navigateToNoteDetail: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val uimList = FakeDataSource.getNoteUIM().take(1)
    val isEmptyList = true
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(color = Color.LightGray),
            verticalArrangement = if (isEmptyList) LastItemCenteredArrangement else {
                Arrangement.Top
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .background(color = Color.Red),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "HEADER NOTORIOUS", style = MaterialTheme.typography.headlineLarge)
                }
            }

            if (isEmptyList) {
                item {
                    Column(
                        modifier = Modifier.heightIn(min = 255.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "리스트가 비어있습니다.\n상태를 다시 확인해주세요.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
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
        Footer()
    }
}

@Composable
private fun Footer() {
    Column {
        Text(text = "DISCLAIMER")
        Text(text = "- Hello World")
        Text(text = "- Hello World")
        Text(text = "- Hello World")
        Text(text = "- Hello World")
    }
}


// LazyColumn
object LastItemCenteredArrangement : Arrangement.Vertical {
    override fun Density.arrange(
        totalSize: Int,
        sizes: IntArray,
        outPositions: IntArray
    ) {
        var y = 0
        sizes.forEachIndexed { index, size ->
            if (index == sizes.lastIndex) {
                // Center the last item
                val remainingSpace = totalSize - y - size
                val centerPosition = y + (remainingSpace / 2)
                outPositions[index] = centerPosition
            } else {
                // Place the other items in the normal top-to-bottom order
                outPositions[index] = y
                y += size
            }
        }
    }
}
