package de.slgdev.essensbons.activity.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
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
    private TextView    t1, t2, t3;
    private ProgressBar spinner;
    private CardView c1;
    private CardView c2;

    @Override
    @SuppressLint("SimpleDateFormat")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewReference = inflater.inflate(R.layout.fragment_qr, container, false);
        activityReference = (EssensbonActivity) getActivity();

        c1 = viewReference.findViewById(R.id.cardViewImage);
        c2 = viewReference.findViewById(R.id.cardViewInfo);

        iv1 = viewReference.findViewById(R.id.imageViewCode);
        iv2 = viewReference.findViewById(R.id.imageViewError);

        t1 = viewReference.findViewById(R.id.textViewMenu);
        t2 = viewReference.findViewById(R.id.textViewMenuDetails);
        t3 = viewReference.findViewById(R.id.textViewError);

        spinner = viewReference.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        synchronize(true);

        return viewReference;
    }

    public void synchronize(boolean start) {
        if (activityReference.isRunningSync())
            return;

        activityReference.startRunningSync();

        c1.setVisibility(View.GONE);
        c2.setVisibility(View.GONE);

        spinner.setVisibility(View.VISIBLE);

        new QRWriteTask(start).addListener(this).execute();
    }

    @Override
    public void taskFinished(Object... params) {

        spinner.setVisibility(View.INVISIBLE);

        Bitmap result      = (Bitmap) params[0];
        short menu         = (Short) params[1];
        String description = (String) params[2];
//        int displayWidth   = GraphicUtils.getDisplayWidth();

        if (result != null) {
            iv1.setImageBitmap(result);
            iv1.setVisibility(View.VISIBLE);

            int c = menu == 1
                    ? ContextCompat.getColor(Utils.getContext(), R.color.menu1)
                    : ContextCompat.getColor(Utils.getContext(), R.color.menu2);

            c1.setCardBackgroundColor(c);
            c2.setCardBackgroundColor(c);

            t1.setText(getString(R.string.qr_display_menu, menu));
            t2.setText(description);

            t1.setVisibility(View.VISIBLE);
            t2.setVisibility(View.VISIBLE);
            t3.setVisibility(View.GONE);
        } else {
            iv2.setImageResource(R.drawable.ic_qrcode_crossedout);
            iv2.setVisibility(View.VISIBLE);

            t3.setText(R.string.no_order);

            int c = ContextCompat.getColor(Utils.getContext(), R.color.colorStandardBackground);
            c1.setCardBackgroundColor(c);
            c2.setCardBackgroundColor(c);

            t1.setVisibility(View.GONE);
            t2.setVisibility(View.GONE);
            t3.setVisibility(View.VISIBLE);
        }

        activityReference.stopRunningSync();
        viewReference.findViewById(R.id.progressBar1).setVisibility(View.GONE);

        c1.setVisibility(View.VISIBLE);
        c2.setVisibility(View.VISIBLE);
    }
}