package de.slg.leoapp.authentication

import android.content.Context
import android.util.Base64
import androidx.annotation.StringRes
import de.slg.leoapp.annotation.Module
import de.slg.leoapp.core.data.User
import de.slg.leoapp.core.modules.Authentication
import de.slg.leoapp.core.modules.Input
import de.slg.leoapp.core.preferences.PreferenceManager
import de.slg.leoapp.core.utility.DOMAIN_DEV
import de.slg.leoapp.core.utility.toHexString
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.SecureRandom

@Module("authentication", true)
class Module : Authentication {

    @StringRes
    private var error: Int? = null

    override fun validateInput(context: Context, vararg inputs: String): Boolean {
        val username = inputs[0]
        val password = inputs[1]

        val request = khttp.get("$DOMAIN_DEV/verify.php", headers = mapOf("Authorization" to toAuthFormat(username, password)))

        if (request.statusCode == 401) {
            error = R.string.error_auth_failed
            return false
        }

        if (request.statusCode != 200) {
            error = R.string.error_try_again
            return false
        }

        val checksum = request.jsonObject.getString("checksum")

        PreferenceManager.edit(context) {
            putString(PreferenceManager.Device.AUTHENTICATION, checksum)
        }

        return true
    }

    override fun getErrorMessage() = error

    override fun getNecessaryInput() = Input.USERNAME + Input.PASSWORD

    override fun getAPIKey(context: Context): String {
        lateinit var checksum: String

        PreferenceManager.read(context) {
            checksum = getString(PreferenceManager.Device.AUTHENTICATION)
        }

        val random = SecureRandom()
        val saltBytes = ByteArray(32)
        random.nextBytes(saltBytes)

        val salt = Base64.encodeToString(saltBytes, Base64.NO_WRAP)
        val timestamp = System.currentTimeMillis() / 10000
        val userId = User(context).id

        val base = checksum + salt + userId + timestamp

        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(base.toByteArray(Charset.forName("UTF-8")))

        return "$userId-${hash.toHexString()}-$salt-${timestamp % 10}"
    }

    override fun getDeviceListingEnabled() = true

    private fun toAuthFormat(username: String, password: String) =
            "Basic ${String(Base64.encode(("$username:$password").toByteArray(), 0))}"

}