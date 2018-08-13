@file:Suppress("unused", "CanBePrimaryConstructorProperty")

package de.leoappslg.core.utility

const val PERMISSION_UNVERIFIZIERT = 0
const val PERMISSION_SCHUELER = 1
const val PERMISSION_LEHRER = 2
const val PERMISSION_ADMIN = 3

class User(val id: Int, val name: String, val klasse: String, val permission: Int, val defaultname: String)
