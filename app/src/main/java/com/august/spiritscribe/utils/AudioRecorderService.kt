package com.august.spiritscribe.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AudioRecorderService(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var currentFilePath: String? = null

    fun startRecording(): String {
        if (!PermissionHandler.hasRecordAudioPermission(context)) {
            throw SecurityException("RECORD_AUDIO permission not granted")
        }

        val fileName = "whiskey_note_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.mp3"
        val file = File(context.cacheDir, fileName)
        currentFilePath = file.absolutePath

        try {
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(FileOutputStream(file).fd)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(128000)

                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to start recording: ${e.message}")
        }

        return currentFilePath ?: ""
    }

    fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder = null
        }
    }

    fun getRecordingFile(): File? {
        return currentFilePath?.let { File(it) }
    }
} 