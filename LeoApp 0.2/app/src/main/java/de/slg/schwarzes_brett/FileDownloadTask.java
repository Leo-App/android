package de.slg.schwarzes_brett;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

class FileDownloadTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
        try {
            URL u = new URL("http://www.moritz.liegmanns.de"+params[0]);
            InputStream is = u.openStream();

            String[] components = params[0].split("/");

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + "data/"+components[components.length-1]));
            while ((length = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
