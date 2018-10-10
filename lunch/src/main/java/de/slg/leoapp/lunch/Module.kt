package de.slg.leoapp.lunch

import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.modules.Notification
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.utility.PERMISSION_STUDENT
import de.slg.leoapp.lunch.ui.qr.MainActivity

@Module("lunch")
class Module : Feature {

    override fun getIcon() = R.drawable.ic_feature_lunch

    override fun getName() = R.string.lunch_feature_name

    override fun getFeatureId() = R.string.lunch_feature_name

    override fun getNecessaryPermission() = PERMISSION_STUDENT

    override fun getEntryActivity(): Class<out LeoAppFeatureActivity> {
        return MainActivity::class.java //TODO show login activity when not logged in
    }

    override fun getNotification(): Notification? {
        return Notification(R.string.lunch_notification_description, "preference_key_lunch_notification")
    }
}