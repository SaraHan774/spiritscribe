package com.august.spiritscribe.feature.lab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.august.spiritscribe.feature.lab.domain.MixingRatio

/**
 * 비율 선택기 컴포넌트
 */
@Composable
fun RatioSelector(
    selectedRatio: MixingRatio,
    onRatioSelected: (MixingRatio) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "믹싱 비율 선택",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MixingRatio.values().forEach { ratio ->
                    RatioButton(
                        ratio = ratio,
                        isSelected = ratio == selectedRatio,
                        onClick = { onRatioSelected(ratio) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RatioButton(
    ratio: MixingRatio,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                Color(0xFF4CAF50) // 초록색 (선택됨)
            } else {
                Color.White.copy(alpha = 0.2f) // 반투명 (선택 안됨)
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ratio.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = ratio.description,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.6f)
            )
        }
    }
}
