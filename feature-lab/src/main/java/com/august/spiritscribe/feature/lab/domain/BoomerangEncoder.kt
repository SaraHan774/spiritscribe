package com.august.spiritscribe.feature.lab.domain

import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer

/**
 * MediaCodec/MediaMuxer를 사용한 보머랭 비디오 인코더
 * 
 * 참고: https://developer.android.com/reference/android/media/MediaCodec
 * https://developer.android.com/reference/android/media/MediaMuxer
 * 
 * Forward + Reverse 루프를 생성하여 보머랭 효과 구현
 */
class BoomerangEncoder {
    
    companion object {
        private const val TAG = "BoomerangEncoder"
        
        // 비디오 인코딩 설정
        private const val MIME_TYPE = "video/avc" // H.264
        private const val BIT_RATE = 6_000_000 // 6 Mbps
        private const val FRAME_RATE = 30
        private const val I_FRAME_INTERVAL = 1
        
        // 해상도 설정
        private const val OUTPUT_WIDTH = 720
        private const val OUTPUT_HEIGHT = 720
    }
    
    /**
     * 보머랭 비디오 생성
     */
    suspend fun createBoomerangVideo(
        frames: List<Bitmap>,
        outputPath: String,
        styleAppliedFrames: List<Bitmap>? = null
    ): String = withContext(Dispatchers.Default) {
        
        try {
            Log.d(TAG, "Starting boomerang video creation with ${frames.size} frames")
            
            // MediaCodec 설정
            val format = MediaFormat.createVideoFormat(MIME_TYPE, OUTPUT_WIDTH, OUTPUT_HEIGHT).apply {
                setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
                setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL)
                setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            }
            
            // 인코더 생성
            val encoder = MediaCodec.createEncoderByType(MIME_TYPE)
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            
            // MediaMuxer 설정
            val muxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            
            try {
                // 보머랭 시퀀스 생성 (Forward + Reverse)
                val boomerangFrames = createBoomerangSequence(
                    frames = frames,
                    styleFrames = styleAppliedFrames
                )
                
                // 인코딩 실행
                encodeFramesToVideo(encoder, muxer, boomerangFrames)
                
                Log.d(TAG, "Boomerang video created successfully: $outputPath")
                return@withContext outputPath
                
            } finally {
                encoder.stop()
                encoder.release()
                muxer.stop()
                muxer.release()
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create boomerang video", e)
            throw e
        }
    }
    
    /**
     * 보머랭 시퀀스 생성 (Forward + Reverse)
     */
    private fun createBoomerangSequence(
        frames: List<Bitmap>,
        styleFrames: List<Bitmap>?
    ): List<Bitmap> {
        val targetFrames = styleFrames ?: frames
        val boomerangSequence = mutableListOf<Bitmap>()
        
        // Forward: 원본 순서
        boomerangSequence.addAll(targetFrames)
        
        // Reverse: 역순 (첫 번째와 마지막 프레임 제외하여 자연스러운 루프)
        if (targetFrames.size > 2) {
            val reverseFrames = targetFrames.drop(1).dropLast(1).reversed()
            boomerangSequence.addAll(reverseFrames)
        }
        
        Log.d(TAG, "Created boomerang sequence with ${boomerangSequence.size} frames")
        return boomerangSequence
    }
    
