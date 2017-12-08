package de.slg.essensqr.activity.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.slg.essensqr.activity.EssensQRActivity;
import de.slg.essensqr.task.QRWriteTask;
import de.slg.leoapp.R;

public class QRFragment extends Fragment {
    private View      rootView;
    private ImageView iv1, iv2;
    private TextView t2, t3;
    private ProgressBar spinner;

    @Override
    @SuppressLint("SimpleDateFormat")
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qr, container, false);

        iv1 = (ImageView) rootView.findViewById(R.id.imageView);
        iv2 = (ImageView) rootView.findViewById(R.id.imageViewError);

        TextView t = (TextView) rootView.findViewById(R.id.textViewDatum);
        t.bringToFront();

        t2 = (TextView) rootView.findViewById(R.id.textViewMenu);
        t2.bringToFront();

        t3 = (TextView) rootView.findViewById(R.id.textViewMenuDetails);

        spinner = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        Date             d  = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
        t.setText(df.format(d));

        synchronize(true);

        return rootView;
    }

    public void synchronize(boolean start) {
        Log.d("LeoApp", "syncFIRSTCALLED");
        Log.d("LeoApp", "OnStart: " + String.valueOf(start));

        if (EssensQRActivity.runningSync)
            return;

        EssensQRActivity.runningSync = true;

        iv1.setVisibility(View.INVISIBLE);
        iv2.setVisibility(View.INVISIBLE);

        spinner.setVisibility(View.VISIBLE);

        t2.setVisibility(View.INVISIBLE);
        t3.setVisibility(View.INVISIBLE);

        QRWriteTask task = new QRWriteTask(this, start);
        task.execute(rootView);
    }

    public void showSnackBarNoConnection() {
        final Snackbar cS = Snackbar.make(rootView.findViewById(R.id.myCoordinatorLayout), R.string.snackbar_no_connection_info, Snackbar.LENGTH_LONG);
        cS.setActionTextColor(Color.WHITE);
        cS.setAction(getString(R.string.snackbar_no_connection_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cS.dismiss();
            }
        });
        cS.show();
    }
}