package com.august.spiritscribe.feature.lab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.august.spiritscribe.feature.lab.domain.LabFeature

/**
 * 실험실 기능을 표시하는 카드 컴포넌트
 */
@Composable
fun LabFeatureCard(
    feature: LabFeature,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = feature.isAvailable,
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (feature.isAvailable) {
                Color(0xFF1E1E2E)
            } else {
                Color(0xFF2A2A2A)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 기능 아이콘
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (feature.isAvailable) {
                            Color(0xFF4FC3F7).copy(alpha = 0.2f)
                        } else {
                            Color(0xFF666666).copy(alpha = 0.2f)
                        },
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = feature.emoji,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // 기능 정보
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = feature.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (feature.isAvailable) Color.White else Color(0xFF666666)
                    )
                    
                    if (!feature.isAvailable) {
                        Surface(
                            color = Color(0xFF666666),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "준비중",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (feature.isAvailable) Color(0xFFB0BEC5) else Color(0xFF666666)
                )
            }

            // 화살표 아이콘
            if (feature.isAvailable) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "이동",
                    tint = Color(0xFF4FC3F7),
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "잠금",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
