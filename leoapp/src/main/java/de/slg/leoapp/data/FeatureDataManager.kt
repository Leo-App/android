package de.slg.leoapp.data

object FeatureDataManager: IFeatureDataManager {

    private lateinit var featureStatistics: Map<Int, FeatureStatistics>

    override fun syncUsageStatistics(callback: () -> Unit) {
        featureStatistics = emptyMap()//todo
        callback()
    }

    override fun getInteractions(featureId: Int) = featureStatistics[featureId]?.interactions

    override fun getAverageTime(featureId: Int) = featureStatistics[featureId]?.averageTimeSpent

    data class FeatureStatistics(val interactions: Int, val averageTimeSpent: Float)

}