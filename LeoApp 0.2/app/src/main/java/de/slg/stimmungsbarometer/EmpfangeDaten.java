package de.slg.stimmungsbarometer;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class EmpfangeDaten extends AsyncTask<Void, Void, Ergebnis[][]> {

    private int userid;
    private String[] splitI, splitS, splitL, splitA;

    public EmpfangeDaten(int userid) {
        this.userid = userid;
        splitI = new String[0];
        splitS = new String[0];
        splitL = new String[0];
        splitA = new String[0];
    }

    @Override
    protected Ergebnis[][] doInBackground(Void... voids) {
        try {
            URL url = new URL("http://moritz.liegmanns.de/stimmungsbarometer/ergebnisse.php?key=5453&userid=" + userid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            String s = "";
            while ((line = reader.readLine()) != null)
                s += line;
            String[] e = s.split("_abschnitt_");
            reader.close();
            if (!e[0].equals("."))
                splitI = e[0].split("_next_");
            if (!e[1].equals("."))
                splitS = e[1].split("_next_");
            if (!e[2].equals("."))
                splitL = e[2].split("_next_");
            if (!e[3].equals("."))
                splitA = e[3].split("_next_");
            Ergebnis[][] ergebnisse = new Ergebnis[4][];
            ergebnisse[0] = new Ergebnis[splitI.length];
            ergebnisse[1] = new Ergebnis[splitS.length];
            ergebnisse[2] = new Ergebnis[splitL.length];
            ergebnisse[3] = new Ergebnis[splitA.length];
            for (int i = 0; i < ergebnisse[0].length; i++) {
                String[] current = splitI[i].split(";");
                if (current.length == 2) {
                    String[] date = current[1].replace('.', '_').split("_");
                    ergebnisse[0][i] = new Ergebnis(new Date(Integer.parseInt(date[2]) - 1900, Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])), Double.parseDouble(current[0]), true, false, false, false);
                }
            }
            for (int i = 0; i < ergebnisse[1].length; i++) {
                String[] current = splitS[i].split(";");
                if (current.length == 2) {
                    String[] date = current[1].replace('.', '_').split("_");
                    ergebnisse[1][i] = new Ergebnis(new Date(Integer.parseInt(date[2]) - 1900, Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])), Double.parseDouble(current[0]), false, true, false, false);
                }
            }
            for (int i = 0; i < ergebnisse[2].length; i++) {
                String[] current = splitL[i].split(";");
                if (current.length == 2) {
                    String[] date = current[1].replace('.', '_').split("_");
                    ergebnisse[2][i] = new Ergebnis(new Date(Integer.parseInt(date[2]) - 1900, Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])), Double.parseDouble(current[0]), false, false, true, false);
                }
            }
            for (int i = 0; i < ergebnisse[3].length; i++) {
                String[] current = splitA[i].split(";");
                if (current.length == 2) {
                    String[] date = current[1].replace('.', '_').split("_");
                    ergebnisse[3][i] = new Ergebnis(new Date(Integer.parseInt(date[2]) - 1900, Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0])), Double.parseDouble(current[0]), false, false, false, true);
                }
            }
            return ergebnisse;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}