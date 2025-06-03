package com.august.spiritscribe.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class AudioPlayerService(private val context: Context) {
    private var player: MediaPlayer? = null
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition
    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration
    
    companion object {
        private const val TAG = "AudioPlayerService"
    }

    fun startPlaying(file: File) {
        Log.d(TAG, "Starting playback of file: ${file.absolutePath}")
        if (!file.exists()) {
            Log.e(TAG, "File does not exist: ${file.absolutePath}")
            return
        }
        
        stopPlaying()
        
        try {
            player = MediaPlayer().apply {
                setOnErrorListener { mp, what, extra ->
                    Log.e(TAG, "MediaPlayer Error: what=$what extra=$extra")
                    false
                }
                
                setOnPreparedListener {
                    Log.d(TAG, "MediaPlayer prepared, duration: $duration")
                    _duration.value = duration
                    start()
                    _isPlaying.value = true
                }
                
                setOnCompletionListener {
                    Log.d(TAG, "Playback completed")
                    stopPlaying()
                }
                
                try {
                    setDataSource(file.absolutePath)
                    prepareAsync()
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting data source", e)
                    release()
                    throw e
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MediaPlayer", e)
            stopPlaying()
        }
    }

    fun pausePlaying() {
        Log.d(TAG, "Pausing playback")
        player?.let {
            if (it.isPlaying) {
                try {
                    it.pause()
                    _currentPosition.value = it.currentPosition
                    _isPlaying.value = false
                } catch (e: Exception) {
                    Log.e(TAG, "Error pausing playback", e)
                }
            }
        }
    }

    fun resumePlaying() {
        Log.d(TAG, "Resuming playback")
        player?.let {
            if (!it.isPlaying) {
                try {
                    it.start()
                    _isPlaying.value = true
                } catch (e: Exception) {
                    Log.e(TAG, "Error resuming playback", e)
                }
            }
        }
    }

    fun stopPlaying() {
        Log.d(TAG, "Stopping playback")
        try {
            player?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping playback", e)
        } finally {
            player = null
            _isPlaying.value = false
            _currentPosition.value = 0
            _duration.value = 0
        }
    }

    fun seekTo(position: Int) {
        Log.d(TAG, "Seeking to position: $position")
        try {
            player?.seekTo(position)
            _currentPosition.value = position
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking to position", e)
        }
    }

    fun updatePosition() {
        player?.let {
            if (it.isPlaying) {
                try {
                    _currentPosition.value = it.currentPosition
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating position", e)
                }
            }
        }
    }
} 