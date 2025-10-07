package com.august.spiritscribe.feature.lab.domain

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.collections.flatten

/**
 * TensorFlow Lite 기반 스타일 전이 엔진
 * 
 * 참고: https://blog.tensorflow.org/2020/04/optimizing-style-transfer-to-run-on-mobile-with-tflite.html
 * GPU delegate를 사용하여 온디바이스 스타일 전이 수행
 */
class StyleTransferEngine(private val context: Context) {
    
    companion object {
        private const val TAG = "StyleTransferEngine"
        
        // 모델 입력 크기 (일반적인 모바일 스타일 전이 모델 크기)
        private const val INPUT_WIDTH = 384
        private const val INPUT_HEIGHT = 384
        private const val INPUT_CHANNELS = 3
        
        // GPU delegate 옵션
        private const val GPU_DELEGATE_OPTIONS = 0
    }
    
    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null
    private var isInitialized = false
    
    /**
     * 스타일 전이 엔진 초기화
     */
    suspend fun initialize(modelPath: String = "models/style_transfer.tflite") = withContext(Dispatchers.IO) {
        try {
            // 모델 로드
            val modelBuffer = loadModelFile(modelPath)
            
            // GPU delegate 설정 (선택사항)
            val options = Interpreter.Options().apply {
                try {
                    gpuDelegate = GpuDelegate()
                    addDelegate(gpuDelegate)
                    Log.d(TAG, "GPU delegate initialized successfully")
                } catch (e: Exception) {
                    Log.w(TAG, "GPU delegate initialization failed, using CPU", e)
                    gpuDelegate = null
                }
            }
            
            // 인터프리터 생성
            interpreter = Interpreter(modelBuffer, options)
            isInitialized = true
            
            Log.d(TAG, "Style transfer engine initialized successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize style transfer engine", e)
            throw e
        }
    }
    
    /**
     * 스타일 전이 적용
     */
    suspend fun applyStyle(
        contentBitmap: Bitmap,
        styleId: SwirlArtStyle = SwirlArtStyle.NEON
    ): Bitmap = withContext(Dispatchers.Default) {
        if (!isInitialized) {
            throw IllegalStateException("Style transfer engine not initialized")
        }
        
        try {
            // 입력 이미지 전처리
            val inputTensor = preprocessImage(contentBitmap)
            
            // 스타일 전이 실행
            val outputTensor = Array(1) { 
                Array(INPUT_HEIGHT) { 
                    Array(INPUT_WIDTH) { 
                        FloatArray(INPUT_CHANNELS) 
                    } 
                } 
            }
            
            interpreter?.run(inputTensor, outputTensor)
            
            // 출력 이미지 후처리
            val resultBitmap = postprocessImage(outputTensor[0])
            
            Log.d(TAG, "Style transfer completed for style: ${styleId.displayName}")
            return@withContext resultBitmap
            
        } catch (e: Exception) {
            Log.e(TAG, "Style transfer failed", e)
            throw e
        }
    }
    
    /**
     * 배치 스타일 전이 (성능 최적화)
     */
    suspend fun applyStyleBatch(
        bitmaps: List<Bitmap>,
        styleId: SwirlArtStyle = SwirlArtStyle.NEON
    ): List<Bitmap> = withContext(Dispatchers.Default) {
        if (!isInitialized) {
            throw IllegalStateException("Style transfer engine not initialized")
        }
        
        try {
            val results = mutableListOf<Bitmap>()
            
            // 배치 크기만큼 처리 (메모리 효율성을 위해)
            val batchSize = 4
            for (i in bitmaps.indices step batchSize) {
                val batch = bitmaps.subList(i, minOf(i + batchSize, bitmaps.size))
                
                batch.forEach { bitmap ->
                    val result = applyStyle(bitmap, styleId)
                    results.add(result)
                }
                
                // 배치 간 yield로 UI 블로킹 방지
                kotlinx.coroutines.yield()
            }
            
            return@withContext results
            
        } catch (e: Exception) {
            Log.e(TAG, "Batch style transfer failed", e)
            throw e
        }
    }
    
    /**
     * 모델 파일 로드
     */
    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    
    /**
     * 이미지 전처리 (Bitmap -> Float Array)
     */
    private fun preprocessImage(bitmap: Bitmap): Array<FloatArray> {
        // 비트맵을 모델 입력 크기로 리사이즈
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_WIDTH, INPUT_HEIGHT, true)
        
        // OpenCV Mat으로 변환
        val mat = Mat()
        Utils.bitmapToMat(resizedBitmap, mat)
        
        // BGR -> RGB 변환
        val rgbMat = Mat()
        Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_BGR2RGB)
        
        // 정규화 (0-255 -> 0-1)
        val normalizedMat = Mat()
        rgbMat.convertTo(normalizedMat, CvType.CV_32F, 1.0 / 255.0)
        
        // Float 배열로 변환
        val inputArray = Array(INPUT_HEIGHT) { FloatArray(INPUT_WIDTH * INPUT_CHANNELS) }
        normalizedMat.get(0, 0, inputArray.flatten())
        
        // 메모리 정리
        mat.release()
        rgbMat.release()
        normalizedMat.release()
        
        return inputArray
    }
    
    /**
     * 이미지 후처리 (Float Array -> Bitmap)
     */
    private fun postprocessImage(outputArray: Array<Array<FloatArray>>): Bitmap {
        // Float 배열을 OpenCV Mat으로 변환
        val outputMat = Mat(INPUT_HEIGHT, INPUT_WIDTH, CvType.CV_32FC3)
        
        for (y in 0 until INPUT_HEIGHT) {
            for (x in 0 until INPUT_WIDTH) {
                val pixel = floatArrayOf(
                    outputArray[y][x][0], // R
                    outputArray[y][x][1], // G
                    outputArray[y][x][2]  // B
                )
                outputMat.put(y, x, pixel)
            }
        }
        
        // 정규화 해제 (0-1 -> 0-255)
        val denormalizedMat = Mat()
        outputMat.convertTo(denormalizedMat, CvType.CV_8UC3, 255.0)
        
        // BGR로 변환 (OpenCV 기본 형식)
        val bgrMat = Mat()
        Imgproc.cvtColor(denormalizedMat, bgrMat, Imgproc.COLOR_RGB2BGR)
        
        // Bitmap으로 변환
        val resultBitmap = Bitmap.createBitmap(INPUT_WIDTH, INPUT_HEIGHT, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(bgrMat, resultBitmap)
        
        // 메모리 정리
        outputMat.release()
        denormalizedMat.release()
        bgrMat.release()
        
        return resultBitmap
    }
    
    /**
     * 엔진이 초기화되었는지 확인
     */
    fun isReady(): Boolean = isInitialized && interpreter != null
    
    /**
     * 리소스 정리
     */
    fun release() {
        interpreter?.close()
        gpuDelegate?.close()
        interpreter = null
        gpuDelegate = null
        isInitialized = false
        
        Log.d(TAG, "Style transfer engine released")
    }
}
