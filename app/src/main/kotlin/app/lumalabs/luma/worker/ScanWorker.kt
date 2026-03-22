package app.lumalabs.luma.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.graphics.BitmapFactory
import android.net.Uri
import app.lumalabs.luma.data.local.dao.ScanDao
import app.lumalabs.luma.data.local.entity.ScanResult
import app.lumalabs.luma.data.repository.PhotoRepository
import app.lumalabs.luma.data.repository.SimilarityRepository
import app.lumalabs.luma.domain.model.Photo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.*

@HiltWorker
class ScanWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val photoRepository: PhotoRepository,
    private val similarityRepository: SimilarityRepository,
    private val scanDao: ScanDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val photos = photoRepository.queryAllPhotos().first()
            if (photos.isEmpty()) return Result.success()

            val dbResults = mutableListOf<ScanResult>()

            // 1. Cluster Similar Photos (Processing in chunks to avoid OOM)
            val chunkedPhotos = photos.chunked(50) // Adjust chunk size as needed
            chunkedPhotos.forEachIndexed { chunkIndex, chunk ->
                val embeddings = chunk.mapNotNull { photo ->
                    try {
                        val bitmap = loadBitmap(photo.uri)
                        if (bitmap != null) {
                            val emb = similarityRepository.computeEmbeddings(listOf(bitmap))
                            photo.uri to emb[0]
                        } else null
                    } catch (e: Exception) { null }
                }

                val clusters = similarityRepository.clusterPhotos(embeddings)
                clusters.forEachIndexed { index, clusterUris ->
                    val clusterId = "cluster_${chunkIndex}_$index"
                    
                    // Identify the best photo in the cluster based on sharpness
                    var bestUri: String? = null
                    var maxSharpness = -1.0
                    
                    val clusterResults = clusterUris.map { uri ->
                        val photo = photos.find { it.uri == uri }
                        val sharpness = try {
                            val bitmap = loadBitmap(uri)
                            if (bitmap != null) similarityRepository.calculateSharpness(bitmap) else 0.0
                        } catch (e: Exception) { 0.0 }
                        
                        if (sharpness > maxSharpness) {
                            maxSharpness = sharpness
                            bestUri = uri
                        }
                        
                        ScanResult(
                            clusterId = clusterId,
                            photoUri = uri,
                            score = 0.9, // Higher than threshold
                            isBest = false, // Will update below
                            category = "SIMILAR",
                            sizeBytes = photo?.size ?: 0L
                        )
                    }
                    
                    dbResults.addAll(clusterResults.map { it.copy(isBest = it.photoUri == bestUri) })
                }
            }
            
            // 2. Screenshots Heuristics
            photos.filter { it.name.contains("screenshot", ignoreCase = true) || it.path?.contains("screenshots", ignoreCase = true) == true }
                .forEach { photo ->
                    dbResults.add(
                        ScanResult(
                            clusterId = null,
                            photoUri = photo.uri,
                            score = 1.0,
                            isBest = false,
                            category = "SCREENSHOT",
                            sizeBytes = photo.size
                        )
                    )
                }

            // 3. Chat Media Heuristics
            val chatPaths = listOf("WhatsApp", "Telegram", "Instagram", "Messenger")
            photos.filter { photo -> 
                chatPaths.any { path -> photo.path?.contains(path, ignoreCase = true) == true } 
            }.forEach { photo ->
                dbResults.add(
                    ScanResult(
                        clusterId = null,
                        photoUri = photo.uri,
                        score = 1.0,
                        isBest = false,
                        category = "CHAT",
                        sizeBytes = photo.size
                    )
                )
            }

            scanDao.clearAll()
            scanDao.insertAll(dbResults)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun loadBitmap(uri: String): android.graphics.Bitmap? {
        return try {
            val contentUri = Uri.parse(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(contentUri)?.use { 
                BitmapFactory.decodeStream(it, null, options) 
            }
            
            options.inSampleSize = calculateInSampleSize(options, 224, 224)
            options.inJustDecodeBounds = false
            
            context.contentResolver.openInputStream(contentUri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
