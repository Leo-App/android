package de.slgdev.leoapp.task;

import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import de.slgdev.leoapp.utility.MailClient;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;

public class MailSendTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... strings) {
        String requestText = strings[0];
        if (!Utils.checkNetwork()) {
            Utils.getController().getPreferences()
                    .edit()
                    .putString("pref_key_request_cached", requestText)
                    .apply();
            return null;
        }
        Utils.getController().getPreferences()
                .edit()
                .putString("pref_key_request_cached", "-")
                .apply();
        try {
            String emailBody = "<center>----------<h3>Feature Request</h3>----------</center><br/>" +
                    "<b>Name: </b> " + Utils.getUserName() + "<br/>" +
                    "<b>Version: </b>" + Utils.getAppVersionName() + "<br/><br/>" +
                    "<b>Request:</b><br/><br/><pre>" +
                    requestText + "</pre>";
            MailClient mailClient = new MailClient(new List<String>().append("app@leo-ac.de"), "FeatureRequest - " + Utils.getUserName() + " - " + Utils.getUserStufe() + " - " + Utils.getAppVersionName(), emailBody);
            mailClient.createEmailMessage();
            mailClient.sendEmail();
        } catch (MessagingException | UnsupportedEncodingException e) {
            Utils.logError(e);
        }
        return null;
    }
}
