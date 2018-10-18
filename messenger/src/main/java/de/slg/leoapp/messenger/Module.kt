package de.slg.leoapp.messenger

import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.modules.Notification
import de.slg.leoapp.core.utility.PERMISSION_STUDENT
import de.slg.leoapp.messenger.ui.MainActivity

@Module("messenger")
class Module : Feature {
    override fun getIcon() = R.drawable.ic_feature_messenger

    override fun getName() = R.string.messenger_feature_name

    override fun getFeatureId() = R.string.messenger_feature_name

    override fun getNecessaryPermission() = PERMISSION_STUDENT

    override fun getEntryActivity() = MainActivity::class.java

    override fun getNotification(): Notification? {
        return null
    }
}