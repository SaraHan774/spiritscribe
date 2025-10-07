package com.august.spiritscribe.feature.lab.ui.components

import android.graphics.Rect
import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.august.spiritscribe.feature.lab.domain.SwirlDetectionState

/**
 * 실시간 스월 감지 오버레이 UI
 * 화면 중앙에 원형 ROI와 스월 강도 시각화를 표시
 */
@Composable
fun SwirlDetectionOverlay(
    detectionState: SwirlDetectionState,
    swirlScore: Float,
    roi: Rect?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // ROI 원형 오버레이
        roi?.let { roiRect ->
            ROICircleOverlay(
                roi = roiRect,
                detectionState = detectionState,
                swirlScore = swirlScore,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // 상태 표시 텍스트
        DetectionStatusText(
            detectionState = detectionState,
            swirlScore = swirlScore,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ROICircleOverlay(
    roi: Rect,
    detectionState: SwirlDetectionState,
    swirlScore: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    // 스월 강도에 따른 색상 애니메이션
    val colorAnimation by animateColorAsState(
        targetValue = when (detectionState) {
            SwirlDetectionState.IDLE -> Color.White.copy(alpha = 0.7f)
            SwirlDetectionState.DETECTING -> Color.Yellow.copy(alpha = 0.8f)
            SwirlDetectionState.CAPTURING -> Color.Green.copy(alpha = 0.9f)
            SwirlDetectionState.PROCESSING -> Color.Blue.copy(alpha = 0.9f)
        },
        animationSpec = tween(300),
        label = "roi_color"
    )
    
    // 스월 강도에 따른 크기 애니메이션
    val scaleAnimation by animateFloatAsState(
        targetValue = when (detectionState) {
            SwirlDetectionState.IDLE -> 1f
            SwirlDetectionState.DETECTING -> 1.1f
            SwirlDetectionState.CAPTURING -> 1.2f
            SwirlDetectionState.PROCESSING -> 1f
        },
        animationSpec = tween(300),
        label = "roi_scale"
    )
    
    // 맥박 애니메이션 (감지 중일 때)
    val pulseAnimation by animateFloatAsState(
        targetValue = if (detectionState == SwirlDetectionState.DETECTING) 1.3f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = with(density) { roi.width().toPx() / 2f }
        
        // 맥박 효과 (감지 중일 때)
        if (detectionState == SwirlDetectionState.DETECTING) {
            drawCircle(
                color = colorAnimation.copy(alpha = 0.3f),
                radius = radius * pulseAnimation,
                center = Offset(centerX, centerY),
                style = Stroke(width = with(density) { 4.dp.toPx() })
            )
        }
        
        // 메인 ROI 원
        drawCircle(
            color = colorAnimation,
            radius = radius * scaleAnimation,
            center = Offset(centerX, centerY),
            style = Stroke(width = with(density) { 3.dp.toPx() })
        )
        
        // 스월 강도 표시 (원형 프로그레스)
        if (swirlScore > 0) {
            val progress = (swirlScore / 3f).coerceIn(0f, 1f) // 3.0을 최대값으로 가정
            drawArc(
                color = Color.Red,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = with(density) { 6.dp.toPx() })
            )
        }
        
        // 중심 십자가
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(centerX - with(density) { 10.dp.toPx() }, centerY),
            end = Offset(centerX + with(density) { 10.dp.toPx() }, centerY),
            strokeWidth = with(density) { 2.dp.toPx() }
        )
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(centerX, centerY - with(density) { 10.dp.toPx() }),
            end = Offset(centerX, centerY + with(density) { 10.dp.toPx() }),
            strokeWidth = with(density) { 2.dp.toPx() }
        )
    }
}

@Composable
private fun DetectionStatusText(
    detectionState: SwirlDetectionState,
    swirlScore: Float,
    modifier: Modifier = Modifier
) {
    val statusText = when (detectionState) {
        SwirlDetectionState.IDLE -> "위스키 잔을 원 안에 두고 살짝 돌려보세요"
        SwirlDetectionState.DETECTING -> "스월이 감지되었습니다... (${String.format("%.1f", swirlScore)})"
        SwirlDetectionState.CAPTURING -> "자동 캡처 중... (${String.format("%.1f", swirlScore)})"
        SwirlDetectionState.PROCESSING -> "아트 생성 중..."
    }
    
    val statusColor = when (detectionState) {
        SwirlDetectionState.IDLE -> Color.White
        SwirlDetectionState.DETECTING -> Color.Yellow
        SwirlDetectionState.CAPTURING -> Color.Green
        SwirlDetectionState.PROCESSING -> Color.Blue
    }
    
    Card(
        modifier = modifier
            .padding(16.dp)
            .background(Color.Black.copy(alpha = 0.7f), CircleShape),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Text(
            text = statusText,
            color = statusColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/**
 * 스월 강도 실시간 표시기
 */
@Composable
fun SwirlScoreIndicator(
    swirlScore: Float,
    modifier: Modifier = Modifier
) {
    val maxScore = 3f
    val normalizedScore = (swirlScore / maxScore).coerceIn(0f, 1f)
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 점수 텍스트
        Text(
            text = String.format("%.2f", swirlScore),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        // 프로그레스 바
        LinearProgressIndicator(
            progress = normalizedScore,
            modifier = Modifier
                .width(100.dp)
                .height(8.dp)
                .clip(CircleShape),
            color = when {
                normalizedScore < 0.3f -> Color.Green
                normalizedScore < 0.7f -> Color.Yellow
                else -> Color.Red
            },
            trackColor = Color.White.copy(alpha = 0.3f)
        )
        
        Text(
            text = "스월 강도",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp
        )
    }
}
