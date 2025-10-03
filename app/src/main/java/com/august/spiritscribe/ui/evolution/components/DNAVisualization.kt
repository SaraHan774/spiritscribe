package com.august.spiritscribe.ui.evolution.components

import androidx.compose.animation.core.*
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
import com.august.spiritscribe.domain.model.DNAStrand
import com.august.spiritscribe.domain.model.EvolutionStage
import com.august.spiritscribe.domain.model.Flavor
import kotlin.math.*

@Composable
fun DNAVisualization(
    dnaStrands: List<DNAStrand>,
    evolutionStage: EvolutionStage,
    modifier: Modifier = Modifier,
    onStrandClick: (DNAStrand) -> Unit = {}
) {
    val density = LocalDensity.current
    
    // DNA 나선의 애니메이션
    val infiniteTransition = rememberInfiniteTransition(label = "dna_animation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dna_rotation"
    )
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val maxRadius = minOf(size.width, size.height) / 2f - 50f
            
            // 배경 원 그리기
            drawCircle(
                color = Color.Black.copy(alpha = 0.05f),
                radius = maxRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )
            
            // DNA 이중 나선 그리기
            drawDNAHelix(
                center = Offset(centerX, centerY),
                radius = maxRadius,
                strands = dnaStrands,
                evolutionStage = evolutionStage,
                rotation = rotation,
                density = density
            )
        }
        
        // 중앙 정보 표시
        EvolutionCenterInfo(
            evolutionStage = evolutionStage,
            totalStrands = dnaStrands.size,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun EvolutionCenterInfo(
    evolutionStage: EvolutionStage,
    totalStrands: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = evolutionStage.emoji,
                style = MaterialTheme.typography.displayMedium
            )
            
            Text(
                text = evolutionStage.displayName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "DNA: ${totalStrands}개",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = evolutionStage.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun DrawScope.drawDNAHelix(
    center: Offset,
    radius: Float,
    strands: List<DNAStrand>,
    evolutionStage: EvolutionStage,
    rotation: Float,
    density: androidx.compose.ui.unit.Density
) {
    val stageColor = getEvolutionStageColor(evolutionStage)
    
    // DNA 나선을 그리기 위한 점들 계산
    val helixPoints = generateHelixPoints(center, radius, strands, rotation)
    
    // 첫 번째 나선 (주 나선)
    helixPoints.firstOrNull()?.let { firstHelix ->
        drawHelixPath(
            points = firstHelix,
            color = stageColor.copy(alpha = 0.7f),
            strokeWidth = 4f
        )
    }
    
    // 두 번째 나선 (보조 나선) - 약간의 오프셋으로 이중 나선 효과
    helixPoints.getOrNull(1)?.let { secondHelix ->
        drawHelixPath(
            points = secondHelix,
            color = stageColor.copy(alpha = 0.5f),
            strokeWidth = 3f
        )
    }
    
    // 플레이버 노드 그리기
    strands.forEachIndexed { index, strand ->
        val angle = (index * 2 * PI / max(strands.size, 1)) + rotation
        val nodeRadius = radius * (0.6f + (strand.evolutionLevel / 10f))
        val nodeX = center.x + cos(angle.toFloat()) * nodeRadius
        val nodeY = center.y + sin(angle.toFloat()) * nodeRadius
        
        drawFlavorNode(
            center = Offset(nodeX, nodeY),
            strand = strand,
            evolutionStage = evolutionStage
        )
    }
}

private fun generateHelixPoints(
    center: Offset,
    radius: Float,
    strands: List<DNAStrand>,
    rotation: Float
): List<List<Offset>> {
    val pointsPerHelix = max(50, strands.size * 4)
    val firstHelix = mutableListOf<Offset>()
    val secondHelix = mutableListOf<Offset>()
    
    for (i in 0 until pointsPerHelix) {
        val t = i.toFloat() / pointsPerHelix
        val angle = t * 4 * PI + rotation
        
        // 첫 번째 나선
        val radius1 = radius * (0.8f + 0.2f * sin(t * 6 * PI).toFloat())
        val x1 = center.x + cos(angle.toFloat()) * radius1
        val y1 = center.y + sin(angle.toFloat()) * radius1 + sin(t * 8 * PI).toFloat() * 20
        firstHelix.add(Offset(x1, y1))
        
        // 두 번째 나선 (약간의 오프셋)
        val radius2 = radius * (0.7f + 0.15f * sin(t * 6 * PI + PI).toFloat())
        val x2 = center.x + cos(angle.toFloat() + PI.toFloat()) * radius2
        val y2 = center.y + sin(angle.toFloat() + PI.toFloat()) * radius2 + sin(t * 8 * PI + PI).toFloat() * 15
        secondHelix.add(Offset(x2, y2))
    }
    
    return listOf(firstHelix, secondHelix)
}

private fun DrawScope.drawHelixPath(
    points: List<Offset>,
    color: Color,
    strokeWidth: Float
) {
    if (points.size < 2) return
    
    val path = Path()
    path.moveTo(points[0].x, points[0].y)
    
    for (i in 1 until points.size) {
        path.lineTo(points[i].x, points[i].y)
    }
    
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

private fun DrawScope.drawFlavorNode(
    center: Offset,
    strand: DNAStrand,
    evolutionStage: EvolutionStage
) {
    val nodeSize = 8f + (strand.evolutionLevel * 2f)
    val nodeColor = getFlavorColor(strand.primaryFlavor)
    val intensity = strand.intensity / 5f
    
    // 노드 외부 원
    drawCircle(
        color = nodeColor.copy(alpha = 0.3f),
        radius = nodeSize * 1.5f,
        center = center
    )
    
    // 노드 메인 원
    drawCircle(
        color = nodeColor,
        radius = nodeSize,
        center = center,
        style = Stroke(width = 2f)
    )
    
    // 강도 표시 (내부 원)
    drawCircle(
        color = nodeColor.copy(alpha = intensity),
        radius = nodeSize * 0.6f,
        center = center
    )
    
    // 진화 레벨 표시 (작은 점들)
    for (i in 1..strand.evolutionLevel) {
        val dotAngle = (i * 2 * PI / strand.evolutionLevel).toFloat()
        val dotRadius = nodeSize + 4f
        val dotX = center.x + cos(dotAngle) * dotRadius
        val dotY = center.y + sin(dotAngle) * dotRadius
        
        drawCircle(
            color = nodeColor,
            radius = 2f,
            center = Offset(dotX, dotY)
        )
    }
}

private fun getEvolutionStageColor(stage: EvolutionStage): Color {
    return when (stage) {
        EvolutionStage.EGG -> Color(0xFFFFE0B2) // 연한 주황
        EvolutionStage.LARVA -> Color(0xFFC8E6C9) // 연한 그린
        EvolutionStage.PUPA -> Color(0xFFBBDEFB) // 연한 블루
        EvolutionStage.BUTTERFLY -> Color(0xFFE1BEE7) // 연한 퍼플
    }
}

private fun getFlavorColor(flavor: Flavor): Color {
    return when (flavor) {
        Flavor.MALT -> Color(0xFF8D6E63) // 갈색
        Flavor.FRUIT -> Color(0xFFE91E63) // 핑크
        Flavor.DRIED -> Color(0xFF795548) // 다크 브라운
        Flavor.FLORAL -> Color(0xFFE91E63) // 핑크
        Flavor.CITRUS -> Color(0xFFFF9800) // 오렌지
        Flavor.SPICE -> Color(0xFFD32F2F) // 레드
        Flavor.WOOD -> Color(0xFF5D4037) // 다크 브라운
        Flavor.PEAT -> Color(0xFF424242) // 그레이
        Flavor.NUTS -> Color(0xFF8D6E63) // 브라운
        Flavor.TOFFEE -> Color(0xFF8D6E63) // 브라운
        Flavor.VANILLA -> Color(0xFFF5F5DC) // 베이지
        Flavor.HONEY -> Color(0xFFFFC107) // 골드
        Flavor.HERB -> Color(0xFF4CAF50) // 그린
        Flavor.CHAR -> Color(0xFF212121) // 블랙
    }
}
