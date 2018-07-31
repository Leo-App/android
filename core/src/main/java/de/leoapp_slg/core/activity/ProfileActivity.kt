package de.leoapp_slg.core.activity

class ProfileActivity : LeoAppNavActivity() {
    override fun getContentView(): Int {
        return -1
    }

    override fun getDrawerLayoutId(): Int {
        return -1
    }

    override fun getNavigationId(): Int {
        return -1
    }

    override fun getToolbarId(): Int {
        return -1
    }

    override fun getToolbarTextId(): Int {
        return -1
    }

    override fun getNavigationHighlightId(): Int {
        return -1
    }

    override fun getActivityTag(): String {
        return "Profile"
    }
}