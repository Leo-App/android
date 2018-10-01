package de.slg.leoapp.substitutions

import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.modules.Feature
import de.slg.leoapp.core.utility.PERMISSION_STUDENT
import de.slg.leoapp.substitutions.ui.SubstitutionsActivity

@Module("substitutions")
class Module : Feature {
    override fun getIcon() = R.drawable.ic_substitution

    override fun getName() = R.string.substitutions_feature_title

    override fun getFeatureId() = R.string.substitutions_feature_title

    override fun getNecessaryPermission() = PERMISSION_STUDENT

    override fun getEntryActivity() = SubstitutionsActivity::class.java
}