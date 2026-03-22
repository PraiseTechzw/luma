package app.lumalabs.luma.data.local.dao

import androidx.room.*
import app.lumalabs.luma.data.local.entity.ScanResult
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Query("SELECT * FROM scan_results WHERE category = :category")
    fun getResultsByCategory(category: String): Flow<List<ScanResult>>

    @Query("SELECT * FROM scan_results WHERE clusterId = :clusterId")
    suspend fun getCluster(clusterId: String): List<ScanResult>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<ScanResult>)

    @Query("DELETE FROM scan_results WHERE photoUri = :uri")
    suspend fun deleteByUri(uri: String)

    @Query("DELETE FROM scan_results")
    suspend fun clearAll()

    @Query("SELECT SUM(sizeBytes) FROM scan_results WHERE isBest = 0 AND category = 'SIMILAR'")
    fun getTotalReclaimableSize(): Flow<Long?>

    @Query("SELECT SUM(sizeBytes) FROM scan_results WHERE category = :category")
    fun getCategoryTotalSize(category: String): Flow<Long?>

    @Query("SELECT COUNT(*) FROM scan_results WHERE category = :category")
    fun getCategoryCount(category: String): Flow<Int>
}
