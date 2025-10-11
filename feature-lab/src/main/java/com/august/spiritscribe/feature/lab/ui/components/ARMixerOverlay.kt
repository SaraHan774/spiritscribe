package com.august.spiritscribe.feature.lab.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.august.spiritscribe.feature.lab.domain.ARLineColors
import com.august.spiritscribe.feature.lab.domain.MixingRatio

/**
 * AR 믹서 오버레이 (비율 라인, 상태 표시)
 */
@Composable
fun ARMixerOverlay(
    isTracking: Boolean,
    currentRatio: MixingRatio,
    liquidLevel: Float,
    error: String?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // AR 라인 렌더링
        ARLinesCanvas(
            currentRatio = currentRatio,
            liquidLevel = liquidLevel,
            isTracking = isTracking,
            modifier = Modifier.fillMaxSize()
        )

        // 상태 표시
        when {
            error != null -> {
                ErrorOverlay(
                    error = error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            !isTracking -> {
                TrackingOverlay(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // 액체 레벨 표시
        LiquidLevelIndicator(
            level = liquidLevel,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun ARLinesCanvas(
    currentRatio: MixingRatio,
    liquidLevel: Float,
    isTracking: Boolean,
    modifier: Modifier = Modifier
) {
    var centerX by remember { mutableStateOf(0f) }
    var centerY by remember { mutableStateOf(0f) }
    var radius by remember { mutableStateOf(0f) }

    // 애니메이션
    val alpha by animateFloatAsState(
        targetValue = if (isTracking) 1f else 0.3f,
        animationSpec = tween(durationMillis = 300),
        label = "lineAlpha"
    )

    Canvas(modifier = modifier) {
        centerX = size.width / 2f
        centerY = size.height / 2f
        radius = minOf(size.width, size.height) * 0.3f

        if (isTracking) {
            // 위스키 라인 (갈색)
            val whiskeyLineY = centerY - radius * (1f - currentRatio.ratio)
            drawLine(
                color = Color(ARLineColors.WHISKEY_LINE).copy(alpha = alpha),
                start = Offset(centerX - radius, whiskeyLineY),
                end = Offset(centerX + radius, whiskeyLineY),
                strokeWidth = 4.dp.toPx()
            )

            // 토닉워터 라인 (시안색)
            val tonicLineY = centerY + radius * (1f - currentRatio.ratio)
            drawLine(
                color = Color(ARLineColors.TONIC_LINE).copy(alpha = alpha),
                start = Offset(centerX - radius, tonicLineY),
                end = Offset(centerX + radius, tonicLineY),
                strokeWidth = 4.dp.toPx()
            )

            // 액체 레벨 표시
            val liquidY = centerY - radius + (radius * 2 * liquidLevel)
            drawLine(
                color = Color(ARLineColors.MIXED_LINE).copy(alpha = alpha * 0.7f),
                start = Offset(centerX - radius * 0.8f, liquidY),
                end = Offset(centerX + radius * 0.8f, liquidY),
                strokeWidth = 6.dp.toPx()
            )

            // 컵 윤곽선
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.5f),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Composable
private fun ErrorOverlay(
    error: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "오류",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "AR 오류",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun TrackingOverlay(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Blue.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "추적 중",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "컵을 찾는 중...",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "컵을 카메라에 비춰주세요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun LiquidLevelIndicator(
    level: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "액체 레벨",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = "${(level * 100).toInt()}%",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            // 프로그레스 바
            LinearProgressIndicator(
                progress = { level },
                modifier = Modifier
                    .width(60.dp)
                    .height(4.dp),
                color = Color(ARLineColors.MIXED_LINE)
            )
        }
    }
}
