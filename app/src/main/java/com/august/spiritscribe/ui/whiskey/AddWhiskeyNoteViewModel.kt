package com.august.spiritscribe.ui.whiskey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.domain.model.Whiskey
import com.august.spiritscribe.domain.model.WhiskeyNote
import com.august.spiritscribe.domain.model.FinalRating
import com.august.spiritscribe.domain.model.ColorMeter
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
                    flavors = emptyList() // 나중에 플레이버 선택 기능 추가 가능
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
