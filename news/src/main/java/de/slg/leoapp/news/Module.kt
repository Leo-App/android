package de.slg.leoapp.news

import de.slg.leoapp.news.ui.main.MainActivity
import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.utility.PERMISSION_STUDENT

@Module("news")
class Module : Feature {
    override fun getIcon() = R.drawable.ic_pin

    override fun getName() = R.string.feature_title_news

    override fun getFeatureId() = 0xdefa12

    override fun getNecessaryPermission() = PERMISSION_STUDENT

    override fun getEntryActivity() = MainActivity::class.java
}