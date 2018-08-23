package de.leoappslg.news

import de.leoappslg.news.ui.main.MainActivity
import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.utility.PERMISSION_STUDENT
import de.slg.leoapp.news.R

@Module("news")
class Module : Feature {
    override fun getIcon() = R.drawable.temp_feature_icon

    override fun getName() = R.string.feature_title

    override fun getFeatureId() = 0xdefa12

    override fun getNecessaryPermission() = PERMISSION_STUDENT

    override fun getEntryActivity() = MainActivity::class.java
}