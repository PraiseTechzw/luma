package app.lumalabs.luma.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import app.lumalabs.luma.data.local.dao.ScanDao
import app.lumalabs.luma.data.repository.PhotoRepository
import app.lumalabs.luma.worker.ScanWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scanDao: ScanDao,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    val reclaimableSize = scanDao.getTotalReclaimableSize()
        .map { it ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0L)

    val similarClusters = scanDao.getResultsByCategory("SIMILAR")
        .map { list -> list.filter { it.clusterId != null }.groupBy { it.clusterId!! } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    fun getResultsByCategory(category: String): Flow<List<ScanResult>> = 
        scanDao.getResultsByCategory(category)

    fun trashPhotos(uris: List<android.net.Uri>, onTrashRequest: (android.app.PendingIntent) -> Unit) {
        viewModelScope.launch {
            if (uris.isNotEmpty()) {
                val pendingIntent = photoRepository.createTrashRequest(uris)
                onTrashRequest(pendingIntent)
            }
        }
    }

    val similarSize = scanDao.getCategoryTotalSize("SIMILAR")
        .map { formatSize(it ?: 0L) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "0 MB")

    val screenshotsCount = scanDao.getCategoryCount("SCREENSHOT")
        .map { "$it items" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "0 items")

    val chatSize = scanDao.getCategoryTotalSize("CHAT")
        .map { formatSize(it ?: 0L) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "0 MB")

    private val workManager = WorkManager.getInstance(context)
    
    val isScanning = workManager.getWorkInfosByTagFlow("scanning_tag")
        .map { list -> list.any { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun triggerScan() {
        val workRequest = OneTimeWorkRequestBuilder<ScanWorker>()
            .addTag("scanning_tag")
            .build()
        workManager.enqueueUniqueWork("scan_all", ExistingWorkPolicy.REPLACE, workRequest)
    }

    fun keepBest(clusterId: String, onTrashRequest: (android.app.PendingIntent, List<android.net.Uri>) -> Unit) {
        viewModelScope.launch {
            val clusterResults = scanDao.getCluster(clusterId)
            val urisToTrash = clusterResults.filter { !it.isBest }.map { android.net.Uri.parse(it.photoUri) }
            if (urisToTrash.isNotEmpty()) {
                val pendingIntent = photoRepository.createTrashRequest(urisToTrash)
                onTrashRequest(pendingIntent, urisToTrash)
            }
        }
    }

    fun removeResults(uris: List<android.net.Uri>) {
        viewModelScope.launch {
            uris.forEach { uri ->
                scanDao.deleteByUri(uri.toString())
            }
        }
    }

    private fun formatSize(bytes: Long): String {
        return if (bytes > 1024 * 1024 * 1024) {
            String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024))
        } else {
            "${bytes / (1024 * 1024)} MB"
        }
    }
}
