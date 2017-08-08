package de.slg.startseite;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import de.slg.leoapp.R;

public class CardAddDialog extends AlertDialog {

    private MainActivity context;

    protected CardAddDialog(@NonNull MainActivity context) {
        super(context);

        this.context = context;

    }

    @Override
    public void onCreate(Bundle b) {

        super.onCreate(b);
        setContentView(R.layout.dialog_layout_add_card);

        initOptions();
        initSendButton();

    }
    private void initOptions() {

        findViewById(R.id.imageButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    private void initSendButton() {



    }



}
