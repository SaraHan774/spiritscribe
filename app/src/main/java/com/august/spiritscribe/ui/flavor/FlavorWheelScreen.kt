package com.august.spiritscribe.ui.flavor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.august.spiritscribe.domain.model.FlavorProfile
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlavorWheelScreen(
    modifier: Modifier = Modifier,
    viewModel: FlavorWheelViewModel = hiltViewModel()
) {
    val flavorProfile by viewModel.flavorProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "플레이버 휠",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshFlavorData() }
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 에러 상태 표시
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            when {
                isLoading -> {
                    // 로딩 상태
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "플레이버 데이터를 분석 중입니다...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                flavorProfile.aroma.isEmpty() && flavorProfile.palate.isEmpty() && flavorProfile.finish.isEmpty() -> {
                    // 데이터가 없는 상태
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "🍃",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Text(
                                text = "아직 플레이버 데이터가 없습니다",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "위스키 노트를 추가하면 플레이버 휠에 데이터가 표시됩니다",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                else -> {
                    // 데이터가 있는 상태 - 플레이버 휠 표시
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(16.dp)
                    ) {
                        FlavorWheel(
                            flavorProfile = flavorProfile,
                            selectedCategory = selectedCategory,
                            onCategorySelected = { selectedCategory = it }
                        )
                    }
                    
                    // 선택된 카테고리 정보 표시
                    selectedCategory?.let { category ->
                        FlavorCategoryDetails(
                            category = category,
                            flavorProfile = flavorProfile,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlavorWheel(
    flavorProfile: FlavorProfile,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = minOf(size.width, size.height) / 2f - 50f
            
            // 실제 데이터 기반 카테고리 생성
            val categories = buildList {
                if (flavorProfile.aroma.isNotEmpty()) {
                    add("향 (Aroma)" to flavorProfile.aroma.size)
                }
                if (flavorProfile.palate.isNotEmpty()) {
                    add("맛 (Palate)" to flavorProfile.palate.size)
                }
                if (flavorProfile.finish.isNotEmpty()) {
                    add("피니시 (Finish)" to flavorProfile.finish.size)
                }
            }
            
            if (categories.isNotEmpty()) {
                val sectionAngle = 2 * PI / categories.size
                
                categories.forEachIndexed { index, (category, flavorCount) ->
                    val startAngle = index * sectionAngle
                    val color = when {
                        category == selectedCategory -> Color(0xFFE57373)
                        category.contains("향") -> Color(0xFFFFB74D)
                        category.contains("맛") -> Color(0xFF81C784)
                        else -> Color(0xFF64B5F6)
                    }
                    
                    val intensityRadius = radius * (0.3f + (flavorCount.toFloat() / 20f).coerceIn(0.1f, 0.7f))
                    
                    drawArc(
                        color = color.copy(alpha = 0.6f),
                        startAngle = (startAngle * 180 / PI).toFloat(),
                        sweepAngle = (sectionAngle * 180 / PI).toFloat(),
                        useCenter = false,
                        topLeft = Offset(center.x - intensityRadius, center.y - intensityRadius),
                        size = Size(intensityRadius * 2, intensityRadius * 2),
                        style = Stroke(width = 8f)
                    )
                }
                
                drawCircle(
                    color = Color.White,
                    radius = radius * 0.2f,
                    center = center
                )
                
                drawCircle(
                    color = Color.Gray,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2f)
                )
            }
        }
        
        // 카테고리 라벨과 중앙 정보는 별도로 처리
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "플레이버 휠",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "총 ${flavorProfile.aroma.size + flavorProfile.palate.size + flavorProfile.finish.size}개",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FlavorCategoryDetails(
    category: String,
    flavorProfile: FlavorProfile,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // 카테고리에 해당하는 플레이버 표시
            val flavors = when {
                category.contains("향") -> flavorProfile.aroma
                category.contains("맛") -> flavorProfile.palate
                category.contains("피니시") -> flavorProfile.finish
                else -> emptyList()
            }
            
            if (flavors.isNotEmpty()) {
                flavors.forEach { flavor ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = flavor,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                Text(
                    text = "이 카테고리에 해당하는 플레이버가 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
} 