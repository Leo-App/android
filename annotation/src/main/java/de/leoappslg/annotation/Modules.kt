package de.leoappslg.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class Modules(vararg val features: String, val authentication: String)