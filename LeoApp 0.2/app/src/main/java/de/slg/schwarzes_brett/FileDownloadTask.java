package de.slg.schwarzes_brett;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;

class FileDownloadTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {

        Uri    location = Uri.parse(Utils.DOMAIN_DEV + params[0].substring(1));
        String filename = params[0].substring(params[0].lastIndexOf('/') + 1);

        DownloadManager         downloadManager = (DownloadManager) Utils.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request         = new DownloadManager.Request(location);

        request.setTitle(filename);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDescription(Utils.getString(R.string.download_description_news));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            request.setDestinationInExternalFilesDir(Utils.getContext(), Environment.DIRECTORY_DOCUMENTS, filename);
        else
            request.setDestinationInExternalFilesDir(Utils.getContext(), Environment.DIRECTORY_DOWNLOADS, filename);

        downloadManager.enqueue(request);

        return null;
    }
}
