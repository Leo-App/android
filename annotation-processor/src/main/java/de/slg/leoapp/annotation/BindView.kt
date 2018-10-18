package de.slg.leoapp.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class BindView(val viewid: Int)