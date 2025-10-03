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
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ratingDisplay.color.copy(alpha = 0.2f),
                            ratingDisplay.color.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 이모지 별점
                Text(
                    text = ratingDisplay.stars,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                
                // 평점 텍스트
                Text(
                    text = ratingDisplay.text,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ratingDisplay.color,
                    textAlign = TextAlign.Center
                )
                
                // 퍼센트 표시 (선택적)
                if (showPercentage) {
                    Text(
                        text = "${ratingDisplay.percentage}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = ratingDisplay.color.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
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
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = ratingDisplay.color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = ratingDisplay.color.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = ratingDisplay.stars,
                fontSize = 12.sp
            )
            
            Text(
                text = ratingDisplay.text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = ratingDisplay.color
            )
        }
    }
}
