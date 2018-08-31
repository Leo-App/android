package de.slg.leoapp.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.slg.leoapp.core.R
import de.slg.leoapp.core.modules.MenuEntry
import de.slg.leoapp.core.utility.Utils

/**
 * @author Moritz
 * @since 2.0.0
 */
class BottomNavigationDrawer(private val highlightedItem: Int) : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.app_bottom_navigation_drawer, container, false)

        val menuWrapper: LinearLayout = v.findViewById(R.id.bottomNavigationMenu)

        for (menuEntry: MenuEntry in Utils.Menu.getEntries()) {
            val item = inflater.inflate(
                    if (menuEntry.getId() == highlightedItem)
                        R.layout.bottom_navigation_item_highlighted
                    else
                        R.layout.bottom_navigation_item,
                    menuWrapper,
                    false
            )

            val icon: ImageView = item.findViewById(R.id.icon)
            val title: TextView = item.findViewById(R.id.featureTitle)

            icon.setImageResource(menuEntry.getIcon())
            title.text = menuEntry.getTitle()

            item.setOnClickListener { startActivity(menuEntry.getIntent(context!!)) }

            menuWrapper.addView(item)
        }

        return v
    }
}