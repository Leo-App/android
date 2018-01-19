package de.slg.essensbons.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class FeedbackDialog extends AlertDialog {

    private boolean valid;
    private int     orderedMenu;

    private ZXingScannerView scannerView;

    public FeedbackDialog(@NonNull Context context, boolean valid, int orderedMenu) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        this.valid = valid;
        this.orderedMenu = orderedMenu;
        this.scannerView = Utils.getController().getEssensbonActivity().getScannerView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (valid) {
            setContentView(R.layout.dialog_valid);
            ((TextView) findViewById(R.id.textView4)).setText(Utils.getContext().getString(R.string.dialog_desc_valid, orderedMenu));
        } else {
            setContentView(R.layout.dialog_invalid);
        }

        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                scannerView.startCamera(0);
            }
        });

        setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                scannerView.setResultHandler(Utils.getController().getEssensbonActivity());
                scannerView.startCamera(0);
            }
        });

    }

}
