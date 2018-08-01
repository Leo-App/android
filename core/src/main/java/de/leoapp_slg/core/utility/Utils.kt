@file:Suppress("unused", "WeakerAccess")

package de.leoapp_slg.core.utility

abstract class Utils {
    abstract class User {
        companion object {
            fun getID(): Int {
                return 0
            }

            fun getPermission(): Int {
                return 0
            }

            fun getName(): String {
                return ""
            }

            fun getDefaultname(): String {
                return ""
            }

            fun getKlasse(): String {
                return ""
            }
        }

    }
}
