package com.august.spiritscribe.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.august.spiritscribe.ui.note.NoteListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onWhiskeyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .semantics { isTraversalGroup = true }
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch = viewModel::onSearch,
                active = uiState.isSearchActive,
                onActiveChange = viewModel::onSearchActiveChange,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .semantics { traversalIndex = 0f },
                placeholder = { Text("Search whiskeys...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.Tune, contentDescription = "Filters")
                    }
                }
            ) {
                // Search suggestions
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.searchSuggestions) { suggestion ->
                        ListItem(
                            headlineContent = { Text(suggestion) },
                            leadingContent = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier
                                .clickable {
                                    viewModel.onSearchQueryChange(suggestion)
                                    viewModel.onSearch(suggestion)
                                }
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Filters section
        AnimatedVisibility(visible = showFilters) {
            SearchFilters(
                filters = uiState.filters,
                onFilterChange = viewModel::onFilterChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        // Search results
        if (!uiState.isSearchActive) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.searchResults) { whiskey ->
                        WhiskeySearchResult(
                            whiskey = whiskey,
                            onClick = { onWhiskeyClick(whiskey.id) }
                        )
                    }
                }

                if (uiState.searchResults.isEmpty() && uiState.searchQuery.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Type filter
        FilterChipGroup(
            title = "Type",
            options = listOf("All", "Single Malt", "Bourbon", "Rye", "Blend"),
            selectedOption = filters.type,
            onOptionSelected = { onFilterChange(filters.copy(type = it)) }
        )

        // Rating filter
        FilterChipGroup(
            title = "Rating",
            options = listOf("All", "4★+", "3★+", "2★+", "1★+"),
            selectedOption = filters.rating,
            onOptionSelected = { onFilterChange(filters.copy(rating = it)) }
        )

        // Price range filter
        Text(
            text = "Price Range",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = filters.minPrice?.toString() ?: "",
                onValueChange = { value ->
                    onFilterChange(filters.copy(minPrice = value.toDoubleOrNull()))
                },
                modifier = Modifier.weight(1f),
                label = { Text("Min") },
                singleLine = true
            )
            OutlinedTextField(
                value = filters.maxPrice?.toString() ?: "",
                onValueChange = { value ->
                    onFilterChange(filters.copy(maxPrice = value.toDoubleOrNull()))
                },
                modifier = Modifier.weight(1f),
                label = { Text("Max") },
                singleLine = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChipGroup(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) },
                    label = { Text(option) }
                )
            }
        }
    }
}

@Composable
private fun WhiskeySearchResult(
    whiskey: WhiskeySearchResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        headlineContent = { Text(whiskey.name) },
        supportingContent = { Text(whiskey.description) },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Text(
                text = whiskey.rating.toString() + "★",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    )
} 