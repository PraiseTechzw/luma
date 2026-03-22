package app.lumalabs.luma.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import app.lumalabs.luma.data.local.dao.ScanDao
import app.lumalabs.luma.data.local.entity.ScanResult

@Database(entities = [ScanResult::class], version = 1, exportSchema = false)
abstract class LumaDatabase : RoomDatabase() {
    abstract val scanDao: ScanDao
}
