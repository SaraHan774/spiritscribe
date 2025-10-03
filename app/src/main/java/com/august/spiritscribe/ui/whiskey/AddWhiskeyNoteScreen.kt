package com.august.spiritscribe.ui.whiskey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.august.spiritscribe.domain.model.Flavor
import com.august.spiritscribe.ui.components.CreativeRatingChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWhiskeyNoteScreen(
    whiskeyId: String,
    onNavigateBack: () -> Unit,
    viewModel: AddWhiskeyNoteViewModel = hiltViewModel()
) {
    // 위스키 정보 로드
    LaunchedEffect(whiskeyId) {
        viewModel.loadWhiskey(whiskeyId)
    }

    val whiskey by viewModel.whiskey.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val noteText by viewModel.noteText.collectAsState()
    val rating by viewModel.rating.collectAsState()
    val selectedFlavors by viewModel.selectedFlavors.collectAsState()

    AddWhiskeyNoteScreenContent(
        whiskey = whiskey,
        isSaving = isSaving,
        noteText = noteText,
        rating = rating,
        selectedFlavors = selectedFlavors,
        onNavigateBack = onNavigateBack,
        onUpdateNoteText = viewModel::updateNoteText,
        onUpdateRating = viewModel::updateRating,
        onToggleFlavor = viewModel::toggleFlavor,
        onUpdateFlavorIntensity = viewModel::updateFlavorIntensity,
        onSaveNote = { viewModel.saveNote { onNavigateBack() } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddWhiskeyNoteScreenContent(
    whiskey: com.august.spiritscribe.domain.model.Whiskey?,
    isSaving: Boolean,
    noteText: String,
    rating: Int,
    selectedFlavors: Map<Flavor, Int>,
    onNavigateBack: () -> Unit,
    onUpdateNoteText: (String) -> Unit,
    onUpdateRating: (Int) -> Unit,
    onToggleFlavor: (Flavor) -> Unit,
    onUpdateFlavorIntensity: (Flavor, Int) -> Unit,
    onSaveNote: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 그라데이션
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                        )
                    )
                )
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Note",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "뒤로가기"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = onSaveNote,
                            enabled = !isSaving && noteText.isNotBlank()
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "저장"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 위스키 정보 카드 - 개선된 디자인
                whiskey?.let { whiskey ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                                        )
                                    )
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = whiskey.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 24.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(
                                            alpha = 0.5f
                                        )
                                    ) {
                                        Text(
                                            text = whiskey.distillery,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            ),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    whiskey.age?.let { age ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                alpha = 0.5f
                                            )
                                        ) {
                                            Text(
                                                text = "${age}년",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,
                                                    vertical = 4.dp
                                                ),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(
                                            alpha = 0.5f
                                        )
                                    ) {
                                        Text(
                                            text = "${whiskey.abv}% ABV",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            ),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 평점 입력 - 개선된 디자인
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        CreativeRatingChip(
                            rating = rating,
                            showPercentage = true
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { index ->
                                val isSelected = index < rating
                                IconButton(
                                    onClick = { onUpdateRating(index + 1) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) {
                                            Icons.Default.Star
                                        } else {
                                            Icons.Default.StarBorder
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                        },
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 플레이버 선택 - 인터랙티브 디자인
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌿 느낀 플레이버",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (selectedFlavors.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = "${selectedFlavors.size}개 선택",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        ),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Text(
                            text = "위스키에서 느낀 플레이버를 선택하고 강도를 조절해보세요.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // 플레이버 선택 그리드
                        FlavorSelectionGrid(
                            selectedFlavors = selectedFlavors,
                            onFlavorToggle = onToggleFlavor,
                            onIntensityChange = onUpdateFlavorIntensity,
                            isFlavorSelected = { flavor -> selectedFlavors.containsKey(flavor) },
                            getFlavorIntensity = { flavor -> selectedFlavors[flavor] ?: 3 }
                        )
                    }
                }

                // 노트 입력 - 개선된 디자인
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "📝 테이스팅 노트",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = noteText,
                            onValueChange = onUpdateNoteText,
                            placeholder = {
                                Text(
                                    "위스키에 대한 느낌, 맛, 향 등을 자유롭게 적어보세요...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            maxLines = 8,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlavorSelectionGrid(
    selectedFlavors: Map<Flavor, Int>,
    onFlavorToggle: (Flavor) -> Unit,
    onIntensityChange: (Flavor, Int) -> Unit,
    isFlavorSelected: (Flavor) -> Boolean,
    getFlavorIntensity: (Flavor) -> Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Flavor.values().forEach { flavor ->
            val isSelected = isFlavorSelected(flavor)
            val intensity = if (isSelected) getFlavorIntensity(flavor) else 3

            // 상태 변화 로그
            LaunchedEffect(isSelected, intensity) {
                android.util.Log.d(
                    "FlavorSelectionGrid",
                    "📋 FlavorSelectionGrid: ${flavor.name}, isSelected=$isSelected, intensity=$intensity"
                )
            }

            FlavorChip(
                flavor = flavor,
                isSelected = isSelected,
                intensity = intensity,
                onToggle = {
                    android.util.Log.d("FlavorSelectionGrid", "👆 FlavorChip 터치됨: ${flavor.name}")
                    onFlavorToggle(flavor)
                },
                onIntensityChange = { intensity ->
                    android.util.Log.d(
                        "FlavorSelectionGrid",
                        "🎯 강도 변경 요청: ${flavor.name} -> $intensity"
                    )
                    onIntensityChange(flavor, intensity)
                }
            )
        }
    }
}

@Composable
private fun FlavorChip(
    flavor: Flavor,
    isSelected: Boolean,
    intensity: Int,
    onToggle: () -> Unit,
    onIntensityChange: (Int) -> Unit
) {
    // 상태 변화를 로그로 추적
    LaunchedEffect(isSelected, intensity) {
        android.util.Log.d(
            "FlavorChip",
            "🎨 FlavorChip 리컴포지션: ${flavor.name}, isSelected=$isSelected, intensity=$intensity"
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            onClick = onToggle,
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            },
            border = if (isSelected) {
                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            } else {
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            },
            modifier = Modifier
                .size(85.dp)
                .padding(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = flavor.emoji,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = flavor.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 12.sp
                )
            }
        }

        // 강도 조절 (선택된 경우에만)
        if (isSelected) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "강도",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        val isIntensitySelected = index < intensity
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = if (isIntensitySelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    },
                                    shape = CircleShape
                                )
                                .clickable { onIntensityChange(index + 1) }
                        ) {
                            if (isIntensitySelected) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(
                                            color = Color.White,
                                            shape = CircleShape
                                        )
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Mock 데이터 생성
private val mockWhiskey = com.august.spiritscribe.domain.model.Whiskey(
    id = "1",
    name = "Macallan 18년",
    distillery = "Macallan",
    type = com.august.spiritscribe.domain.model.WhiskeyType.SINGLEMALT,
    region = "스코틀랜드",
    abv = 43.0,
    age = 18,
    year = 2020,
    price = 150000.0,
    description = "매끄럽고 풍부한 맛의 싱글 몰트 위스키",
    rating = 92,
    imageUris = emptyList()
)

// Preview 함수들
@Preview(name = "노트 추가 화면 - 기본", showBackground = true)
@Composable
private fun AddWhiskeyNoteScreenPreview() {
    MaterialTheme {
        AddWhiskeyNoteScreenContent(
            whiskey = mockWhiskey,
            isSaving = false,
            noteText = "",
            rating = 0,
            selectedFlavors = emptyMap(),
            onNavigateBack = {},
            onUpdateNoteText = {},
            onUpdateRating = {},
            onToggleFlavor = {},
            onUpdateFlavorIntensity = { _, _ -> },
            onSaveNote = {}
        )
    }
}

@Preview(name = "노트 추가 화면 - 입력된 상태", showBackground = true)
@Composable
private fun AddWhiskeyNoteScreenWithDataPreview() {
    MaterialTheme {
        AddWhiskeyNoteScreenContent(
            whiskey = mockWhiskey,
            isSaving = false,
            noteText = "매끄럽고 달콤한 바닐라와 꿀의 향이 느껴집니다. 오크의 풍미가 조화롭게 어우러져 있습니다.",
            rating = 4,
            selectedFlavors = mapOf(
                Flavor.VANILLA to 4,
                Flavor.HONEY to 3,
                Flavor.WOOD to 2
            ),
            onNavigateBack = {},
            onUpdateNoteText = {},
            onUpdateRating = {},
            onToggleFlavor = {},
            onUpdateFlavorIntensity = { _, _ -> },
            onSaveNote = {}
        )
    }
}

@Preview(name = "노트 추가 화면 - 저장 중", showBackground = true)
@Composable
private fun AddWhiskeyNoteScreenSavingPreview() {
    MaterialTheme {
        AddWhiskeyNoteScreenContent(
            whiskey = mockWhiskey,
            isSaving = true,
            noteText = "매끄럽고 달콤한 바닐라와 꿀의 향이 느껴집니다.",
            rating = 5,
            selectedFlavors = mapOf(
                Flavor.VANILLA to 5,
                Flavor.HONEY to 4,
                Flavor.WOOD to 3,
                Flavor.SPICE to 2
            ),
            onNavigateBack = {},
            onUpdateNoteText = {},
            onUpdateRating = {},
            onToggleFlavor = {},
            onUpdateFlavorIntensity = { _, _ -> },
            onSaveNote = {}
        )
    }
}

@Preview(name = "노트 추가 화면 - 위스키 정보 없음", showBackground = true)
@Composable
private fun AddWhiskeyNoteScreenNoWhiskeyPreview() {
    MaterialTheme {
        AddWhiskeyNoteScreenContent(
            whiskey = null,
            isSaving = false,
            noteText = "",
            rating = 0,
            selectedFlavors = emptyMap(),
            onNavigateBack = {},
            onUpdateNoteText = {},
            onUpdateRating = {},
            onToggleFlavor = {},
            onUpdateFlavorIntensity = { _, _ -> },
            onSaveNote = {}
        )
    }
}