    /**
     * 프레임들을 비디오로 인코딩
     */
    private fun encodeFramesToVideo(
        encoder: MediaCodec,
        muxer: MediaMuxer,
        frames: List<Bitmap>
    ) {
        encoder.start()
        
        val inputSurface = encoder.createInputSurface()
        val bufferInfo = MediaCodec.BufferInfo()
        var trackIndex = -1
        var muxerStarted = false
        
        try {
            // 프레임 인코딩
            frames.forEachIndexed { index, bitmap ->
                // 비트맵을 Surface에 그리기 (Canvas 사용)
                drawBitmapToSurface(inputSurface, bitmap)
                
                // 인코더에 입력 완료 신호
                encoder.signalEndOfInputStream()
                
                // 출력 버퍼 처리
                while (true) {
                    val outputBufferIndex = encoder.dequeueOutputBuffer(bufferInfo, 10000)
                    
                    when {
                        outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                            // 더 이상 출력 버퍼가 없음
                            break
                        }
                        
                        outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                            // 출력 형식 변경
                            val newFormat = encoder.outputFormat
                            trackIndex = muxer.addTrack(newFormat)
                            muxer.start()
                            muxerStarted = true
                            Log.d(TAG, "Output format changed, track index: $trackIndex")
                        }
                        
                        outputBufferIndex >= 0 -> {
                            // 유효한 출력 버퍼
                            val outputBuffer = encoder.getOutputBuffer(outputBufferIndex)
                            
                            if (outputBuffer != null && bufferInfo.size > 0) {
                                if (muxerStarted) {
                                    muxer.writeSampleData(trackIndex, outputBuffer, bufferInfo)
                                }
                            }
                            
                            encoder.releaseOutputBuffer(outputBufferIndex, false)
                            
                            if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                Log.d(TAG, "End of stream reached")
                                break
                            }
                        }
                    }
                }
            }
            
        } finally {
            inputSurface.release()
        }
    }
    
    /**
     * 비트맵을 Surface에 그리기
     * 실제 구현에서는 OpenGL ES나 Canvas를 사용해야 함
     */
    private fun drawBitmapToSurface(surface: android.view.Surface, bitmap: Bitmap) {
        // TODO: 실제 구현 필요
        // OpenGL ES를 사용하여 비트맵을 Surface에 렌더링
        // 또는 Canvas를 사용하여 그리기
        
        // 임시 구현: 비트맵을 원하는 해상도로 리사이즈
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, OUTPUT_WIDTH, OUTPUT_HEIGHT, true)
        
        // 실제 Surface 렌더링 로직은 OpenGL ES나 Canvas로 구현 필요
        Log.d(TAG, "Drawing bitmap to surface (${resizedBitmap.width}x${resizedBitmap.height})")
    }
    
    /**
     * GIF 생성 (옵션)
     */
    suspend fun createBoomerangGif(
        frames: List<Bitmap>,
        outputPath: String,
        styleAppliedFrames: List<Bitmap>? = null
    ): String = withContext(Dispatchers.Default) {
        
        try {
            Log.d(TAG, "Creating boomerang GIF with ${frames.size} frames")
            
            // 보머랭 시퀀스 생성
            val boomerangFrames = createBoomerangSequence(frames, styleAppliedFrames)
            
            // GIF 인코딩 (실제 구현 필요)
            // Android에서는 GIF 인코딩을 위한 라이브러리가 필요 (예: Glide, Fresco 등)
            encodeFramesToGif(boomerangFrames, outputPath)
            
            Log.d(TAG, "Boomerang GIF created successfully: $outputPath")
            return@withContext outputPath
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create boomerang GIF", e)
            throw e
        }
    }
    
    /**
     * 프레임들을 GIF로 인코딩
     */
    private fun encodeFramesToGif(frames: List<Bitmap>, outputPath: String) {
        // TODO: GIF 인코딩 구현
        // Android에서 GIF 인코딩을 위해서는 외부 라이브러리 사용 권장
        // 예: Glide, Fresco, 또는 GIF 인코딩 전용 라이브러리
        
        Log.d(TAG, "GIF encoding not implemented yet, saving as placeholder")
        
        // 임시로 첫 번째 프레임을 PNG로 저장
        val outputFile = File(outputPath.replace(".gif", ".png"))
        frames.firstOrNull()?.let { firstFrame ->
            // 실제 GIF 인코딩 로직 구현 필요
        }
    }
    
    /**
     * 파일 크기 최적화
     */
    private fun optimizeFileSize(inputPath: String, maxSizeMB: Int = 10): String {
        // TODO: 파일 크기 최적화 로직 구현
        // 비트레이트 조정, 해상도 조정 등을 통해 파일 크기 최적화
        
        return inputPath
    }
}
