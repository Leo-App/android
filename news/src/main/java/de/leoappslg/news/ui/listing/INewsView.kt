package de.leoappslg.news.ui.listing

interface INewsView {
    fun openNewEntryDialog()
    fun enableTextViewEditing()
    fun openDatePicker()
    fun showEntry()
    fun returnToListing()
    fun deleteEntry()
    fun openProfileActivity()
    fun openSettings()
}