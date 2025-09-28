package com.august.spiritscribe.ui.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.domain.model.WhiskeyNote
import com.august.spiritscribe.domain.repository.WhiskeyNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val whiskeyNoteRepository: WhiskeyNoteRepository
) : ViewModel() {

    var note by mutableStateOf<WhiskeyNote?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadNote(noteId: String) {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                
                whiskeyNoteRepository.getNoteById(noteId)
                    .onSuccess { foundNote ->
                        note = foundNote
                        isLoading = false
                    }
                    .onFailure { throwable ->
                        error = "Failed to load note: ${throwable.message}"
                        isLoading = false
                    }
            } catch (e: Exception) {
                error = "Failed to load note: ${e.message}"
                isLoading = false
            }
        }
    }
}