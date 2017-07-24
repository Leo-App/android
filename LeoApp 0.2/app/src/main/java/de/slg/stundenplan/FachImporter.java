package de.slg.stundenplan;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import de.slg.leoapp.Utils;

class FachImporter extends AsyncTask<Void, Void, Void> {
    private final Context context;
    private final String stufe;

    FachImporter(Context c, String pStufe) {
        this.context = c;
        stufe = pStufe;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (Utils.checkNetwork()) {
            Log.e("FachImporter", "started");
            try {
                InputStream inputStream =
                        new URL("http://moritz.liegmanns.de/testdaten.txt")
                                .openConnection()
                                .getInputStream();
                FileOutputStream fileOutput = context.openFileOutput("testdaten.txt", Context.MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                int bufferLength;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                }
                fileOutput.close();
                inputStream.close();

                context.deleteFile("allefaecher.txt");

                BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput("testdaten.txt")));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput("allefaecher.txt", Context.MODE_PRIVATE)));

                String lastKurzel = "";
                long lastID = -1;

                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    String[] fach = line.replace("\"", "").split(",");
                    if (fach[1].equals(stufe)) {
                        String s = "Name;"
                                + fach[3] + ";" //Kürzel
                                + fach[4] + ";" //Raum
                                + fach[2] + ";" //Lehrer
                                + fach[5] + ";" //Tag
                                + fach[6] + ";" //Stunde
                                + "nicht" + ";"
                                + " ";
                        writer.write(s);
                        writer.newLine();
                        if (!fach[3].equals(lastKurzel)) {
                            lastID = Utils.getStundDB().insertFach(fach[3], fach[2], fach[4]);
                            lastKurzel = fach[3];
                        }
                        Utils.getStundDB().insertStunde(lastID, Integer.parseInt(fach[5]), Integer.parseInt(fach[6]));
                    }
                }
                writer.close();
                reader.close();
                Log.e("FachImporter", "done!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
