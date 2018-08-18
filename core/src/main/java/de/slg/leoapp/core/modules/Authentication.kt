package de.slg.leoapp.core.modules

import android.content.Context
import androidx.annotation.StringRes

interface Authentication : Module {

    @StringRes
    fun getErrorMessage(): Int?

    fun getNecessaryInput(): Int

    fun getAPIKey(context: Context): String

    fun getDeviceListingEnabled(): Boolean

    /**
     * Gibt für übergebene User-Eingabedaten zurück, ob die Verifizierung erfolgreich war. Die Parameter werden in folgender Reihenfolge
     * übergeben: USERNAME, PASSWORT, ID; wobei nicht verfügbare Inputs übersprungen werden.
     *
     * @param inputs Verfügbare Userinputs
     * @param context Der aktuelle Context bei Überprüfung
     * @return War die Verifizierung erfolgreich?
     */
    fun validateInput(context: Context, vararg inputs: String): Boolean
}