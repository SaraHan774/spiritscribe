package com.august.spiritscribe.ui.flavor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.august.spiritscribe.domain.model.FlavorProfile
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlavorWheelScreen(
    modifier: Modifier = Modifier,
    flavorProfile: FlavorProfile
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Flavor Wheel",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        
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
                flavorProfile = flavorProfile
            )
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
    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = minOf(size.width, size.height) / 2f
        
        // 외부 원 그리기
        drawCircle(
            color = Color.Gray,
            radius = radius,
            center = center,
            style = Stroke(width = 2f)
        )
        
        // 카테고리 섹션 그리기
        val categories = FlavorProfile.AROMA_CATEGORIES + 
                        FlavorProfile.PALATE_CATEGORIES
        val sectionAngle = 2 * PI / categories.size
        
        categories.forEachIndexed { index, (category, _) ->
            val startAngle = index * sectionAngle
            val color = when {
                category == selectedCategory -> Color(0xFFE57373) // 선택된 카테고리
                index < FlavorProfile.AROMA_CATEGORIES.size -> Color(0xFFFFB74D) // 향
                else -> Color(0xFF81C784) // 맛
            }
            
            // 섹션 그리기
            drawArc(
                color = color,
                startAngle = (startAngle * 180 / PI).toFloat(),
                sweepAngle = (sectionAngle * 180 / PI).toFloat(),
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            
            // 카테고리 이름 표시 위치 계산
            val textAngle = startAngle + sectionAngle / 2
            val textRadius = radius * 0.7f
            val textX = center.x + cos(textAngle).toFloat() * textRadius
            val textY = center.y + sin(textAngle).toFloat() * textRadius
            
            // 여기서는 텍스트를 그리지 않습니다 - Canvas에서 텍스트 그리기는 복잡하므로
            // 실제 구현시에는 Box와 Text 컴포저블을 사용하는 것이 좋습니다
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
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // 카테고리에 해당하는 향/맛 표시
            val flavors = when {
                FlavorProfile.AROMA_CATEGORIES.any { it.first == category } ->
                    flavorProfile.aroma.filter { flavor ->
                        FlavorProfile.AROMA_CATEGORIES
                            .find { it.first == category }?.second
                            ?.contains(flavor) == true
                    }
                FlavorProfile.PALATE_CATEGORIES.any { it.first == category } ->
                    flavorProfile.palate.filter { flavor ->
                        FlavorProfile.PALATE_CATEGORIES
                            .find { it.first == category }?.second
                            ?.contains(flavor) == true
                    }
                else -> emptyList()
            }
            
            flavors.forEach { flavor ->
                Text(
                    text = "• $flavor",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
} 