package de.slg.leoapp.ui.home

import de.slg.leoapp.ModuleLoader
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.ui.mvp.AbstractPresenter
import de.slg.leoapp.data.FeatureDataManager
import de.slg.leoapp.data.IFeatureDataManager
import de.slg.leoapp.ui.home.adapter.FeatureView

class HomePresenter : AbstractPresenter<HomeView, IFeatureDataManager>(), IHomePresenter {

    private lateinit var featureList: List<Feature>

    override fun onViewAttached(view: HomeView) {
        super.onViewAttached(view)
        registerDataManager(FeatureDataManager)

        featureList = ModuleLoader.getFeatures().toMutableList().sortedByDescending {
            getDataManager().getInteractions(it.getFeatureId()) ?: it.getFeatureId()
        }
        getMvpView().showFeatureList()
    }

    override fun onFeatureCardClicked(position: Int) {
        getMvpView().openFeatureActivity(featureList[position].getEntryActivity())
    }

    override fun onBindFeatureViewAtPosition(position: Int, holder: FeatureView) {
        holder.setIcon(featureList[position].getIcon())
        holder.setName(featureList[position].getName())
    }

    override fun getModuleCount() = ModuleLoader.getFeatures().size

}