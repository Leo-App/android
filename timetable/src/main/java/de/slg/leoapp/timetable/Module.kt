package de.slg.leoapp.timetable

import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.utility.PERMISSION_STUDENT

@Module("timetable")
class Module : Feature {
    override fun getFeatureId() = R.string.timetable_feature_title

    override fun getIcon() = R.drawable.ic_stundenplan

    override fun getName() = R.string.timetable_feature_title

    override fun getNecessaryPermission() = PERMISSION_STUDENT

    override fun getEntryActivity() = MainActivity::class.java
}