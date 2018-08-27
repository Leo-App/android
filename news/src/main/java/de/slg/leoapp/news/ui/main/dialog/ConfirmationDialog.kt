package de.slg.leoapp.news.ui.main.dialog

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import de.slg.leoapp.news.R
import de.slg.leoapp.news.ui.main.NewsPresenter

class ConfirmationDialog(private val presenter: NewsPresenter, context: Context) : AlertDialog(context), IDialogView {
//todo
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_confirm)
        super.onCreate(savedInstanceState)
    }

    override fun getViewContext(): Context {
        TODO("not implemented")
    }

}