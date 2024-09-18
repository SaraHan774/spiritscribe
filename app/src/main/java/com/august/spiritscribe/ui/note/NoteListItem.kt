package com.august.spiritscribe.ui.note

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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
import com.august.spiritscribe.data.FakeDataSource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoteListItem(
    modifier: Modifier = Modifier,
    uim: NoteUIM,
    onClickItem: (NoteUIM) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    with(sharedTransitionScope) {
        Row(
            modifier = modifier
                .clickable { onClickItem(uim) }
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(size = 4.dp))
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Image(
                modifier = Modifier
                    .sharedElement(
                        animatedVisibilityScope = animatedContentScope,
                        state = sharedTransitionScope.rememberSharedContentState(key = "image${uim.id}")
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
                        append(uim.name)
                    }
                    append("\n")
                    if (uim.year.isNotEmpty()) {
                        append(stringResource(R.string.note_formatter_year, uim.year))
                        append(" · ")
                    }
                    if (uim.age.isNotEmpty()) {
                        append(stringResource(R.string.note_formatter_age, uim.age))
                        append(" · ")
                    }
                    append(stringResource(R.string.note_formatter_abv, uim.abv))
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
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                    text = uim.description,
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
        SharedTransitionLayout {
            // AnimatedContent 는 단지 프리뷰 에러 방지를 위해서 넣는다.
            // animatedContentScope 제공하는 용도임 ;
            AnimatedContent(targetState = Unit, label = "") { s ->
                NoteListItem(
                    uim = FakeDataSource.getNoteUIM()[0],
                    onClickItem = { s },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this
                )
            }
        }
    }
}
