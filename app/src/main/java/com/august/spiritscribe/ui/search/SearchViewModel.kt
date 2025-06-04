package com.august.spiritscribe.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val searchResults: List<WhiskeySearchResult> = emptyList(),
    val searchSuggestions: List<String> = emptyList(),
    val filters: SearchFilters = SearchFilters(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class SearchFilters(
    val type: String = "All",
    val rating: String = "All",
    val minPrice: Double? = null,
    val maxPrice: Double? = null
)

data class WhiskeySearchResult(
    val id: String,
    val name: String,
    val description: String,
    val type: String,
    val rating: Double,
    val price: Double?
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    // TODO: Inject repositories
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _filters = MutableStateFlow(SearchFilters())

    @OptIn(FlowPreview::class)
    private val _searchResults = _searchQuery
        .debounce(300)
        .combine(_filters) { query, filters ->
            if (query.length >= 2) {
                searchWhiskeys(query, filters)
            } else {
                emptyList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            combine(
                _searchQuery,
                _searchResults,
                _filters
            ) { query, results, filters ->
                SearchUiState(
                    searchQuery = query,
                    searchResults = results,
                    filters = filters,
                    searchSuggestions = generateSuggestions(query),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val results = searchWhiskeys(query, _filters.value)
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Search failed: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun onSearchActiveChange(active: Boolean) {
        _uiState.value = _uiState.value.copy(isSearchActive = active)
    }

    fun onFilterChange(filters: SearchFilters) {
        _filters.value = filters
    }

    private suspend fun searchWhiskeys(query: String, filters: SearchFilters): List<WhiskeySearchResult> {
        // TODO: Implement actual search using repository
        // This is mock data for demonstration
        return listOf(
            WhiskeySearchResult(
                id = "1",
                name = "Macallan 12 Double Cask",
                description = "Highland Single Malt Scotch Whisky",
                type = "Single Malt",
                rating = 4.5,
                price = 89.99
            ),
            WhiskeySearchResult(
                id = "2",
                name = "Buffalo Trace",
                description = "Kentucky Straight Bourbon Whiskey",
                type = "Bourbon",
                rating = 4.2,
                price = 29.99
            ),
            WhiskeySearchResult(
                id = "3",
                name = "Lagavulin 16",
                description = "Islay Single Malt Scotch Whisky",
                type = "Single Malt",
                rating = 4.8,
                price = 109.99
            )
        ).filter { whiskey ->
            whiskey.name.contains(query, ignoreCase = true) &&
            (filters.type == "All" || whiskey.type == filters.type) &&
            (filters.rating == "All" || whiskey.rating >= filters.rating.first().toString().toDouble()) &&
            (filters.minPrice == null || whiskey.price == null || whiskey.price >= filters.minPrice) &&
            (filters.maxPrice == null || whiskey.price == null || whiskey.price <= filters.maxPrice)
        }
    }

    private fun generateSuggestions(query: String): List<String> {
        // TODO: Implement actual suggestions using repository
        return if (query.isNotEmpty()) {
            listOf(
                "$query Single Malt",
                "$query Bourbon",
                "$query Whiskey",
                "Best $query",
                "Top Rated $query"
            )
        } else {
            emptyList()
        }
    }
} 