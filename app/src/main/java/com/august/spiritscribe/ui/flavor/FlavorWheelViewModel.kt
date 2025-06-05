package com.august.spiritscribe.ui.flavor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.domain.model.FlavorProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlavorWheelViewModel @Inject constructor() : ViewModel() {
    private val _flavorProfile = MutableStateFlow(FlavorProfile())
    val flavorProfile: StateFlow<FlavorProfile> = _flavorProfile.asStateFlow()

    fun updateAroma(aroma: List<String>) {
        viewModelScope.launch {
            _flavorProfile.value = _flavorProfile.value.copy(aroma = aroma)
        }
    }

    fun updatePalate(palate: List<String>) {
        viewModelScope.launch {
            _flavorProfile.value = _flavorProfile.value.copy(palate = palate)
        }
    }

    fun updateFinish(finish: List<String>) {
        viewModelScope.launch {
            _flavorProfile.value = _flavorProfile.value.copy(finish = finish)
        }
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