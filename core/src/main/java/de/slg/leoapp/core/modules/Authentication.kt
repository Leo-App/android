package de.slg.leoapp.core.modules

import androidx.annotation.StringRes

interface Authentication : Module {

    @StringRes
    fun getErrorMessage(): Int?

    fun getNecessaryInput(): Int

    fun getAPIKey(): String

    fun getDeviceListingEnabled(): Boolean

    fun validateInput(vararg inputs: String): Boolean
}