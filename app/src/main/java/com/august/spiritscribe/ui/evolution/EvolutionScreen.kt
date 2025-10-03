package com.august.spiritscribe.ui.evolution

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.august.spiritscribe.domain.model.EvolutionStage
import com.august.spiritscribe.ui.evolution.components.DNAVisualization
import com.august.spiritscribe.ui.evolution.components.EvolutionAnalysisCard
import kotlin.math.PI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvolutionScreen(
    modifier: Modifier = Modifier,
    viewModel: EvolutionViewModel = hiltViewModel()
) {
    val evolutionState by viewModel.evolutionState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "테이스트 진화",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshEvolutionData() }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "새로고침"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 배경 그라데이션
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF8F9FA),
                                Color(0xFFE9ECEF),
                                Color(0xFFDEE2E6)
                            )
                        )
                    )
            )
            
            when {
                isLoading -> {
                    LoadingContent()
                }
                
                error != null -> {
                    ErrorContent(error = error!!)
                }
                
                evolutionState.evolution == null || evolutionState.evolution.totalNotes == 0 -> {
                    EmptyContent()
                }
                
                else -> {
                    EvolutionContent(
                        evolution = evolutionState.evolution!!,
                        analysis = evolutionState.analysis!!
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "테이스트 DNA를 분석 중입니다...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "잠시만 기다려주세요",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorContent(error: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "분석 오류",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "🥚",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "테이스트 여정의 시작",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "아직 위스키 노트가 없습니다",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "첫 번째 위스키를 기록하면\n나만의 테이스트 DNA가 생성됩니다",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EvolutionContent(
    evolution: com.august.spiritscribe.domain.model.TasteEvolution,
    analysis: com.august.spiritscribe.domain.model.EvolutionAnalysis
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // DNA 시각화 섹션
        DNAVisualizationSection(
            evolution = evolution,
            modifier = Modifier.height(400.dp)
        )
        
        // 진화 분석 카드
        EvolutionAnalysisCard(
            analysis = analysis,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun DNAVisualizationSection(
    evolution: com.august.spiritscribe.domain.model.TasteEvolution,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
                    text = "테이스트 DNA",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                EvolutionStageBadge(stage = evolution.evolutionStage)
            }
            
            // DNA 시각화
            DNAVisualization(
                dnaStrands = evolution.dnaStrands,
                evolutionStage = evolution.evolutionStage,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 통계 정보
            EvolutionStats(
                totalNotes = evolution.totalNotes,
                uniqueFlavors = evolution.uniqueFlavors,
                evolutionScore = evolution.evolutionScore
            )
        }
    }
}

@Composable
private fun EvolutionStageBadge(
    stage: EvolutionStage,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = getStageColor(stage).copy(alpha = 0.2f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stage.emoji,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stage.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = getStageColor(stage)
            )
        }
    }
}

@Composable
private fun EvolutionStats(
    totalNotes: Int,
    uniqueFlavors: Int,
    evolutionScore: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            title = "노트",
            value = totalNotes.toString(),
            icon = "📝"
        )
        StatItem(
            title = "플레이버",
            value = uniqueFlavors.toString(),
            icon = "🧬"
        )
        StatItem(
            title = "진화점수",
            value = "${evolutionScore.toInt()}",
            icon = "⭐"
        )
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getStageColor(stage: EvolutionStage): Color {
    return when (stage) {
        EvolutionStage.EGG -> Color(0xFFFF9800) // 오렌지
        EvolutionStage.LARVA -> Color(0xFF4CAF50) // 그린
        EvolutionStage.PUPA -> Color(0xFF2196F3) // 블루
        EvolutionStage.BUTTERFLY -> Color(0xFF9C27B0) // 퍼플
    }
}
