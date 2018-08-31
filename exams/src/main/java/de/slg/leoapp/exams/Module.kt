package de.slg.leoapp.exams

import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.utility.PERMISSION_STUDENT

@Module("exams")
class Module : Feature {
    override fun getFeatureId() = R.string.feature_title_exams

    override fun getIcon() = R.drawable.ic_klausurplan //TODO change

    override fun getName() = R.string.feature_title_exams

    override fun getNecessaryPermission() = PERMISSION_STUDENT

    override fun getEntryActivity() = MainActivity::class.java
}