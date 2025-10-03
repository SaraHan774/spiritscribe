package com.august.spiritscribe.ui.flavor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.domain.model.FlavorProfile
import com.august.spiritscribe.domain.model.FlavorIntensity
import com.august.spiritscribe.domain.model.Flavor
import com.august.spiritscribe.data.local.dao.WhiskeyNoteDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlavorWheelViewModel @Inject constructor(
    private val whiskeyNoteDao: WhiskeyNoteDao
) : ViewModel() {
    private val _flavorProfile = MutableStateFlow(FlavorProfile())
    val flavorProfile: StateFlow<FlavorProfile> = _flavorProfile.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadFlavorData()
    }
    
    /**
     * 데이터베이스에서 모든 위스키 노트의 플레이버 데이터를 가져와서 분석
     */
    private fun loadFlavorData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // 모든 노트 가져오기
                val notes = whiskeyNoteDao.getNotesByCreatedDesc().catch { e ->
                    _error.value = "플레이버 데이터를 불러오는 중 오류가 발생했습니다: ${e.message}"
                }.onEach { noteEntities ->
                    // 플레이버 데이터 분석 및 집계
                    val aggregatedFlavors = analyzeFlavorData(noteEntities)
                    _flavorProfile.value = aggregatedFlavors
                    _isLoading.value = false
                }
                
                // Flow를 collect하여 데이터 로딩 시작
                notes.collect { /* Flow는 onEach에서 처리됨 */ }
                
            } catch (e: Exception) {
                _error.value = "플레이버 데이터 분석 중 오류가 발생했습니다: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 위스키 노트들의 플레이버 데이터를 분석하여 집계된 FlavorProfile 생성
     */
    private fun analyzeFlavorData(notes: List<com.august.spiritscribe.data.local.entity.WhiskeyNoteEntity>): FlavorProfile {
        val allFlavors = mutableListOf<FlavorIntensity>()
        
        // 모든 노트에서 플레이버 데이터 추출
        notes.forEach { noteEntity ->
            try {
                val note = noteEntity.toDomain()
                allFlavors.addAll(note.flavors)
            } catch (e: Exception) {
                // 개별 노트 파싱 오류는 무시하고 계속 진행
            }
        }
        
        // 플레이버별 평균 강도 계산
        val flavorAverages = allFlavors.groupBy { it.flavor }
            .mapValues { (_, intensities) ->
                intensities.map { it.intensity }.average()
            }
        
        // 카테고리별로 플레이버 분류
        val aromaFlavors = mutableListOf<String>()
        val palateFlavors = mutableListOf<String>()
        val finishFlavors = mutableListOf<String>()
        
        flavorAverages.forEach { (flavor, avgIntensity) ->
            val intensity = avgIntensity.toInt()
            val flavorName = flavor.displayName
            
            when {
                // 향 관련 플레이버
                flavor in listOf(Flavor.FLORAL, Flavor.CITRUS, Flavor.HERB, Flavor.PEAT) -> {
                    aromaFlavors.add("$flavorName (강도: $intensity/5)")
                }
                // 맛 관련 플레이버
                flavor in listOf(Flavor.MALT, Flavor.FRUIT, Flavor.DRIED, Flavor.SPICE, 
                               Flavor.WOOD, Flavor.NUTS, Flavor.TOFFEE, Flavor.VANILLA, Flavor.HONEY, Flavor.CHAR) -> {
                    palateFlavors.add("$flavorName (강도: $intensity/5)")
                }
                // 피니시 관련은 현재 Flavor enum에 없으므로 향/맛에 포함
                else -> {
                    palateFlavors.add("$flavorName (강도: $intensity/5)")
                }
            }
        }
        
        return FlavorProfile(
            aroma = aromaFlavors.distinct().sorted(),
            palate = palateFlavors.distinct().sorted(),
            finish = finishFlavors.distinct().sorted()
        )
    }
    
    /**
     * 수동으로 플레이버 데이터 새로고침
     */
    fun refreshFlavorData() {
        loadFlavorData()
    }

    // 카테고리별 선택 가능한 향/맛 목록 반환
    fun getAvailableFlavorsForCategory(category: String): List<String> {
        return when {
            FlavorProfile.AROMA_CATEGORIES.any { it.first == category } ->
                FlavorProfile.AROMA_CATEGORIES.find { it.first == category }?.second ?: emptyList()
            FlavorProfile.PALATE_CATEGORIES.any { it.first == category } ->
                FlavorProfile.PALATE_CATEGORIES.find { it.first == category }?.second ?: emptyList()
            FlavorProfile.FINISH_CHARACTERISTICS.any { it.first == category } ->
                FlavorProfile.FINISH_CHARACTERISTICS.find { it.first == category }?.second ?: emptyList()
            else -> emptyList()
        }
    }

    // 선택된 향/맛이 속한 카테고리 찾기
    fun findCategoryForFlavor(flavor: String): String? {
        FlavorProfile.AROMA_CATEGORIES.forEach { (category, flavors) ->
            if (flavor in flavors) return category
        }
        FlavorProfile.PALATE_CATEGORIES.forEach { (category, flavors) ->
            if (flavor in flavors) return category
        }
        FlavorProfile.FINISH_CHARACTERISTICS.forEach { (category, characteristics) ->
            if (flavor in characteristics) return category
        }
        return null
    }
} 