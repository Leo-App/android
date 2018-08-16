package de.leoappslg.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Module(val name: String, val authentication: Boolean = false)
