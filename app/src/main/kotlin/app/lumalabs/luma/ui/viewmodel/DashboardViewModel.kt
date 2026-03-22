package app.lumalabs.luma.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import app.lumalabs.luma.data.local.dao.ScanDao
import app.lumalabs.luma.worker.ScanWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scanDao: ScanDao
) : ViewModel() {

    val reclaimableSize = scanDao.getTotalReclaimableSize()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0L)

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    fun triggerScan() {
        _isScanning.value = true
        val workRequest = OneTimeWorkRequestBuilder<ScanWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
        // Note: For real app, would observe work info and set scanning status
    }
}
