package app.lumalabs.luma.di

import android.content.Context
import androidx.room.Room
import app.lumalabs.luma.data.local.LumaDatabase
import app.lumalabs.luma.data.local.dao.ScanDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): LumaDatabase {
        return Room.databaseBuilder(
            context,
            LumaDatabase::class.java,
            "luma_db"
        ).build()
    }

    @Provides
    fun provideScanDao(database: LumaDatabase): ScanDao {
        return database.scanDao
    }
}
