package de.slg.leoapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

/**
 * InformationDialog
 *
 * Ein allgemeiner simpler Dialog zum Anzeigen von wichtigen Infomationen.
 *
 * @since 0.5.9
 * @version 2017.1011
 * @author Gianni
 */

public final class InformationDialog extends AlertDialog {

    private String text;

    public InformationDialog(@NonNull Context context) {
        super(context);
    }

    public InformationDialog(@NonNull Context context, @StringRes int text) {
        super(context);
        this.text = Utils.getString(text);
    }

    public InformationDialog(@NonNull Context context, String text) {
        super(context);
        this.text = text;
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_general);

        ((TextView) findViewById(R.id.textView10)).setText(text);

    }

    /**
     * Setzt den Dialogtext
     *
     * @param text Neuer Dialogtext
     * @return Aktuelles Dialogobjekt
     */
    public InformationDialog setText(@StringRes int text) {
        this.text = Utils.getString(text);
        return this;
    }

}
