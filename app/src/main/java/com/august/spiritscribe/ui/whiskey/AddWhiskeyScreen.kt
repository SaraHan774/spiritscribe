package com.august.spiritscribe.ui.whiskey

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.august.spiritscribe.domain.model.WhiskeyType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddWhiskeyScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddWhiskeyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Image picker
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(10)
    ) { uris ->
        uris.forEach { uri ->
            viewModel.onEvent(AddWhiskeyEvent.AddImage(uri))
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateBack()
        }
    }

    AddWhiskeyScreenContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onClickImageIcon = {
            multiplePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onSaveWhiskey = { viewModel.onEvent(AddWhiskeyEvent.SaveWhiskey) },
        onUpdateName = { viewModel.onEvent(AddWhiskeyEvent.UpdateName(it)) },
        onUpdateDistillery = { viewModel.onEvent(AddWhiskeyEvent.UpdateDistillery(it)) },
        onUpdateType = { viewModel.onEvent(AddWhiskeyEvent.UpdateType(it)) },
        onUpdateAge = { viewModel.onEvent(AddWhiskeyEvent.UpdateAge(it)) },
        onUpdateYear = { viewModel.onEvent(AddWhiskeyEvent.UpdateYear(it)) },
        onUpdateAbv = { viewModel.onEvent(AddWhiskeyEvent.UpdateAbv(it)) },
        onUpdatePrice = { viewModel.onEvent(AddWhiskeyEvent.UpdatePrice(it)) },
        onUpdateRegion = { viewModel.onEvent(AddWhiskeyEvent.UpdateRegion(it)) },
        onUpdateDescription = { viewModel.onEvent(AddWhiskeyEvent.UpdateDescription(it)) },
        onUpdateRating = { viewModel.onEvent(AddWhiskeyEvent.UpdateRating(it)) },
        onAddImage = { viewModel.onEvent(AddWhiskeyEvent.AddImage(it)) },
        onRemoveImage = { viewModel.onEvent(AddWhiskeyEvent.RemoveImage(it)) },
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun AddWhiskeyScreenContent(
    state: AddWhiskeyState,
    onNavigateBack: () -> Unit,
    onClickImageIcon: () -> Unit,
    onSaveWhiskey: () -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateDistillery: (String) -> Unit,
    onUpdateType: (WhiskeyType) -> Unit,
    onUpdateAge: (String) -> Unit,
    onUpdateYear: (String) -> Unit,
    onUpdateAbv: (String) -> Unit,
    onUpdatePrice: (String) -> Unit,
    onUpdateRegion: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onUpdateRating: (String) -> Unit,
    onAddImage: (Uri) -> Unit,
    onRemoveImage: (Uri) -> Unit,
    snackbarHostState: SnackbarHostState,
) {

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 그라데이션
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f)
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
                            "Add Whiskey",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Image Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        if (state.imageUris.isEmpty()) {
                            // Add Image Button
                            IconButton(
                                onClick = onClickImageIcon,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(64.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Add Photos",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            // Image Pager
                            val pagerState =
                                rememberPagerState(pageCount = { state.imageUris.size })
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = state.imageUris[page],
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    // Delete button
                                    IconButton(
                                        onClick = {
                                            onRemoveImage(state.imageUris[page])
                                        },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                                shape = CircleShape
                                            )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            // Page indicator
                            Row(
                                Modifier
                                    .height(50.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(pagerState.pageCount) { iteration ->
                                    val color = if (pagerState.currentPage == iteration) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .size(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Basic Information Card
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
                            text = "📝 Basic Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = state.name,
                            onValueChange = onUpdateName,
                            label = { Text("Name*") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = state.distillery,
                            onValueChange = onUpdateDistillery,
                            label = { Text("Distillery*") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Whiskey Type Dropdown
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = state.type.name,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                WhiskeyType.values().forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.name) },
                                        onClick = {
                                            onUpdateType(type)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Age and Year
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = state.age,
                                onValueChange = onUpdateAge,
                                label = { Text("Age") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = state.year,
                                onValueChange = onUpdateYear,
                                label = { Text("Year") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // ABV and Price
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = state.abv,
                                onValueChange = onUpdateAbv,
                                label = { Text("ABV %*") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = state.price,
                                onValueChange = onUpdatePrice,
                                label = { Text("Price") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = state.region,
                            onValueChange = onUpdateRegion,
                            label = { Text("Region") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Description
                        OutlinedTextField(
                            value = state.description,
                            onValueChange = onUpdateDescription,
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )

                        Text(
                            text = "💡 팁: 위스키를 추가한 후, 테이스팅 노트를 작성할 때 플레이버 프로파일을 설정할 수 있습니다.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // 저장 버튼
                Button(
                    onClick = onSaveWhiskey,
                    enabled = !state.isLoading && state.name.isNotBlank() && state.distillery.isNotBlank() && state.abv.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = if (state.name.isNotBlank() && state.distillery.isNotBlank() && state.abv.isNotBlank()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    if (state.isLoading) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "저장 중...",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        Text(
                            text = "위스키 저장",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (state.name.isNotBlank() && state.distillery.isNotBlank() && state.abv.isNotBlank()) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}

// Mock 데이터 생성
private val mockEmptyState = AddWhiskeyState()

private val mockFilledState = AddWhiskeyState(
    name = "Macallan 18년",
    distillery = "Macallan",
    type = WhiskeyType.SINGLEMALT,
    age = "18",
    year = "2020",
    abv = "43.0",
    price = "150000",
    region = "스코틀랜드",
    description = "매끄럽고 풍부한 맛의 싱글 몰트 위스키. 바닐라와 오크의 향이 조화롭게 어우러져 있습니다.",
    rating = "92"
)

private val mockLoadingState = AddWhiskeyState(
    name = "Macallan 18년",
    distillery = "Macallan",
    type = WhiskeyType.SINGLEMALT,
    age = "18",
    year = "2020",
    abv = "43.0",
    price = "150000",
    region = "스코틀랜드",
    description = "매끄럽고 풍부한 맛의 싱글 몰트 위스키.",
    rating = "92",
    isLoading = true
)

// Preview 함수들
@Preview(name = "위스키 추가 화면 - 기본", showBackground = true)
@Composable
private fun AddWhiskeyScreenPreview() {
    MaterialTheme {
        AddWhiskeyScreenContent(
            state = mockEmptyState,
            onNavigateBack = {},
            onClickImageIcon = {},
            onSaveWhiskey = {},
            onUpdateName = {},
            onUpdateDistillery = {},
            onUpdateType = {},
            onUpdateAge = {},
            onUpdateYear = {},
            onUpdateAbv = {},
            onUpdatePrice = {},
            onUpdateRegion = {},
            onUpdateDescription = {},
            onUpdateRating = {},
            onAddImage = {},
            onRemoveImage = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Preview(name = "위스키 추가 화면 - 입력된 상태", showBackground = true)
@Composable
private fun AddWhiskeyScreenFilledPreview() {
    MaterialTheme {
        AddWhiskeyScreenContent(
            state = mockFilledState,
            onNavigateBack = {},
            onClickImageIcon = {},
            onSaveWhiskey = {},
            onUpdateName = {},
            onUpdateDistillery = {},
            onUpdateType = {},
            onUpdateAge = {},
            onUpdateYear = {},
            onUpdateAbv = {},
            onUpdatePrice = {},
            onUpdateRegion = {},
            onUpdateDescription = {},
            onUpdateRating = {},
            onAddImage = {},
            onRemoveImage = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
