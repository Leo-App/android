package de.slg.leoapp.data

import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

object FeatureDataManager : IFeatureDataManager {
    private lateinit var featureStatistics: Map<Int, FeatureStatistics>

    override fun syncUsageStatistics(callback: () -> Unit) {
        launch(UI) {
            delay(6000)
            featureStatistics = emptyMap() //todo
            callback()
        }
    }

    override fun getInteractions(featureId: Int) = featureStatistics[featureId]?.interactions

    override fun getAverageTime(featureId: Int) = featureStatistics[featureId]?.averageTimeSpent

    data class FeatureStatistics(val interactions: Int, val averageTimeSpent: Float)

}