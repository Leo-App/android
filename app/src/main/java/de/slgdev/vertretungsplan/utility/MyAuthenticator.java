package de.slgdev.vertretungsplan.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

public class MyAuthenticator extends Authenticator {

    private String username;
    private String password;
    private BufferedReader bufferedReader;
    private URL url;

    protected PasswordAuthentication getPasswordAuthentication() {

        username = "leo";
        password ="";

        Authenticator.setDefault(new SecondAuthenticator());
        try {
            url = new URL("https://secureaccess.itac-school.de/slgweb/leoapp_php/vplanpass.txt");
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));

            password = bufferedReader.readLine();

            bufferedReader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Authenticator.setDefault(new MyAuthenticator());

        return new PasswordAuthentication(username, password.toCharArray());
    }

}
