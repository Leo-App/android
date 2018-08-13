package de.leoappslg.authentication

import de.leoappslg.annotation.Module
import de.leoappslg.core.modules.Authentication
import de.leoappslg.core.modules.Input

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