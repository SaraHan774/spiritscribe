package com.august.spiritscribe.ui.note

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.august.spiritscribe.utils.AudioPlayerService
import com.august.spiritscribe.utils.AudioRecorderService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class NewThreadViewModel : ViewModel() {
    private var audioRecorderService: AudioRecorderService? = null
    private var audioPlayerService: AudioPlayerService? = null
    
    // two - way binding
    // https://proandroiddev.com/two-way-data-binding-in-jetpack-compose-1be55c402ec6
    val inputStateFlow = MutableStateFlow("")
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()
    
    val isPlaying: StateFlow<Boolean>
        get() = audioPlayerService?.isPlaying ?: MutableStateFlow(false)
    val currentPosition: StateFlow<Int>
        get() = audioPlayerService?.currentPosition ?: MutableStateFlow(0)
    val duration: StateFlow<Int>
        get() = audioPlayerService?.duration ?: MutableStateFlow(0)

    private val _uim = MutableStateFlow(
        NewThreadUIM(
            nickName = "Whiskey Enthusiast",
            profileImageUrl = "",
            threadContent = ThreadContent.None()
        )
    )
    val uim: StateFlow<NewThreadUIM> = _uim.asStateFlow()

    fun initializeAudioServices(context: Context) {
        if (audioRecorderService == null) {
            audioRecorderService = AudioRecorderService(context)
        }
        if (audioPlayerService == null) {
            audioPlayerService = AudioPlayerService(context)
        }
        
        // Start position update job
        viewModelScope.launch {
            while (true) {
                audioPlayerService?.let { player ->
                    player.updatePosition()
                    // Update progress percentage
                    if (player.duration.value > 0) {
                        _playbackProgress.value = player.currentPosition.value.toFloat() / player.duration.value.toFloat()
                    }
                }
                delay(100) // Update every 100ms
            }
        }
    }

    fun startRecording() {
        try {
            audioRecorderService?.let { recorder ->
                val filePath = recorder.startRecording()
                _isRecording.value = true
                _uim.value = _uim.value.copy(
                    threadContent = ThreadContent.Mic(filePath)
                )
                _successMessage.value = "Recording started"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting recording", e)
            _errorMessage.value = "Failed to start recording: ${e.message}"
        }
    }

    fun stopRecording() {
        try {
            audioRecorderService?.stopRecording()
            _isRecording.value = false
            _successMessage.value = "Recording saved successfully"
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recording", e)
            _errorMessage.value = "Failed to stop recording: ${e.message}"
        }
    }

    fun togglePlayback() {
        try {
            val content = _uim.value.threadContent
            if (content is ThreadContent.Mic) {
                val file = File(content.uri)
                if (file.exists()) {
                    audioPlayerService?.let { player ->
                        if (player.isPlaying.value) {
                            player.pausePlaying()
                            _successMessage.value = "Playback paused"
                        } else {
                            if (player.currentPosition.value > 0) {
                                player.resumePlaying()
                                _successMessage.value = "Playback resumed"
                            } else {
                                player.startPlaying(file)
                                _successMessage.value = "Starting playback"
                            }
                        }
                    }
                } else {
                    _errorMessage.value = "Audio file not found"
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling playback", e)
            _errorMessage.value = "Failed to play audio: ${e.message}"
        }
    }

    fun seekTo(position: Int) {
        try {
            audioPlayerService?.seekTo(position)
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking", e)
            _errorMessage.value = "Failed to seek: ${e.message}"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }

    fun setMessageInput(input: String) {
        inputStateFlow.value = input
    }

    fun onSelectImagesFromGallery(uris: List<String>) {
        _uim.value = _uim.value.copy(
            threadContent = ThreadContent.Image(uris)
        )
    }

    fun onTakePicturePreview(uri: String) {
        _uim.value = _uim.value.copy(
            threadContent = ThreadContent.Image(listOf(uri))
        )
    }

    fun onSelectGif(uri: String) {
        _uim.value = _uim.value.copy(
            threadContent = ThreadContent.Image(listOf(uri))
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopRecording()
        audioPlayerService?.stopPlaying()
        audioRecorderService = null
        audioPlayerService = null
    }
    
    companion object {
        private const val TAG = "NewThreadViewModel"
    }
}
