package de.slg.leoapp.core.modules

import androidx.annotation.StringRes

interface Authentication : Module {

    @StringRes
    fun getErrorMessage(): Int?

    fun getNecessaryInput(): Int

    fun getAPIKey(): String

    fun getDeviceListingEnabled(): Boolean

    /**
     * Gibt für übergebene User-Eingabedaten zurück, ob die Verifizierung erfolgreich war. Die Parameter werden in folgender Reihenfolge
     * übergeben: USERNAME, PASSWORT, ID; wobei nicht verfügbare Inputs übersprungen werden.
     *
     * @param inputs Verfügbare Userinputs
     * @return War die Verifizierung erfolgreich?
     */
    fun validateInput(vararg inputs: String): Boolean
}