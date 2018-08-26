package de.slg.leoapp.exams

import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.activity.LeoAppFeatureActivity
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.utility.PERMISSION_SCHUELER

@Module("exams")
class Module : Feature {
    override fun getFeatureId(): Int {
        return R.string.FeatureTitle
    }

    override fun getIcon(): Int {
        return R.drawable.ic_menu //TODO change
    }

    override fun getName(): Int {
        return R.string.feature_title
    }

    override fun getNecessaryPermission(): Int {
        return PERMISSION_SCHUELER
    }

    override fun getEntryActivity(): Class<out LeoAppFeatureActivity> {
        return MainActivity::class.java
    }
}