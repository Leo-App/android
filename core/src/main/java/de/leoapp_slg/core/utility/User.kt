@file:Suppress("unused", "CanBePrimaryConstructorProperty")

package de.leoapp_slg.core.utility

const val PERMISSION_UNVERIFIZIERT = 0
const val PERMISSION_SCHUELER = 1
const val PERMISSION_LEHRER = 2
const val PERMISSION_ADMIN = 3

class User(id: Int, name: String, klasse: String, permission: Int, defaultname: String) {

    /**
     * Eindeutige User-ID
     */
    val id: Int = id

    /**
     * Änderbarer Benutzername
     */
    val name: String = name

    /**
     * Benutzername des pädagogischen Netzwerks
     */
    val defaultname: String = defaultname

    /**
     * Aktuelle Stufe des Users
     */
    val klasse: String = klasse

    /**
     * User Permission-Level
     */
    val permission: Int = permission
}
