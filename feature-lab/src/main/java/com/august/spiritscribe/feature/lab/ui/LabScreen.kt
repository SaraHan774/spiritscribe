package com.august.spiritscribe.feature.lab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.august.spiritscribe.feature.lab.ui.components.SwirlArtCard
import com.august.spiritscribe.feature.lab.ui.components.LabFeatureCard
import com.august.spiritscribe.feature.lab.domain.LabFeature
import com.august.spiritscribe.feature.lab.domain.LabState

/**
 * 실험실 메인 화면
 * 다양한 실험적 기능들을 제공하는 허브 역할
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabScreen(
    modifier: Modifier = Modifier,
    viewModel: LabViewModel = hiltViewModel()
) {
    val labState by viewModel.labState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

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
                            Color(0xFF1A1A2E),
                            Color(0xFF16213E),
                            Color(0xFF0F3460)
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

            else -> {
                LabContent(
                    labState = labState,
                    onFeatureClick = { feature ->
                        viewModel.onFeatureClick(feature)
                    }
                )
            }
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
                color = Color(0xFF4FC3F7)
            )
            Text(
                text = "실험실을 준비 중입니다...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
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
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "오류",
                tint = Color(0xFFE57373),
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "실험실에 문제가 발생했습니다",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFB0BEC5)
            )
        }
    }
}

@Composable
private fun LabContent(
    labState: LabState,
    onFeatureClick: (LabFeature) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(40.dp))

        // 실험실 헤더
        LabHeader()

        Spacer(Modifier.height(24.dp))

        // 스월 아트 카드 (메인 기능)
        SwirlArtCard(
            onStartSwirlArt = { onFeatureClick(LabFeature.SWIRL_ART) },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 실험실 기능 목록
        LabFeaturesList(
            features = labState.availableFeatures,
            onFeatureClick = onFeatureClick
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun LabHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "🧪",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "실험실",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "위스키와 함께하는 혁신적인 경험",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFB0BEC5)
        )
    }
}

@Composable
private fun LabFeaturesList(
    features: List<LabFeature>,
    onFeatureClick: (LabFeature) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        features.forEach { feature ->
            LabFeatureCard(
                feature = feature,
                onClick = { onFeatureClick(feature) }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LabScreenPreview() {
    MaterialTheme {
        LabContent(
            labState = LabState(
                availableFeatures = LabFeature.values().toList()
            ),
            onFeatureClick = { }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LabLoadingPreview() {
    MaterialTheme {
        LoadingContent()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LabErrorPreview() {
    MaterialTheme {
        ErrorContent(error = "실험실을 초기화할 수 없습니다.")
    }
}
