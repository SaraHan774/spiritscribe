package com.august.spiritscribe.ui.evolution.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.august.spiritscribe.domain.model.*

@Composable
fun EvolutionAnalysisCard(
    analysis: EvolutionAnalysis,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "진화 분석",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                // 진화 트렌드 아이콘
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = "진화 트렌드",
                    tint = getTrendColor(analysis.evolutionTrend)
                )
            }
            
            // 테이스트 성격
            PersonalityCard(personality = analysis.personalityType)
            
            // 진행률 바
            ProgressCard(
                currentStage = analysis.currentStage,
                progress = analysis.progressToNextStage
            )
            
            // 지배적 플레이버
            if (analysis.dominantFlavors.isNotEmpty()) {
                FlavorSection(
                    title = "지배적 플레이버",
                    flavors = analysis.dominantFlavors,
                    color = Color(0xFFFF6B6B)
                )
            }
            
            // 신흥 플레이버
            if (analysis.emergingFlavors.isNotEmpty()) {
                FlavorSection(
                    title = "신흥 플레이버",
                    flavors = analysis.emergingFlavors,
                    color = Color(0xFF4ECDC4)
                )
            }
            
            // 다음 마일스톤
            if (analysis.nextMilestones.isNotEmpty()) {
                NextMilestonesSection(milestones = analysis.nextMilestones)
            }
        }
    }
}

@Composable
private fun PersonalityCard(
    personality: TastePersonality,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getPersonalityColor(personality).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = "성격 유형",
                tint = getPersonalityColor(personality),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = personality.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = personality.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProgressCard(
    currentStage: EvolutionStage,
    progress: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "다음 진화까지",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            LinearProgressIndicator(
                progress = progress.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline
            )
            
            currentStage.nextStage?.let { nextStage ->
                Text(
                    text = "다음 단계: ${nextStage.displayName} ${nextStage.emoji}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FlavorSection(
    title: String,
    flavors: List<Flavor>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(flavors) { flavor ->
                FlavorChip(
                    flavor = flavor,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun FlavorChip(
    flavor: Flavor,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = flavor.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun NextMilestonesSection(
    milestones: List<EvolutionPoint>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "다음 목표",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        milestones.forEach { milestone ->
            MilestoneItem(milestone = milestone)
        }
    }
}

@Composable
private fun MilestoneItem(
    milestone: EvolutionPoint,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 마일스톤 타입별 아이콘
            val iconColor = getMilestoneTypeColor(milestone.type)
            val iconContent = getMilestoneTypeIcon(milestone.type)
            
            iconContent?.let { icon ->
                Icon(
                    icon,
                    contentDescription = milestone.type.name,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = milestone.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// 헬퍼 함수들
private fun getTrendColor(trend: EvolutionTrend): Color {
    return when (trend) {
        EvolutionTrend.RAPID_GROWTH -> Color(0xFF4CAF50) // 그린
        EvolutionTrend.STEADY_PROGRESS -> Color(0xFF2196F3) // 블루
        EvolutionTrend.EXPLORATION_PHASE -> Color(0xFF9C27B0) // 퍼플
        EvolutionTrend.MASTERY_PHASE -> Color(0xFFFF9800) // 오렌지
        EvolutionTrend.STAGNATION -> Color(0xFFF44336) // 레드
    }
}

private fun getPersonalityColor(personality: TastePersonality): Color {
    return when (personality) {
        TastePersonality.CLASSIC_CONNOISSEUR -> Color(0xFF8D6E63) // 브라운
        TastePersonality.ADVENTUROUS_EXPLORER -> Color(0xFF9C27B0) // 퍼플
        TastePersonality.SWEET_DREAMER -> Color(0xFFFF9800) // 오렌지
        TastePersonality.SMOKY_WARRIOR -> Color(0xFF424242) // 그레이
        TastePersonality.BALANCED_MASTER -> Color(0xFF4CAF50) // 그린
        TastePersonality.MINIMALIST_PURIST -> Color(0xFF2196F3) // 블루
    }
}

private fun getMilestoneTypeColor(type: EvolutionPointType): Color {
    return when (type) {
        EvolutionPointType.FIRST_NOTE -> Color(0xFF4CAF50)
        EvolutionPointType.FLAVOR_DISCOVERY -> Color(0xFF2196F3)
        EvolutionPointType.STAGE_EVOLUTION -> Color(0xFF9C27B0)
        EvolutionPointType.MILESTONE_NOTES -> Color(0xFFFF9800)
        EvolutionPointType.FLAVOR_MASTERY -> Color(0xFFF44336)
        EvolutionPointType.COMBINATION_BREAKTHROUGH -> Color(0xFFE91E63)
    }
}

private fun getMilestoneTypeIcon(type: EvolutionPointType): androidx.compose.ui.graphics.vector.ImageVector? {
    return when (type) {
        EvolutionPointType.FIRST_NOTE -> Icons.Default.Star
        EvolutionPointType.FLAVOR_DISCOVERY -> Icons.Default.Star
        EvolutionPointType.STAGE_EVOLUTION -> Icons.Default.Star
        EvolutionPointType.MILESTONE_NOTES -> Icons.Default.Star
        EvolutionPointType.FLAVOR_MASTERY -> Icons.Default.Star
        EvolutionPointType.COMBINATION_BREAKTHROUGH -> Icons.Default.Star
    }
}
