package de.slg.leoapp.data

interface IFeatureDataManager {
    fun syncUsageStatistics(callback: () -> Unit)
    fun getInteractions(featureId: Int): Int?
    fun getAverageTime(featureId: Int): Float?
}