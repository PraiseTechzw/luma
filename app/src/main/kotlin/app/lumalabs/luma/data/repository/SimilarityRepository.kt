package app.lumalabs.luma.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import app.lumalabs.luma.data.local.dao.ScanDao
import app.lumalabs.luma.data.local.entity.ScanResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class SimilarityRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scanDao: ScanDao
) {
    private var interpreter: Interpreter? = null

    init {
        try {
            val assetFileDescriptor = context.assets.openFd("mobilenet_v3_embedder.tflite")
            val inputStream = assetFileDescriptor.createInputStream()
            val modelBuffer = inputStream.readBytes()
            val byteBuffer = ByteBuffer.allocateDirect(modelBuffer.size)
            byteBuffer.order(ByteOrder.nativeOrder())
            byteBuffer.put(modelBuffer)
            interpreter = Interpreter(byteBuffer)
        } catch (e: Exception) {
            // Handle error or use a mock interpreter for now
        }
    }

    suspend fun computeEmbeddings(bitmaps: List<Bitmap>): List<FloatArray> = withContext(Dispatchers.Default) {
        bitmaps.map { bitmap ->
            val inputBuffer = preprocessImage(bitmap)
            val outputBuffer = Array(1) { FloatArray(128) }
            interpreter?.run(inputBuffer, outputBuffer)
            outputBuffer[0]
        }
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()
        val tensorImage = TensorImage.fromBitmap(bitmap)
        return imageProcessor.process(tensorImage).buffer
    }

    fun computeCosineSimilarity(vec1: FloatArray, vec2: FloatArray): Float {
        var dotProduct = 0f
        var normA = 0f
        var normB = 0f
        for (i in vec1.indices) {
            dotProduct += vec1[i] * vec2[i]
            normA += vec1[i] * vec1[i]
            normB += vec2[i] * vec2[i]
        }
        return dotProduct / (sqrt(normA) * sqrt(normB))
    }

    fun clusterPhotos(embeddings: List<Pair<String, FloatArray>>, threshold: Float = 0.92f): List<List<String>> {
        val clusters = mutableListOf<MutableList<String>>()
        val visited = mutableSetOf<String>()

        for ((uri, vec) in embeddings) {
            if (uri in visited) continue
            val cluster = mutableListOf(uri)
            visited.add(uri)

            for ((otherUri, otherVec) in embeddings) {
                if (otherUri in visited) continue
                if (computeCosineSimilarity(vec, otherVec) > threshold) {
                    cluster.add(otherUri)
                    visited.add(otherUri)
                }
            }
            if (cluster.size > 1) {
                clusters.add(cluster)
            }
        }
        return clusters
    }

    // Laplacian variance for sharpness
    fun calculateSharpness(bitmap: Bitmap): Double {
        // Mocking Laplacian for now, in real app would use OpenCV or RenderScript
        // A simple approach is calculating pixel intensity variance
        var mean = 0.0
        var variance = 0.0
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        for (pixel in pixels) {
            val gray = (0.299 * ((pixel shr 16) and 0xff) + 0.587 * ((pixel shr 8) and 0xff) + 0.114 * (pixel and 0xff))
            mean += gray
        }
        mean /= pixels.size

        for (pixel in pixels) {
            val gray = (0.299 * ((pixel shr 16) and 0xff) + 0.587 * ((pixel shr 8) and 0xff) + 0.114 * (pixel and 0xff))
            variance += (gray - mean) * (gray - mean)
        }
        return variance / pixels.size
    }

    suspend fun saveClusters(clusters: List<List<ScanResult>>) = withContext(Dispatchers.IO) {
        clusters.forEach { cluster ->
            scanDao.insertAll(cluster)
        }
    }
}
