package de.slgdev.leoapp.task;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import de.slgdev.leoapp.utility.Utils;

public class SyncFilesTask extends AsyncTask<Void, Void, Void> {
    public static boolean running = false;

    @Override
    protected Void doInBackground(Void... voids) {
        running = true;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new URL(
                                    Utils.BASE_URL_PHP + "klausurplan/" +
                                            "aktuell.xml"
                            )
                                    .openConnection()
                                    .getInputStream()
                    )
            );

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            Utils.getContext()
                                    .openFileOutput(
                                            "klausurplan.xml",
                                            Context.MODE_PRIVATE
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
            Utils.logError(e);
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
                writer.write(
                        line
                                .substring(
                                        line.indexOf(',') + 1,
                                        line.lastIndexOf(',')
                                )
                                .replace(
                                        "L�Z",
                                        "LÜZ")
                                .replace(
                                        "CH�",
                                        "CHÜ")
                                .replace(
                                        "BI�",
                                        "BIÜ")
                                .replace(
                                        "\"",
                                        ""
                                )
                );
                writer.newLine();
            }

            reader.close();
            writer.close();
        } catch (IOException e) {
            Utils.logError(e);
        }

        running = false;

        return null;
    }
}