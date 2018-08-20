@file:Suppress("unused", "WeakerAccess")

package de.slg.leoapp.core.utility

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.datastructure.List
import de.slg.leoapp.core.datastructure.Stack
import de.slg.leoapp.core.modules.MenuEntry

abstract class Utils {

    abstract class Activity {
        companion object {
            private val openActivities: Stack<String> = Stack()

            fun registerActivity(tag: String) {
                openActivities.add(tag)
            }

            fun unregisterActivity(tag: String) {
                if (tag == openActivities.getContent()) {
                    openActivities.remove()
                }
            }
        }
    }

    abstract class Menu {
        companion object {
            private val menuEntries: List<MenuEntry> = List()

            fun addMenuEntry(id: Int, title: String, @DrawableRes icon: Int, activity: Class<out LeoAppFeatureActivity>) {
                menuEntries.append(object : MenuEntry {
                    override fun getId() = id
                    override fun getTitle() = title
                    override fun getIcon() = icon
                    override fun getIntent(context: Context) = Intent(context, activity)
                })
            }

            fun getEntries(): List<MenuEntry> {
                val list: List<MenuEntry> = List()
                for (entry: MenuEntry in menuEntries) {
                    list.append(entry)
                }
                return list
            }
        }
    }
}
