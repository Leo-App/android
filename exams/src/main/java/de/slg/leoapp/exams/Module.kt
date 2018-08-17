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
        return R.drawable.leo_app_icon
    }

    override fun getName(): String {
        return "Klausurplan"
    }

    override fun getNecessaryPermission(): Int {
        return PERMISSION_SCHUELER
    }

    override fun getEntryActivity(): Class<out LeoAppFeatureActivity> {
        return MainActivity::class.java
    }
}