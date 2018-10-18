package de.slg.leoapp.core.ui.mvp

import androidx.annotation.CallSuper

abstract class AbstractPresenter<V: Any, D: Any> {

    private lateinit var view: V
    private lateinit var dataManager: D

    @CallSuper
    open fun onViewAttached(view: V) {
        this.view = view
    }

    fun registerDataManager(dm: D) {
        dataManager = dm
    }

    fun getMvpView(): V {
        checkViewAttached()
        return view
    }

    fun getDataManager(): D {
        checkDataManagerAvailable()
        return dataManager
    }

    private fun checkViewAttached() {
        if (!::view.isInitialized)
            throw ViewNotAttachedException("You need to attach a view before using the presenter")
    }

    private fun checkDataManagerAvailable() {
        if (!::dataManager.isInitialized)
            throw DataManagerNotAccessibleException("You need to register a data manager before using the presenter")
    }

    class ViewNotAttachedException(desc: String) : RuntimeException(desc)
    class DataManagerNotAccessibleException(desc: String) : RuntimeException(desc)

}