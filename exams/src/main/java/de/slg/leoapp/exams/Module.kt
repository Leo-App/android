package de.slg.leoapp.exams

import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.ui.LeoAppFeatureActivity
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.utility.PERMISSION_STUDENT

@Module("exams")
class Module : Feature {
    override fun getFeatureId(): Int {
        return R.string.feature_title
    }

    override fun getIcon(): Int {
        return R.drawable.ic_menu //TODO change
    }

    override fun getName(): Int {
        return R.string.feature_title
    }

    override fun getNecessaryPermission(): Int {
        return PERMISSION_STUDENT
    }

    override fun getEntryActivity(): Class<out LeoAppFeatureActivity> {
        return MainActivity::class.java
    }
}