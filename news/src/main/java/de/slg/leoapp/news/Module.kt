package de.slg.leoapp.news

import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.modules.Notification
import de.slg.leoapp.core.utility.PERMISSION_STUDENT
import de.slg.leoapp.news.ui.main.MainActivity

@Module("news")
class Module : Feature {
    override fun getIcon() = R.drawable.ic_pin

    override fun getName() = R.string.news_feature_title

    override fun getFeatureId() = 0xdefa12

    override fun getNecessaryPermission() = PERMISSION_STUDENT

    override fun getEntryActivity() = MainActivity::class.java

    override fun getNotification(): Notification? {
        return Notification(R.string.news_desc_notification, "preference_key_news_notification")
    }

}