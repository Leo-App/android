package de.slg.schwarzes_brett;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import de.slg.leoapp.Utils;

class FileDownloadTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
        try {
            DataInputStream inputStream =
                    new DataInputStream(
                            new URL(Utils.BASE_URL + params[0].substring(1))
                                    .openStream());

            String filename = params[0].substring(params[0].lastIndexOf('/')+1);
            String directory =
                    Environment.getExternalStorageDirectory() + File.separator
                            + "LeoApp" + File.separator
                            + "data" + File.separator;

            Log.wtf("TAG", directory);
            Log.wtf("TAG", filename);
            Log.wtf("TAG", "canWrite = " + Environment.getExternalStorageDirectory().canWrite());

            File dir = new File(directory);
            dir.mkdirs();
            Log.wtf("TAG", String.valueOf(dir.isDirectory()));
            File file = new File(directory + filename);
            file.createNewFile();

            byte[] buffer = new byte[1024];
            int    length;

            FileOutputStream outputStream = new FileOutputStream(file);
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
