package de.slg.leoapp.task;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import de.slg.leoapp.utility.Utils;

public class DownloadFilesTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(
                                    Utils.BASE_URL_PHP + "klausurplan/aktuell.xml"
                            )
                                    .openConnection()
                                    .getInputStream()
                    )
            );

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            Utils.getContext()
                                    .openFileOutput(
                                            "klausurplan.xml", Context.MODE_PRIVATE
                                    )
                    )
            );

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                writer.write(line);
                writer.newLine();
            }

            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(
                                    Utils.BASE_URL_PHP + "stundenplan/aktuell.txt"
                            )
                                    .openConnection()
                                    .getInputStream()
                    )
            );

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            Utils.getContext()
                                    .openFileOutput(
                                            "stundenplan.txt",
                                            Context.MODE_PRIVATE
                                    )
                    )
            );

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                writer.write(line.replace("L�Z", "LÜZ").replace("CH�", "CHÜ").replace("BI�", "BIÜ"));
                writer.newLine();
            }

            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}