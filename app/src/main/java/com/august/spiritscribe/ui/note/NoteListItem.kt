package com.august.spiritscribe.ui.note

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.august.spiritscribe.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoteListItem(
    modifier: Modifier = Modifier,
    itemId: Int, // TODO UIM 으로 교체
    onClickItem: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    with(sharedTransitionScope) {
        Row(
            modifier = modifier
                .clickable { onClickItem(itemId) }
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(size = 4.dp))
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Image(
                modifier = Modifier
                    .sharedElement(
                        animatedVisibilityScope = animatedContentScope,
                        state = sharedTransitionScope.rememberSharedContentState(key = "image$itemId")
                    )
                    .padding(8.dp)
                    .size(width = 80.dp, height = 160.dp)
                    .clip(RoundedCornerShape(4.dp)),
                painter = painterResource(R.drawable.ic_launcher_background),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val annotatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("DISTILLERY #$itemId")
                        append(" · ")
                        append("BOTTLER")
                    }
                    append("\n")
                    append("YEAR")
                    append(" · ")
                    append("AGE")
                    append(" · ")
                    append("ABV")
                }
                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyLarge
                )
                Canvas(
                    modifier = Modifier
                        .padding(end = 8.dp, top = 4.dp, bottom = 4.dp)
                        .fillMaxWidth()
                ) {
                    // 생각하던 라인 모양이 아닌데 일단 흠..이지만 놔둔다.
                    drawLine(
                        color = Color.Gray,
                        strokeWidth = (0.5).dp.toPx(),
                        start = Offset(0f, 1.dp.toPx() / 2),
                        end = Offset(size.width, 1.dp.toPx() / 2),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(3f, 20f)
                        )
                    )
                }
                Text(
                    text = stringResource(R.string.lorem_20_words),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun Preview(modifier: Modifier = Modifier) {
    MaterialTheme {
        // TODO : fix preview!!!
//        NoteListItem(
//            itemId = 0,
//            onClickItem = {},
//        )
    }
}
