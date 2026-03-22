package app.lumalabs.luma.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.lumalabs.luma.data.local.dao.ScanDao
import app.lumalabs.luma.data.local.entity.ScanResult
import app.lumalabs.luma.data.repository.PhotoRepository
import app.lumalabs.luma.data.repository.SimilarityRepository
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

            // 1. Cluster Similar Photos (Placeholder logic - ideally this is scaled)
            // For real implementation would use limited chunks due to memory
            val clusters = similarityRepository.clusterPhotos(photos.map { it.uri to floatArrayOf() }) // Mocking embeddings
            
            val dbResults = mutableListOf<ScanResult>()
            clusters.forEachIndexed { index, cluster ->
                val clusterId = "cluster_$index"
                cluster.forEach { uri ->
                    dbResults.add(
                        ScanResult(
                            clusterId = clusterId,
                            photoUri = uri,
                            score = 0.5, // Placeholder
                            isBest = false, // Placeholder
                            category = "SIMILAR"
                        )
                    )
                }
            }
            
            // 2. Screenshots Heuristics
            photos.forEach { photo ->
                if (photo.name.contains("screenshot", ignoreCase = true) || photo.path?.contains("screenshots", ignoreCase = true) == true) {
                    dbResults.add(
                        ScanResult(
                            clusterId = null,
                            photoUri = photo.uri,
                            score = 0.0,
                            isBest = false,
                            category = "SCREENSHOT",
                            sizeBytes = photo.size
                        )
                    )
                }
            }

            // 3. Chat Media Heuristics
            val chatPaths = listOf("whatsapp", "telegram", "instagram")
            photos.filter { photo -> 
                chatPaths.any { path -> photo.path?.contains(path, ignoreCase = true) == true } 
            }.forEach { photo ->
                dbResults.add(
                    ScanResult(
                        clusterId = null,
                        photoUri = photo.uri,
                        score = 0.0,
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
}
