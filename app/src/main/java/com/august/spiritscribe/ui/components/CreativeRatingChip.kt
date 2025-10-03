package com.august.spiritscribe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

@Composable
fun CreativeRatingChip(
    rating: Int,
    modifier: Modifier = Modifier,
    showPercentage: Boolean = false
) {
    val ratingDisplay = getRatingDisplay(rating)
    
    Surface(
        modifier = modifier.widthIn(min = 40.dp), // 최소/최대 너비 제한
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ratingDisplay.color.copy(alpha = 0.2f),
                            ratingDisplay.color.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            // 이모지 별점만 표시
            Text(
                text = ratingDisplay.stars,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CompactRatingChip(
    rating: Int,
    modifier: Modifier = Modifier
) {
    val ratingDisplay = getRatingDisplay(rating)
    
    Surface(
        modifier = modifier.widthIn(min = 30.dp, max = 60.dp), // 최소/최대 너비 제한
        shape = RoundedCornerShape(10.dp),
        color = ratingDisplay.color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = ratingDisplay.color.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            // 이모지 별점만 표시
            Text(
                text = ratingDisplay.stars,
                fontSize = 12.sp
            )
        }
    }
}
