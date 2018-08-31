package de.slg.leoapp.news.ui.main.add

import de.slg.leoapp.core.ui.mvp.MVPView

interface IAddView : MVPView {
    fun getEnteredTitle(): String
    fun getEnteredContent(): String
    fun getSelectedRecipient(): String
    fun getSelectedDeadline(): String
}