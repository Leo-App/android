package de.slg.leoapp.news.ui.main.listing

import de.slg.leoapp.news.ui.main.listing.adapter.IEntryView

interface IListPresenter {
    fun onCardClick(index: Int)
    fun getEntryCount(): Int
    fun onBindEntryViewAtPosition(position: Int, holder: IEntryView)
}