package de.slg.leoapp.exams

import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.activity.LeoAppFeatureActivity
import de.slg.leoapp.core.modules.Feature

@Module("exams")
class Module : Feature {
    override fun getFeatureId(): Int {
        TODO("not implemented")
    }

    override fun getIcon(): Int {
        TODO("not implemented")
    }

    override fun getName(): String {
        TODO("not implemented")
    }

    override fun getNecessaryPermission(): Int {
        TODO("not implemented")
    }

    override fun getEntryActivity(): Class<out LeoAppFeatureActivity> {
        TODO("not implemented")
    }
}