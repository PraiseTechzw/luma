package app.lumalabs.luma.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "luma_prefs")

@Singleton
class PreferenceRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    private val WEEKLY_SCAN = booleanPreferencesKey("weekly_scan")
    private val AUTO_TRASH = booleanPreferencesKey("auto_trash")
    private val MIN_GROUP_SIZE = intPreferencesKey("min_group_size")
    private val SIMILARITY_THRESHOLD = floatPreferencesKey("similarity_threshold")

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }
    val weeklyScan: Flow<Boolean> = context.dataStore.data.map { it[WEEKLY_SCAN] ?: true }
    val autoTrash: Flow<Boolean> = context.dataStore.data.map { it[AUTO_TRASH] ?: false }
    val minGroupSize: Flow<Int> = context.dataStore.data.map { it[MIN_GROUP_SIZE] ?: 2 }
    val similarityThreshold: Flow<Float> = context.dataStore.data.map { it[SIMILARITY_THRESHOLD] ?: 0.92f }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setWeeklyScan(enabled: Boolean) {
        context.dataStore.edit { it[WEEKLY_SCAN] = enabled }
    }

    suspend fun setAutoTrash(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_TRASH] = enabled }
    }

    suspend fun setMinGroupSize(size: Int) {
        context.dataStore.edit { it[MIN_GROUP_SIZE] = size }
    }

    suspend fun setSimilarityThreshold(threshold: Float) {
        context.dataStore.edit { it[SIMILARITY_THRESHOLD] = threshold }
    }
}
