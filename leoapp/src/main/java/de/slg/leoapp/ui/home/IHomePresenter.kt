package de.slg.leoapp.ui.home

import de.slg.leoapp.ui.home.adapter.FeatureView

interface IHomePresenter {
    fun onFeatureCardClicked(position: Int)
    fun getModuleCount(): Int
    fun onBindFeatureViewAtPosition(position: Int, holder: FeatureView)
}