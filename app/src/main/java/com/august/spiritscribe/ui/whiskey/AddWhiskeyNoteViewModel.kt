package com.august.spiritscribe.ui.whiskey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.domain.model.Whiskey
import com.august.spiritscribe.domain.model.WhiskeyNote
import com.august.spiritscribe.domain.model.FinalRating
import com.august.spiritscribe.domain.model.ColorMeter
import com.august.spiritscribe.domain.model.FlavorIntensity
import com.august.spiritscribe.domain.model.Flavor
import com.august.spiritscribe.domain.repository.WhiskeyRepository
import com.august.spiritscribe.domain.repository.WhiskeyNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class AddWhiskeyNoteViewModel @Inject constructor(
    private val whiskeyRepository: WhiskeyRepository,
    private val whiskeyNoteRepository: WhiskeyNoteRepository
) : ViewModel() {

    private val _whiskey = MutableStateFlow<Whiskey?>(null)
    val whiskey: StateFlow<Whiskey?> = _whiskey.asStateFlow()

    private val _noteText = MutableStateFlow("")
    val noteText: StateFlow<String> = _noteText.asStateFlow()

    private val _rating = MutableStateFlow(3) // 기본값 3점
    val rating: StateFlow<Int> = _rating.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _selectedFlavors = MutableStateFlow<Map<Flavor, Int>>(emptyMap())
    val selectedFlavors: StateFlow<Map<Flavor, Int>> = _selectedFlavors.asStateFlow()

    fun loadWhiskey(whiskeyId: String) {
        viewModelScope.launch {
            try {
                val result = whiskeyRepository.getWhiskeyById(whiskeyId)
                if (result.isSuccess) {
                    _whiskey.value = result.getOrNull()
                }
            } catch (e: Exception) {
                // 에러 처리 - 필요시 에러 상태 추가
            }
        }
    }

    fun updateNoteText(text: String) {
        _noteText.value = text
    }

    fun updateRating(newRating: Int) {
        _rating.value = newRating
    }

    fun toggleFlavor(flavor: Flavor) {
        android.util.Log.d("AddWhiskeyNoteViewModel", "🎯 toggleFlavor 호출됨: ${flavor.name}")
        val currentFlavors = _selectedFlavors.value.toMutableMap()
        val wasSelected = currentFlavors.containsKey(flavor)
        android.util.Log.d("AddWhiskeyNoteViewModel", "🔍 현재 선택 상태: ${flavor.name} = $wasSelected")
        
        if (wasSelected) {
            currentFlavors.remove(flavor)
            android.util.Log.d("AddWhiskeyNoteViewModel", "❌ 플레이버 해제: ${flavor.name}")
        } else {
            currentFlavors[flavor] = 3 // 기본 강도 3
            android.util.Log.d("AddWhiskeyNoteViewModel", "✅ 플레이버 선택: ${flavor.name}")
        }
        
        // 새로운 Map 인스턴스 생성하여 StateFlow 업데이트 강제
        val newMap = currentFlavors.toMap()
        _selectedFlavors.value = newMap
        android.util.Log.d("AddWhiskeyNoteViewModel", "📝 StateFlow 업데이트 완료: ${_selectedFlavors.value.keys}")
        android.util.Log.d("AddWhiskeyNoteViewModel", "📊 전체 선택된 플레이버: ${_selectedFlavors.value}")
    }

    fun updateFlavorIntensity(flavor: Flavor, intensity: Int) {
        android.util.Log.d("AddWhiskeyNoteViewModel", "🌶️ 플레이버 강도 변경: ${flavor.name} -> $intensity")
        val currentFlavors = _selectedFlavors.value.toMutableMap()
        currentFlavors[flavor] = intensity
        _selectedFlavors.value = currentFlavors.toMap()
        android.util.Log.d("AddWhiskeyNoteViewModel", "✅ 강도 업데이트 완료")
    }

    fun isFlavorSelected(flavor: Flavor): Boolean {
        val isSelected = _selectedFlavors.value.containsKey(flavor)
        android.util.Log.d("AddWhiskeyNoteViewModel", "🔍 플레이버 선택 상태 확인: ${flavor.name} = $isSelected")
        return isSelected
    }

    fun getFlavorIntensity(flavor: Flavor): Int {
        return _selectedFlavors.value[flavor] ?: 3
    }

    fun saveNote(onSuccess: () -> Unit) {
        val whiskeyData = _whiskey.value ?: return
        val noteTextValue = _noteText.value.trim()
        
        if (noteTextValue.isBlank()) return

        viewModelScope.launch {
            try {
                _isSaving.value = true
                
                val note = WhiskeyNote(
                    name = whiskeyData.name,
                    distillery = whiskeyData.distillery,
                    origin = whiskeyData.region ?: "Unknown",
                    type = whiskeyData.type.name,
                    age = whiskeyData.age,
                    year = whiskeyData.year,
                    abv = whiskeyData.abv,
                    price = whiskeyData.price,
                    sampled = true,
                    color = ColorMeter("Unknown", 3), // 기본값
                    additionalNotes = noteTextValue,
                    finalRating = FinalRating(
                        appearance = rating.value * 20, // 5점을 100점으로 변환
                        nose = rating.value * 20,
                        taste = rating.value * 20,
                        finish = rating.value * 20,
                        overall = rating.value * 20
                    ),
                    flavors = _selectedFlavors.value.map { (flavor, intensity) ->
                        FlavorIntensity(flavor = flavor, intensity = intensity)
                    }
                )
                
                val result = whiskeyNoteRepository.createNote(note)
                if (result.isSuccess) {
                    onSuccess()
                }
            } catch (e: Exception) {
                // 에러 처리 - 필요시 에러 상태 추가
            } finally {
                _isSaving.value = false
            }
        }
    }
}
