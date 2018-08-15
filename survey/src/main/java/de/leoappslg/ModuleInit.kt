package de.leoappslg

import de.leoappslg.annotation.Module
import de.leoappslg.core.activity.LeoAppFeatureActivity
import de.leoappslg.core.modules.Feature

@Module("survey")
class ModuleInit : Feature {
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