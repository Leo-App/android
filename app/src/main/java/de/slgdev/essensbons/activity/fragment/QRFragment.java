package de.slgdev.essensbons.activity.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.slgdev.essensbons.activity.EssensbonActivity;
import de.slgdev.essensbons.task.QRWriteTask;
import de.slgdev.essensbons.utility.EssensbonUtils;
import de.slgdev.leoapp.R;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.GraphicUtils;
import de.slgdev.leoapp.utility.Utils;

public class QRFragment extends Fragment implements TaskStatusListener {

    private View viewReference;
    private EssensbonActivity activityReference;

    private ImageView   iv1, iv2;
    private TextView    t2, t3;
    private ProgressBar spinner;

    @Override
    @SuppressLint("SimpleDateFormat")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewReference = inflater.inflate(R.layout.fragment_qr, container, false);
        activityReference = (EssensbonActivity) getActivity();

        iv1 = viewReference.findViewById(R.id.imageViewCode);
        iv2 = viewReference.findViewById(R.id.imageViewError);

        TextView t = viewReference.findViewById(R.id.textViewDatum);
        t.bringToFront();

        t2 = viewReference.findViewById(R.id.textViewMenu);
        t3 = viewReference.findViewById(R.id.textViewMenuDetails);

        spinner = viewReference.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        Date             d  = new Date();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");
        t.setText(df.format(d));

        synchronize(true);

        return viewReference;
    }

    public void synchronize(boolean start) {
        if (activityReference.isRunningSync())
            return;

        activityReference.startRunningSync();

        iv1.setVisibility(View.INVISIBLE);
        iv2.setVisibility(View.INVISIBLE);

        spinner.setVisibility(View.VISIBLE);

        t2.setVisibility(View.INVISIBLE);
        t3.setVisibility(View.GONE);

        new QRWriteTask(start).addListener(this).execute();
    }

    @Override
    public void taskFinished(Object... params) {

        ProgressBar spinner = viewReference.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.INVISIBLE);

        Bitmap result      = (Bitmap) params[0];
        short menu         = (Short) params[1];
        String description = (String) params[2];
        int displayWidth   = GraphicUtils.getDisplayWidth();

        if (result != null) {
            ((ImageView) viewReference.findViewById(R.id.imageView)).setImageBitmap(result);
 //           ((TextView) viewReference.findViewById(R.id.titleNotiz)).setText(getString(R.string.qr_display_menu, menu));
            TextView menuView = viewReference.findViewById(R.id.titleNotiz);
            menuView.setText(String.valueOf(menu));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) menuView.getLayoutParams();
            layoutParams.setMargins(0, (int) (displayWidth*-0.2f), (int) (displayWidth*0.05f), 0);
            menuView.setLayoutParams(layoutParams);
            t3.setText(description);
            t3.setVisibility(View.VISIBLE);
            viewReference.findViewById(R.id.imageViewCode).setVisibility(View.VISIBLE);
  //          viewReference.findViewById(R.id.textViewMenuDetails).setVisibility(View.VISIBLE);
        } else {
            ((ImageView) viewReference.findViewById(R.id.imageViewCode)).setImageResource(R.drawable.ic_qrcode_crossedout);
            ((TextView) viewReference.findViewById(R.id.titleNotiz)).setText(Utils.getString(R.string.qr_display_not_ordered));
            ((TextView) viewReference.findViewById(R.id.textViewDatum)).setText(Utils.getString(R.string.no_order));

            t2.setVisibility(View.INVISIBLE);
            t3.setVisibility(View.INVISIBLE);
            viewReference.findViewById(R.id.imageViewError).setVisibility(View.VISIBLE);
            viewReference.findViewById(R.id.textViewMenuDetails).setVisibility(View.INVISIBLE);
        }

        ((EssensbonActivity) getActivity()).stopRunningSync();
        viewReference.findViewById(R.id.progressBar1).setVisibility(View.GONE);
    }
}