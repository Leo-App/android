package de.leoapp_slg.core.modules

import androidx.annotation.StringRes

abstract class Authentication {
    abstract fun getNecessaryInput(): Int

    abstract fun validateInput(vararg inputs: String): Boolean

    @StringRes
    abstract fun getErrorMessage(): Int?
}