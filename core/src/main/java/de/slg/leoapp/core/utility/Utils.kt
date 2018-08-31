@file:Suppress("unused", "WeakerAccess")

package de.slg.leoapp.core.utility

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.datastructure.List
import de.slg.leoapp.core.datastructure.Stack
import de.slg.leoapp.core.modules.MenuEntry
import de.slg.leoapp.core.utility.exception.ActivityTypeAlreadyRegisteredException
import de.slg.leoapp.core.utility.exception.ActivityTypeNotRegisteredException
//TODO add Javadoc for all core classes and public methods/functions
abstract class Utils {

    abstract class Activity {
        companion object Manager { //Named companion object for java interoperability. Java classes call Activity.Manager.someMethod()
            private val openActivities: Stack<String> = Stack()
            private lateinit var profileActivity: Class<out LeoAppFeatureActivity>
            private lateinit var settingsActivity: Class<out LeoAppFeatureActivity>

            fun registerActivity(tag: String) {
                openActivities.add(tag)
            }

            fun unregisterActivity(tag: String) {
                if (tag == openActivities.getContent()) {
                    openActivities.remove()
                }
            }

            fun registerProfileActivity(profile: Class<out LeoAppFeatureActivity>) {
//                if (::profileActivity.isInitialized)
//                    throw ActivityTypeAlreadyRegisteredException("A profile activity is already registered")

                profileActivity = profile
            }

            fun registerSettingsActivity(settings: Class<out LeoAppFeatureActivity>) {
//                if (::settingsActivity.isInitialized)
//                    throw ActivityTypeAlreadyRegisteredException("A settings activity is already registered")

                settingsActivity = settings
            }

            fun getSettingsReference(): Class<*> {
                if (!::settingsActivity.isInitialized)
                    throw ActivityTypeNotRegisteredException("""Trying to access non registered activity "Settings"""")

                return settingsActivity
            }

            fun getProfileReference(): Class<*> {
                if (!::profileActivity.isInitialized)
                    throw ActivityTypeNotRegisteredException("""Trying to access non registered activity "Profile"""")

                return profileActivity
            }
        }
    }

    abstract class Menu {
        companion object Manager {
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
