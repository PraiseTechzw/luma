package app.lumalabs.luma.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class ScanResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clusterId: String?,
    val photoUri: String,
    val score: Double,
    val isBest: Boolean,
    val category: String, // SIMILAR, SCREENSHOT, CHAT
    val dateScanned: Long = System.currentTimeMillis(),
    val sizeBytes: Long = 0,
    val labels: String? = null // For screenshot categorization
)
