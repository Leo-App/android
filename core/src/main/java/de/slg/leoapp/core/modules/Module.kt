package de.slg.leoapp.core.modules

interface Module {
    fun getNotification(): Notification? = null
    fun hasNotification() = getNotification() != null
}