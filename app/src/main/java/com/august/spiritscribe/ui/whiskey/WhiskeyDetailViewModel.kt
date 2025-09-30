package com.august.spiritscribe.ui.whiskey

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.domain.model.Whiskey
import com.august.spiritscribe.domain.model.WhiskeyNote
import com.august.spiritscribe.domain.repository.WhiskeyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WhiskeyDetailViewModel @Inject constructor(
    private val whiskeyRepository: WhiskeyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val whiskeyId: String = requireNotNull(savedStateHandle.get<String>("whiskeyId")) {
        "whiskeyId parameter is missing"
    }

    init {
        Log.d("WhiskeyDetailViewModel", "whiskeyId = $whiskeyId")
        // 디버깅: 전체 노트 수와 위스키 수 확인
        viewModelScope.launch {
            try {
                val totalNotes = whiskeyRepository.getTotalNoteCount()
                Log.d("WhiskeyDetailViewModel", "🗂️ 데이터베이스 전체 노트 수: $totalNotes")
                
                val sampleNotes = whiskeyRepository.getFirstFiveNotes()
                Log.d("WhiskeyDetailViewModel", "📄 샘플 노트들:")
                sampleNotes.forEach { note ->
                    Log.d("WhiskeyDetailViewModel", "  - ${note.name} (${note.distillery})")
                }
                
                // 위스키 총 개수도 확인
                whiskeyRepository.getAllWhiskies()
                    .first()
                    .let { whiskies ->
                        Log.d("WhiskeyDetailViewModel", "🥃 데이터베이스 전체 위스키 수: ${whiskies.size}")
                        whiskies.take(5).forEach { whiskey ->
                            Log.d("WhiskeyDetailViewModel", "  - ${whiskey.name} (${whiskey.distillery}) [ID: ${whiskey.id}]")
                        }
                    }
            } catch (e: Exception) {
                Log.e("WhiskeyDetailViewModel", "디버깅 정보 가져오기 실패", e)
            }
        }
    }

    val whiskey: StateFlow<Whiskey?> = flow {
        whiskeyRepository.getWhiskeyById(whiskeyId)
            .onSuccess { whiskey ->
                emit(whiskey)
                Log.d("WhiskeyDetailViewModel", "Successfully loaded whiskey: ${whiskey.name}")
            }
            .onFailure { error ->
                Log.e("WhiskeyDetailViewModel", "Failed to load whiskey: ${error.message}")
                emit(null)
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val notes: StateFlow<List<WhiskeyNote>> = whiskey
        .filterNotNull()
        .flatMapLatest { whiskey ->
            Log.d("WhiskeyDetailViewModel", "🔍 위스키 정보: 이름='${whiskey.name}', 증류소='${whiskey.distillery}'")
            whiskeyRepository.getNotesForWhiskey(whiskey.name, whiskey.distillery)
                .onEach { notes ->
                    Log.d("WhiskeyDetailViewModel", "📝 찾은 노트 개수: ${notes.size}")
                    if (notes.isEmpty()) {
                        Log.w("WhiskeyDetailViewModel", "⚠️ 노트가 없습니다! 쿼리 조건을 확인하세요.")
                    } else {
                        notes.forEach { note ->
                            Log.d("WhiskeyDetailViewModel", "  - 노트: ${note.name} (${note.distillery}) - ${note.additionalNotes.take(50)}")
                        }
                    }
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
} 