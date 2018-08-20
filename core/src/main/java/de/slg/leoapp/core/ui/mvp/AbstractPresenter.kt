package de.slg.leoapp.core.ui.mvp

import androidx.annotation.CallSuper

abstract class AbstractPresenter<V> {

    private var view: V? = null

    @CallSuper
    open fun onViewAttached(view: V) {
        this.view = view
    }

    fun getMvpView(): V? = view

    fun checkViewAttached() {
        if (view == null)
            throw ViewNotAttachedException("You need to attach a view before using the presenter")
    }

    class ViewNotAttachedException(desc: String) : RuntimeException(desc)

}