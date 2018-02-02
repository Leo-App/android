package de.slgdev.it_problem.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.Utils;

public class ImageSynchronizerTask extends AsyncTask<String, Void, Bitmap> {

    private TaskStatusListener listener;

    @Override
    protected Bitmap doInBackground(String... urls) {

        try {
            URL url = new URL(Utils.DOMAIN_DEV+urls[0]);
            Utils.logError(url);
            return BitmapFactory.decodeStream(url.openStream());
        } catch (IOException e) {
            Utils.logError(e);
        }

        return null;
    }

    @Override
    public void onPostExecute(Bitmap bitmap) {
        listener.taskFinished(bitmap);
    }

    public ImageSynchronizerTask registerListener(TaskStatusListener listener) {
        this.listener = listener;
        return this;
    }

}
