package com.august.spiritscribe.ui.evolution.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.august.spiritscribe.domain.model.EvolutionStage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvolutionGuideDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🧬 테이스트 진화 가이드",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            EvolutionGuideContent()
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("확인")
            }
        },
        modifier = modifier
    )
}

@Composable
private fun EvolutionGuideContent() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 소개 섹션
        GuideSection(
            title = "🎯 테이스트 진화란?",
            content = "위스키 노트를 기록할 때마다 선택하는 플레이버들이 모여 나만의 고유한 '테이스트 DNA'를 만들어갑니다. 이 DNA는 시간이 지나면서 진화하며, 여러분의 취향 발전 과정을 시각적으로 보여줍니다."
        )
        
        // 진화 단계 설명
        EvolutionStagesGuide()
        
        // DNA 시각화 설명
        GuideSection(
            title = "🧬 DNA 시각화",
            content = "중앙의 회전하는 이중 나선 구조가 바로 여러분의 테이스트 DNA입니다. 각 노드는 선택한 플레이버를 나타내며, 크기와 색상이 플레이버의 진화 정도를 보여줍니다."
        )
        
        // 성격 유형 설명
        GuideSection(
            title = "🎭 테이스트 페르소나",
            content = "여러분의 플레이버 선택 패턴을 분석하여 6가지 테이스트 성격 중 하나로 분류합니다. 클래식 감식가부터 모험적 탐험가까지, 나만의 고유한 테이스트 성격을 발견해보세요."
        )
        
        // 진화 포인트 설명
        GuideSection(
            title = "⭐ 진화 포인트",
            content = "특별한 순간들을 기록합니다. 첫 번째 노트, 새로운 플레이버 발견, 마일스톤 달성 등 여러분의 테이스트 여정에서 중요한 순간들이 진화 포인트로 저장됩니다."
        )
        
        // 팁 섹션
        GuideSection(
            title = "💡 진화 팁",
            content = "• 다양한 위스키를 시도해보세요\n• 새로운 플레이버를 적극 탐구하세요\n• 꾸준히 노트를 작성하세요\n• 자신만의 취향을 믿고 발전시키세요"
        )
        
        // 마무리
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = "🌟 \"Distill Your Taste\" - 기록으로 취향을 증류하여 나만의 완성된 테이스트 프로파일을 만들어가세요!",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EvolutionStagesGuide() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🦋 진화 단계",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "테이스트 여정은 4단계로 나뉩니다:",
            style = MaterialTheme.typography.bodyMedium
        )
        
        // 각 진화 단계 설명
        EvolutionStage.values().forEach { stage ->
            EvolutionStageItem(stage = stage)
        }
    }
}

@Composable
private fun EvolutionStageItem(stage: EvolutionStage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (stage) {
                EvolutionStage.EGG -> Color(0xFFFFE0B2).copy(alpha = 0.3f)
                EvolutionStage.LARVA -> Color(0xFFC8E6C9).copy(alpha = 0.3f)
                EvolutionStage.PUPA -> Color(0xFFBBDEFB).copy(alpha = 0.3f)
                EvolutionStage.BUTTERFLY -> Color(0xFFE1BEE7).copy(alpha = 0.3f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stage.emoji,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column {
                Text(
                    text = stage.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stage.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "필요 조건: ${stage.requiredNotes}개 이상의 노트",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun GuideSection(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
