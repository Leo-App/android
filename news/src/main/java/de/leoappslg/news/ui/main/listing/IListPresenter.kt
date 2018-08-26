package de.leoappslg.news.ui.main.listing

import de.leoappslg.news.ui.main.listing.adapter.IEntryView

interface IListPresenter {
    fun onCardClick(index: Int)
    fun getEntryCount(): Int
    fun onBindEntryViewAtPosition(position: Int, holder: IEntryView)
}