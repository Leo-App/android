package de.slg.leoapp.authentication

import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.modules.Authentication
import de.slg.leoapp.core.modules.Input

@Module("authentication", true)
class Module : Authentication {

    private var error: Int? = null

    override fun validateInput(vararg inputs: String): Boolean {
        TODO("not implemented")
    }

    override fun getErrorMessage(): Int? {
        return error
    }

    override fun getNecessaryInput(): Int {
        return Input.USERNAME + Input.PASSWORD
    }

    override fun getAPIKey(): String {
        TODO("not implemented")
    }

    override fun getDeviceListingEnabled(): Boolean {
        TODO("not implemented")
    }
}