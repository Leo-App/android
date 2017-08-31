package de.slg.startseite;

import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import de.slg.leoapp.List;
import de.slg.leoapp.Start;
import de.slg.leoapp.Utils;

class MailSendTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... strings) {

        String requestText = strings[0];

        if(!Utils.checkNetwork()) {

            Start.pref.edit().putString("pref_key_request_cached", requestText).apply();
            return null;

        }

        Start.pref.edit().putString("pref_key_request_cached", "-").apply();

        try {
            String emailBody = "<center>----------<h3>Feature Request</h3>----------</center><br/>" +
                    "<b>Name: </b> "+ Utils.getUserName()+ "<br/>" +
                    "<b>Version: </b>"+Utils.getAppVersionName()+"<br/><br/>" +
                    "<b>Request:</b><br/><br/><pre>" +
                    requestText+"</pre>";

            MailClient mailClient = new MailClient("leoapp.noreply@gmail.com", "pOQ2ydhjqzJHxbQioM0Z", new List<String>().append("spitzer-webdesign@outlook.de").append("moritz@liegmanns.de"), "FeatureRequest - " + Utils.getUserName() + " - " + Utils.getUserStufe() + " - " + Utils.getAppVersionName(), emailBody);

            mailClient.createEmailMessage();
            mailClient.sendEmail();
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPostExecute(Void v) {



    }

}
