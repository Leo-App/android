package de.leoappslg.news.ui.listing

interface INewsPresenter {
    fun onCardClick(index: Int)
    fun onFABPressed()
    fun onSettingsPressed()
    fun onDeletePressed()
    fun onProfilePressed()
    fun onNavigationPressed()
}