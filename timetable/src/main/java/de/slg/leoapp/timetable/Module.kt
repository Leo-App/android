package de.slg.leoapp.timetable

import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.utility.PERMISSION_STUDENT

/**
 * @author Moritz
 * Erstelldatum: 13.09.2018
 */
class Module : Feature {
    override fun getFeatureId() = R.string.feature_title_timetable

    override fun getIcon() = R.drawable.ic_stundenplan

    override fun getName() = R.string.feature_title_timetable

    override fun getNecessaryPermission() = PERMISSION_STUDENT

    override fun getEntryActivity() = MainActivity::class.java
}