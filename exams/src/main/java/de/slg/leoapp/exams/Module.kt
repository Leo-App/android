package de.slg.leoapp.exams

import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.modules.Notification
import de.slg.leoapp.core.utility.PERMISSION_STUDENT

@Module("exams")
class Module : Feature {
    override fun getFeatureId() = R.string.exams_feature_title

    override fun getIcon() = R.drawable.ic_klausurplan

    override fun getName() = R.string.exams_feature_title

    override fun getNecessaryPermission() = PERMISSION_STUDENT

    override fun getEntryActivity() = MainActivity::class.java

    override fun getNotification(): Notification? {
        return Notification(R.string.exams_notification_description, "preference_key_notification_exams")
    }
}