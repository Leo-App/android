package de.slg.stundenplan;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FachImporter extends AsyncTask<Void, Void, ArrayList<Fach>> {
    private BufferedReader br;
    private BufferedWriter bw;
    private Context con;
    private ArrayList<Fach> facherAT;
    private String stufe;

    public FachImporter(Context c, String pStufe) {
        this.con = c;
        stufe = pStufe;
    }

    @Override
    protected ArrayList<Fach> doInBackground(Void... parameter) {
        try {
            facherAT = new ArrayList<>();
            URL url = new URL("http://moritz.liegmanns.de/testdaten.txt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();
            FileOutputStream fos = con.openFileOutput("testdaten.txt", Context.MODE_PRIVATE);
            InputStream inSt = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int bufferLength;
            while ((bufferLength = inSt.read(buffer)) > 0) {
                fos.write(buffer, 0, bufferLength);
            }
            fos.close();

            String zeile;
            String[] fach;

            br = new BufferedReader(new InputStreamReader(con.openFileInput("testdaten.txt")));
            bw = new BufferedWriter(new OutputStreamWriter(con.openFileOutput("allefaecher.txt", Context.MODE_PRIVATE))); //Darf ich das oder zu viel speicher? // TODO: 27.05.2017

            zeile = br.readLine();
            while (zeile != null) {
                zeile = zeile.replace('"', '@');
                zeile = this.ignoriereAT(zeile);
                fach = zeile.split(",");
                if (fach[1].equals(stufe)) {
                    facherAT.add(new Fach(fach[3], fach[4], fach[2], fach[5], fach[6], con));
                    bw.write("Name;" + fach[3] + ";" + fach[4] + ";" + fach[2] + ";" + fach[5] + ";" + fach[6] + ";nicht;notiz");
                    bw.newLine();
                }
                zeile = br.readLine();
            }
            bw.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return facherAT;
    }

    private String ignoriereAT(String s) {
        String erg = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '@') {
                erg = erg + s.charAt(i);
            }
        }
        return erg;
    }
}
