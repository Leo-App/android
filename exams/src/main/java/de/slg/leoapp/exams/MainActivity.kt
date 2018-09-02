package de.slg.leoapp.exams

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import de.slg.leoapp.core.ui.LeoAppFeatureActivity

class MainActivity : LeoAppFeatureActivity() {
    override fun getContentView(): Int {
        return R.layout.activity_main
    }

    override fun getNavigationHighlightId(): Int {
        return R.string.feature_title_exams
    }

    override fun getActivityTag(): String {
        return "feature_exams_main"
    }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)

        initListView()
    }

    private fun initListView() {
        val listView: ListView = findViewById(R.id.listView)

        listView.adapter = KlausurListAdapter(applicationContext, Array(5) {"Test"})
    }
}

class KlausurListAdapter(context: Context, data: Array<String>) : ArrayAdapter<String>(context, R.layout.bottom_navigation_item, R.id.featureTitle, data) {

}