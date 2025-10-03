package com.august.spiritscribe.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.august.spiritscribe.domain.model.Whiskey
import com.august.spiritscribe.domain.model.WhiskeyType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onWhiskeyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchState by viewModel.searchState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var showFilters by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 검색 헤더
        SearchHeader(
            query = searchState.query,
            sortOption = searchState.sortOption,
            onQueryChange = viewModel::updateQuery,
            onSearch = { 
                viewModel.performSearch()
                keyboardController?.hide()
            },
            onClear = viewModel::clearSearch,
            onFilterClick = { showFilters = !showFilters },
            showFilters = showFilters
        )

        // 필터 섹션
        AnimatedVisibility(
            visible = showFilters,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            SearchFilters(
                filters = searchState.filters,
                onFilterChange = viewModel::updateFilters,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // 검색 결과
        when {
            searchState.isLoading -> {
                LoadingContent()
            }
            searchState.error != null -> {
                ErrorContent(
                    error = searchState.error!!,
                    onRetry = { viewModel.performSearch() }
                )
            }
            searchState.query.isEmpty() -> {
                EmptySearchContent(
                    recentSearches = searchState.recentSearches,
                    popularSearches = searchState.popularSearches,
                    onSearchClick = { query ->
                        viewModel.updateQuery(query)
                        viewModel.performSearch()
                    },
                    onClearHistory = viewModel::clearSearchHistory
                )
            }
            searchState.results.isEmpty() -> {
                NoResultsContent(
                    query = searchState.query,
                    onClearFilters = viewModel::clearFilters
                )
            }
            else -> {
                SearchResults(
                    results = searchState.results,
                    onWhiskeyClick = onWhiskeyClick,
                    sortOption = searchState.sortOption,
                    onSortChange = viewModel::updateSortOption
                )
            }
        }
    }
}

