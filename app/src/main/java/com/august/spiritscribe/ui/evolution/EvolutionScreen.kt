package com.august.spiritscribe.ui.evolution

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.august.spiritscribe.domain.model.*
import com.august.spiritscribe.ui.evolution.components.DNAVisualization
import com.august.spiritscribe.ui.evolution.components.EvolutionAnalysisCard
import com.august.spiritscribe.ui.evolution.components.EvolutionGuideDialog
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
    var showGuideDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize()
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

            evolutionState.evolution == null || (evolutionState.evolution?.totalNotes
                ?: 0) == 0 -> {
                EmptyContent()
            }

            else -> {
                EvolutionContent(
                    evolution = evolutionState.evolution!!,
                    analysis = evolutionState.analysis!!,
                    showGuideDialog = showGuideDialog,
                    onShowGuideDialog = { showGuideDialog = it },
                    onRefreshData = { viewModel.refreshEvolutionData() }
                )
            }
        }


        // 가이드 다이얼로그
        if (showGuideDialog) {
            EvolutionGuideDialog(
                onDismiss = { showGuideDialog = false }
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
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
private fun ErrorContent(
    error: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
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
private fun EmptyContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
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
    evolution: TasteEvolution,
    analysis: EvolutionAnalysis,
    showGuideDialog: Boolean,
    onShowGuideDialog: (Boolean) -> Unit,
    onRefreshData: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(40.dp))

        // DNA 시각화 섹션
        DNAVisualizationSection(
            evolution = evolution,
            onShowGuideDialog = onShowGuideDialog,
            onRefreshData = onRefreshData,
            modifier = Modifier.height(400.dp)
        )

        Spacer(Modifier.height(20.dp))

        // 진화 분석 카드
        EvolutionAnalysisCard(
            analysis = analysis,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun DNAVisualizationSection(
    evolution: TasteEvolution,
    onShowGuideDialog: (Boolean) -> Unit,
    onRefreshData: () -> Unit,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "테이스트 DNA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    // Info 아이콘
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = "테이스트 진화 가이드",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier
                            .clickable(onClick = { onShowGuideDialog(true) })
                            .padding(start = 4.dp)
                            .size(20.dp)
                    )
                }//
                EvolutionStageBadge(stage = evolution.evolutionStage)
            }

            // DNA 시각화
            DNAVisualization(
                dnaStrands = evolution.dnaStrands,
                evolutionStage = evolution.evolutionStage,
                modifier = Modifier
                    .clickable(onClick = { onRefreshData() })
                    .fillMaxWidth()
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

// Preview용 Mock 데이터
private fun createMockEvolution(): TasteEvolution {
    return TasteEvolution(
        userId = "preview_user",
        evolutionStage = EvolutionStage.PUPA,
        dnaStrands = listOf(
            DNAStrand(
                primaryFlavor = Flavor.VANILLA,
                secondaryFlavor = Flavor.TOFFEE,
                intensity = 4,
                frequency = 15,
                firstDiscoveredDate = System.currentTimeMillis() - 86400000 * 30,
                lastUsedDate = System.currentTimeMillis() - 86400000 * 2,
                evolutionLevel = 4
            ),
            DNAStrand(
                primaryFlavor = Flavor.PEAT,
                secondaryFlavor = null,
                intensity = 3,
                frequency = 12,
                firstDiscoveredDate = System.currentTimeMillis() - 86400000 * 25,
                lastUsedDate = System.currentTimeMillis() - 86400000 * 5,
                evolutionLevel = 3
            ),
            DNAStrand(
                primaryFlavor = Flavor.FRUIT,
                secondaryFlavor = Flavor.CITRUS,
                intensity = 2,
                frequency = 8,
                firstDiscoveredDate = System.currentTimeMillis() - 86400000 * 20,
                lastUsedDate = System.currentTimeMillis() - 86400000 * 7,
                evolutionLevel = 2
            )
        ),
        evolutionPoints = listOf(
            EvolutionPoint(
                type = EvolutionPointType.FIRST_NOTE,
                title = "첫 번째 노트",
                description = "첫 번째 위스키 노트 작성",
                achievedDate = System.currentTimeMillis() - 86400000 * 30
            ),
            EvolutionPoint(
                type = EvolutionPointType.FLAVOR_DISCOVERY,
                title = "새로운 플레이버 발견",
                description = "바닐라 플레이버 발견",
                achievedDate = System.currentTimeMillis() - 86400000 * 25,
                flavor = Flavor.VANILLA
            ),
            EvolutionPoint(
                type = EvolutionPointType.MILESTONE_NOTES,
                title = "10개 노트 달성",
                description = "10개 노트 달성",
                achievedDate = System.currentTimeMillis() - 86400000 * 15,
                milestone = 10
            )
        ),
        currentFlavorProfile = FlavorProfile(
            aroma = listOf("Vanilla", "Toffee", "Honey"),
            palate = listOf("Fruit", "Citrus", "Spice"),
            finish = listOf("Wood", "Peat", "Malt")
        ),
        totalNotes = 18,
        uniqueFlavors = 8,
        evolutionScore = 75.5,
        lastEvolutionDate = System.currentTimeMillis() - 86400000 * 2
    )
}

private fun createMockAnalysis(): EvolutionAnalysis {
    return EvolutionAnalysis(
        currentStage = EvolutionStage.PUPA,
        progressToNextStage = 0.75,
        dominantFlavors = listOf(Flavor.VANILLA, Flavor.TOFFEE),
        emergingFlavors = listOf(Flavor.SPICE, Flavor.NUTS),
        decliningFlavors = listOf(Flavor.WOOD),
        personalityType = TastePersonality.BALANCED_MASTER,
        evolutionTrend = EvolutionTrend.STEADY_PROGRESS,
        nextMilestones = listOf(
            EvolutionPoint(
                type = EvolutionPointType.MILESTONE_NOTES,
                title = "20개 노트 달성",
                description = "20개 노트 달성까지 2개 남음",
                achievedDate = System.currentTimeMillis(),
                milestone = 20
            )
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EvolutionScreenPreview() {
    MaterialTheme {
        EvolutionContent(
            evolution = createMockEvolution(),
            analysis = createMockAnalysis(),
            showGuideDialog = false,
            onShowGuideDialog = { },
            onRefreshData = { }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EvolutionLoadingPreview() {
    MaterialTheme {
        LoadingContent()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EvolutionEmptyPreview() {
    MaterialTheme {
        EmptyContent()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EvolutionErrorPreview() {
    MaterialTheme {
        ErrorContent(error = "데이터를 불러오는 중 오류가 발생했습니다.")
    }
}
