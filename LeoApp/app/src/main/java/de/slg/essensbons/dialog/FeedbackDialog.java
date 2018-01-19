package de.slg.essensbons.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;

public class FeedbackDialog extends AlertDialog {

    private boolean valid;
    private int     orderedMenu;

    public FeedbackDialog(@NonNull Context context, boolean valid, int orderedMenu) {
        super(context);

        this.valid = valid;
        this.orderedMenu = orderedMenu;
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

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Utils.getController().getEssensbonActivity().getIntegrator().initiateScan();
            }
        });

    }

}
