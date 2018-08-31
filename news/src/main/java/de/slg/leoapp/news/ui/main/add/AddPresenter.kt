package de.slg.leoapp.news.ui.main.add

import de.slg.leoapp.core.ui.mvp.AbstractPresenter
import de.slg.leoapp.core.utility.User
import de.slg.leoapp.news.data.INewsDataManager
import de.slg.leoapp.news.data.NewsDataManager
import java.text.SimpleDateFormat
import java.util.*

class AddPresenter : AbstractPresenter<IAddView, INewsDataManager>(), IAddPresenter {

    init {
        registerDataManager(NewsDataManager)
    }

    override fun onAddFinished(callback: (Boolean) -> Unit) {
        //todo check if all fields are filled and valid

        with(getMvpView()) {
            val date = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).parse(getSelectedDeadline())

            getDataManager().addEntry(
                    User(getViewContext()).id,
                    getEnteredTitle(),
                    getEnteredContent(),
                    getSelectedRecipient(),
                    date,
                    getViewContext(),
                    callback)
        }
    }
}