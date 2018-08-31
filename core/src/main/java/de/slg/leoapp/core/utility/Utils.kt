@file:Suppress("unused", "WeakerAccess")

package de.slg.leoapp.core.utility

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.telephony.TelephonyManager as T
import androidx.annotation.DrawableRes
import de.slg.leoapp.core.datastructure.List
import de.slg.leoapp.core.datastructure.Stack
import de.slg.leoapp.core.modules.MenuEntry
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.utility.exception.APIKeyAlgorithmNotValidException
import de.slg.leoapp.core.utility.exception.ActivityTypeAlreadyRegisteredException
import de.slg.leoapp.core.utility.exception.ActivityTypeNotRegisteredException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

//TODO add Javadoc for all core classes and public methods/functions
abstract class Utils {

    abstract class Activity {
        companion object Manager { //Named companion object for java interoperability. Java classes call Activity.Manager.someMethod()
            private val openActivities: Stack<String> = Stack()
            private var profileActivity: Class<*>? = null
            private var settingsActivity: Class<*>? = null

            fun registerActivity(tag: String) {
                openActivities.add(tag)
            }

            fun unregisterActivity(tag: String) {
                if (tag == openActivities.getContent()) {
                    openActivities.remove()
                }
            }

            fun registerProfileActivity(profile: Class<out LeoAppFeatureActivity>) {
                if (profileActivity != null)
                    throw ActivityTypeAlreadyRegisteredException("A profile activity is already registered")

                profileActivity = profile
            }

            fun registerSettingsActivity(settings: Class<out LeoAppFeatureActivity>) {
                if (settingsActivity != null)
                    throw ActivityTypeAlreadyRegisteredException("A settings activity is already registered")

                settingsActivity = settings
            }

            fun getSettingsReference(): Class<*> {
                if (settingsActivity == null)
                    throw ActivityTypeNotRegisteredException("""Trying to access non registered activity "Settings"""")

                return settingsActivity!!
            }

            fun getProfileReference(): Class<*> {
                if (profileActivity == null)
                    throw ActivityTypeNotRegisteredException("""Trying to access non registered activity "Profile"""")

                return profileActivity!!
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

    abstract class Network {
        companion object Connectivity {

            private lateinit var apiKeyAlgorithm: KFunction<String>

            fun registerAPIKeyAlgorithm(algorithm: KFunction<String>) {
                apiKeyAlgorithm = algorithm
                for (cur in apiKeyAlgorithm.parameters) {
                    if (cur.isOptional || cur.type.isMarkedNullable)
                        continue

                    if (cur.type.toString() != "Context")
                        throw APIKeyAlgorithmNotValidException("The provided API Key algorithm has an invalid signature")
                }
            }

            fun getAPIKey(context: Context): String {
                val parameterMap = mutableMapOf<KParameter, Any?>()

                for (cur in apiKeyAlgorithm.parameters) {
                    if (cur.isOptional) continue

                    if (cur.type.isMarkedNullable) {
                        parameterMap[cur] = null
                        continue
                    }

                    parameterMap[cur] = context
                }

                return apiKeyAlgorithm.callBy(parameterMap)
            }

            /**
             * Prüft, ob das aktuelle Gerät mit dem Internet verbunden ist.
             *
             * @return true, falls eine aktive Netzwerkverbindung besteht; false, falls nicht
             * @see getNetworkPerformance
             */
            fun isNetworkAvailable(context: Context): Boolean {
                val c = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val n = c.activeNetworkInfo
                return n.isConnected
            }


            /**
             * Gibt die Geschwindigkeit der aktuellen Internetverbindung zurück.
             *
             * @return Aktuelle Netzwerkperformance, NOT_AVAILABLE wenn kein Internet verfügbar.
             */
            @Suppress("DEPRECATION")
            fun getNetworkPerformance(context: Context): NetworkPerformance {
                val c = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val info = c.activeNetworkInfo ?: return NetworkPerformance.NOT_AVAILABLE

                if (info.type == ConnectivityManager.TYPE_WIFI)
                    return NetworkPerformance.EXCELLENT

                if (info.type == ConnectivityManager.TYPE_MOBILE) {
                    return when (info.subtype) {
                        T.NETWORK_TYPE_GPRS, T.NETWORK_TYPE_EDGE,
                        T.NETWORK_TYPE_CDMA, T.NETWORK_TYPE_1xRTT,
                        T.NETWORK_TYPE_IDEN -> NetworkPerformance.INSUFFICIENT
                        T.NETWORK_TYPE_UMTS, T.NETWORK_TYPE_EVDO_0,
                        T.NETWORK_TYPE_EVDO_A, T.NETWORK_TYPE_HSDPA,
                        T.NETWORK_TYPE_HSUPA, T.NETWORK_TYPE_HSPA,
                        T.NETWORK_TYPE_EVDO_B, T.NETWORK_TYPE_EHRPD,
                        T.NETWORK_TYPE_HSPAP -> NetworkPerformance.MEDIOCRE
                        T.NETWORK_TYPE_LTE -> NetworkPerformance.EXCELLENT
                        else -> if (isNetworkAvailable(context)) NetworkPerformance.INSUFFICIENT else NetworkPerformance.NOT_AVAILABLE
                    }
                }

                return if (isNetworkAvailable(context)) NetworkPerformance.INSUFFICIENT else NetworkPerformance.NOT_AVAILABLE
            }

        }

        enum class NetworkPerformance { EXCELLENT, INSUFFICIENT, NOT_AVAILABLE, MEDIOCRE }

    }

}
