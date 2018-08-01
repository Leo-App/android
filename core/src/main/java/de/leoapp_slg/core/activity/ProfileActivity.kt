package de.leoapp_slg.core.activity

class ProfileActivity : LeoAppFeatureActivity() {
    override fun getActivityTag(): String {
        return "Profile"
    }

    override fun getContentView(): Int {
        return -1
    }

    override fun getDrawerLayoutId(): Int {
        return -1
    }

    override fun getNavigationViewId(): Int {
        return -1
    }

    override fun getToolbarViewId(): Int {
        return -1
    }

    override fun getProgressBarId(): Int {
        return -1
    }

    override fun getToolbarTextId(): Int {
        return -1
    }

    override fun getNavigationHighlightId(): Int {
        return -1
    }
}