package de.slgdev.vertretungsplan.utility;

import android.util.Log;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import de.slgdev.leoapp.utility.Utils;

public class SecondAuthenticator extends Authenticator {

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(Utils.getUserDefaultName(), Utils.getController().getPreferences().getString("pref_key_general_password", "").toCharArray());
    }
}