@Composable
private fun SearchHeader(
    query: String,
    sortOption: SortOption,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    onFilterClick: () -> Unit,
    showFilters: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        // 검색창
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("위스키, 증류소, 지역으로 검색...") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "검색",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "지우기",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 필터 버튼과 정렬 옵션
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 필터 버튼
            FilterChip(
                onClick = onFilterClick,
                label = { Text("필터") },
                leadingIcon = {
                    Icon(
                        if (showFilters) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                selected = showFilters,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            // 정렬 옵션
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "정렬:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = when (sortOption) {
                        SortOption.NAME -> "이름"
                        SortOption.RATING -> "평점"
                        SortOption.PRICE -> "가격"
                        SortOption.YEAR -> "연도"
                        else -> "이름"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SearchFilters(
    filters: SearchFilters,
    onFilterChange: (SearchFilters) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 위스키 타입 필터
            FilterSection(
                title = "위스키 타입",
                options = WhiskeyType.values().map { it.name },
                selectedOptions = filters.types,
                onSelectionChange = { types ->
                    onFilterChange(filters.copy(types = types))
                }
            )

            // 지역 필터
            FilterSection(
                title = "지역",
                options = listOf("스코틀랜드", "아일랜드", "미국", "일본", "캐나다", "대만"),
                selectedOptions = filters.regions,
                onSelectionChange = { regions ->
                    onFilterChange(filters.copy(regions = regions))
                }
            )

            // 가격대 필터
            PriceRangeFilter(
                priceRange = filters.priceRange,
                onPriceRangeChange = { priceRange ->
                    onFilterChange(filters.copy(priceRange = priceRange))
                }
            )

            // 평점 필터
            RatingFilter(
                minRating = filters.minRating,
                onRatingChange = { minRating ->
                    onFilterChange(filters.copy(minRating = minRating))
                }
            )

            // 필터 초기화 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onFilterChange(SearchFilters()) }) {
                    Text("필터 초기화")
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options) { option ->
                FilterChip(
                    onClick = {
                        val newSelection = if (option in selectedOptions) {
                            selectedOptions - option
                        } else {
                            selectedOptions + option
                        }
                        onSelectionChange(newSelection)
                    },
                    label = { Text(option) },
                    selected = option in selectedOptions,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun PriceRangeFilter(
    priceRange: Pair<Float, Float>,
    onPriceRangeChange: (Pair<Float, Float>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "가격대 (만원)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${priceRange.first.toInt()}만원",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${priceRange.second.toInt()}만원",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        RangeSlider(
            value = priceRange.first..priceRange.second,
            onValueChange = { range -> onPriceRangeChange(range.start to range.endInclusive) },
            valueRange = 0f..1000f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RatingFilter(
    minRating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "최소 평점: ${minRating}점",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Slider(
            value = minRating.toFloat(),
            onValueChange = { onRatingChange(it.toInt()) },
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SearchResults(
    results: List<Whiskey>,
    onWhiskeyClick: (String) -> Unit,
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // 정렬 옵션
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "정렬:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (sortOption) {
                        SortOption.NAME -> "이름"
                        SortOption.RATING -> "평점"
                        SortOption.PRICE -> "가격"
                        SortOption.YEAR -> "연도"
                        else -> "이름"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        items(results) { whiskey ->
            WhiskeySearchItem(
                whiskey = whiskey,
                onWhiskeyClick = onWhiskeyClick
            )
        }
    }
}

@Composable
private fun WhiskeySearchItem(
    whiskey: Whiskey,
    onWhiskeyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onWhiskeyClick(whiskey.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 위스키 이미지 (placeholder)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocalBar,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 위스키 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = whiskey.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = whiskey.distillery,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${whiskey.abv}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    whiskey.age?.let { age ->
                        Text(
                            text = "${age}년",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // 평점
            whiskey.rating?.let { rating ->
                Surface(
                    shape = CircleShape,
                    color = when {
                        rating >= 90 -> Color(0xFF4CAF50)
                        rating >= 80 -> Color(0xFF2196F3)
                        rating >= 70 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                ) {
                    Text(
                        text = "$rating",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySearchContent(
    recentSearches: List<String>,
    popularSearches: List<String>,
    onSearchClick: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 인기 검색어
        if (popularSearches.isNotEmpty()) {
            item {
                SearchSection(
                    title = "인기 검색어",
                    searches = popularSearches,
                    onSearchClick = onSearchClick
                )
            }
        }
        
        // 최근 검색어
        if (recentSearches.isNotEmpty()) {
            item {
                SearchSection(
                    title = "최근 검색어",
                    searches = recentSearches,
                    onSearchClick = onSearchClick,
                    onClear = onClearHistory
                )
            }
        }
        
        // 검색 제안
        item {
            SearchSuggestions(onSearchClick = onSearchClick)
        }
    }
}

@Composable
private fun SearchSection(
    title: String,
    searches: List<String>,
    onSearchClick: (String) -> Unit,
    onClear: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            onClear?.let { clear ->
                TextButton(onClick = clear) {
                    Text("지우기")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searches) { search ->
                FilterChip(
                    onClick = { onSearchClick(search) },
                    label = { Text(search) },
                    selected = false,
                    leadingIcon = {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchSuggestions(
    onSearchClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "검색 제안",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("스코틀랜드", "버번", "일본 위스키", "싱글몰트", "블렌디드")) { suggestion ->
                FilterChip(
                    onClick = { onSearchClick(suggestion) },
                    label = { Text(suggestion) },
                    selected = false
                )
            }
        }
    }
}

@Composable
private fun NoResultsContent(
    query: String,
    onClearFilters: () -> Unit,
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
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "검색 결과가 없습니다",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "\"$query\"에 대한 검색 결과를 찾을 수 없습니다",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Button(onClick = onClearFilters) {
                Text("필터 초기화")
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
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("다시 시도")
            }
        }
    }
}

// 데이터 클래스들
data class SearchFilters(
    val types: Set<String> = emptySet(),
    val regions: Set<String> = emptySet(),
    val priceRange: Pair<Float, Float> = 0f to 1000f,
    val minRating: Int = 0
)

enum class SortOption {
    NAME, RATING, PRICE, YEAR
}

data class SearchState(
    val query: String = "",
    val results: List<Whiskey> = emptyList(),
    val filters: SearchFilters = SearchFilters(),
    val sortOption: SortOption = SortOption.NAME,
    val recentSearches: List<String> = emptyList(),
    val popularSearches: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)