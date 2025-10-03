package com.august.spiritscribe.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.domain.model.Whiskey
import com.august.spiritscribe.domain.repository.WhiskeyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val whiskeyRepository: WhiskeyRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _searchFilters = MutableStateFlow(SearchFilters())

    init {
        // 실시간 검색을 위한 Flow 설정
        setupSearchFlow()
        loadSearchHistory()
        loadPopularSearches()
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchFlow() {
        viewModelScope.launch {
            combine(
                _searchQuery.debounce(300), // 300ms 디바운스
                _searchFilters
            ) { query, filters ->
                if (query.isNotEmpty()) {
                    viewModelScope.launch {
                        performSearchInternal(query, filters)
                    }
                } else {
                    _searchState.value = SearchState(
                        query = query,
                        filters = filters,
                        recentSearches = _searchState.value.recentSearches,
                        popularSearches = _searchState.value.popularSearches
                    )
                }
            }.collect()
        }
    }

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilters(filters: SearchFilters) {
        _searchFilters.value = filters
    }

    fun updateSortOption(sortOption: SortOption) {
        _searchState.value = _searchState.value.copy(sortOption = sortOption)
        // 정렬이 변경되면 현재 결과를 다시 정렬
        val currentResults = _searchState.value.results
        if (currentResults.isNotEmpty()) {
            val sortedResults = sortResults(currentResults, sortOption)
            _searchState.value = _searchState.value.copy(results = sortedResults)
        }
    }

    fun performSearch() {
        val query = _searchQuery.value
        val filters = _searchFilters.value
        
        if (query.isNotEmpty()) {
            addToSearchHistory(query)
            viewModelScope.launch {
                performSearchInternal(query, filters)
            }
        }
    }

    private suspend fun performSearchInternal(query: String, filters: SearchFilters) {
        _searchState.value = _searchState.value.copy(isLoading = true, error = null)

        try {
            // 모든 위스키를 가져와서 클라이언트 사이드에서 필터링
            val allWhiskies = whiskeyRepository.getAllWhiskies().first()
            
            val filteredResults = allWhiskies
                .filter { whiskey ->
                    // 텍스트 검색
                    val matchesText = query.isEmpty() || 
                        whiskey.name.contains(query, ignoreCase = true) ||
                        whiskey.distillery.contains(query, ignoreCase = true) ||
                        (whiskey.region?.contains(query, ignoreCase = true) ?: false)
                    
                    // 타입 필터
                    val matchesType = filters.types.isEmpty() || 
                        filters.types.any { type -> 
                            whiskey.type.name.contains(type, ignoreCase = true) 
                        }
                    
                    // 지역 필터
                    val matchesRegion = filters.regions.isEmpty() || 
                        filters.regions.any { region -> 
                            whiskey.region?.contains(region, ignoreCase = true) ?: false
                        }
                    
                    // 가격 필터
                    val matchesPrice = whiskey.price?.let { price ->
                        price >= filters.priceRange.first && price <= filters.priceRange.second
                    } ?: true
                    
                    // 평점 필터
                    val matchesRating = whiskey.rating?.let { rating ->
                        rating >= filters.minRating
                    } ?: true
                    
                    matchesText && matchesType && matchesRegion && matchesPrice && matchesRating
                }
                .let { results ->
                    sortResults(results, _searchState.value.sortOption)
                }

            _searchState.value = _searchState.value.copy(
                query = query,
                results = filteredResults,
                filters = filters,
                isLoading = false
            )

        } catch (e: Exception) {
            _searchState.value = _searchState.value.copy(
                isLoading = false,
                error = "검색 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }

    private fun sortResults(results: List<Whiskey>, sortOption: SortOption): List<Whiskey> {
        return when (sortOption) {
            SortOption.NAME -> results.sortedBy { it.name }
            SortOption.RATING -> results.sortedByDescending { it.rating ?: 0 }
            SortOption.PRICE -> results.sortedBy { it.price ?: Double.MAX_VALUE }
            SortOption.YEAR -> results.sortedByDescending { it.year ?: 0 }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchState.value = _searchState.value.copy(
            query = "",
            results = emptyList()
        )
    }

    fun clearFilters() {
        _searchFilters.value = SearchFilters()
        _searchState.value = _searchState.value.copy(filters = SearchFilters())
    }

    fun clearSearchHistory() {
        _searchState.value = _searchState.value.copy(recentSearches = emptyList())
        // 실제로는 SharedPreferences나 데이터베이스에서도 삭제해야 함
    }

    private fun addToSearchHistory(query: String) {
        val currentHistory = _searchState.value.recentSearches.toMutableList()
        currentHistory.remove(query) // 중복 제거
        currentHistory.add(0, query) // 맨 앞에 추가
        val newHistory = currentHistory.take(10) // 최대 10개만 유지
        
        _searchState.value = _searchState.value.copy(recentSearches = newHistory)
    }

    private fun loadSearchHistory() {
        // 실제로는 SharedPreferences나 데이터베이스에서 로드
        val mockHistory = listOf("스코틀랜드", "버번", "일본 위스키")
        _searchState.value = _searchState.value.copy(recentSearches = mockHistory)
    }

    private fun loadPopularSearches() {
        // 실제로는 서버에서 인기 검색어를 가져오거나 로컬 통계를 기반으로 계산
        val mockPopular = listOf("싱글몰트", "버번", "스코틀랜드", "일본 위스키", "블렌디드")
        _searchState.value = _searchState.value.copy(popularSearches = mockPopular)
    }
}