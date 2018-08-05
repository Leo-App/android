package de.leoappslg

import de.leoapp_slg.core.modules.Authentication
import de.leoapp_slg.core.modules.Input

class Module : Authentication() {

    private var error: Int? = null

    override fun getErrorMessage(): Int? {
        return error
    }

    override fun validateInput(vararg inputs: String): Boolean {
        TODO("not implemented")
    }

    override fun getNecessaryInput(): Int {
        return Input.USERNAME + Input.PASSWORD
    }

}