package de.slg.leoapp.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.utility.UtilsController;

/**
 * InformationDialog
 * <p>
 * Ein allgemeiner simpler Dialog zum Anzeigen von wichtigen Infomationen.
 *
 * @author Gianni
 * @version 2017.1011
 * @since 0.5.9
 */

@SuppressWarnings("unused")
public final class InformationDialog extends AlertDialog {

    private String text;

    /**
     * Konstruktor. Instanziiert den Dialog ohne festgelegten Text. Vor dem Anzeigen muss {@link #setText(int)} aufgerufen werden!
     *
     * @param context Beliebige laufende Activity, siehe {@link UtilsController#getActiveActivity()}
     */
    public InformationDialog(@NonNull Context context) {
        super(context);
    }

    /**
     * Konstruktor. Instanziiert den Dialog mit übergebenem Text.
     *
     * @param context Beliebige laufende Activity, siehe {@link UtilsController#getActiveActivity()}
     * @param text    Dialogtext als Stringressource
     */
    public InformationDialog(@NonNull Context context, @StringRes int text) {
        super(context);
        this.text = Utils.getString(text);
    }

    /**
     * Konstruktor. Instanziiert den Dialog mit übergebenem Text.
     *
     * @param context Beliebige laufende Activity, siehe {@link UtilsController#getActiveActivity()}
     * @param text    Dialogtext als String
     */
    public InformationDialog(@NonNull Context context, String text) {
        super(context);
        this.text = text;
    }

    /**
     * Initialisiert TextView und "OK"-Button. Wird intern aufgerufen.
     *
     * @param b Bundle-Metadaten
     */
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.dialog_general);

        ((TextView) findViewById(R.id.textView10)).setText(text);
        findViewById(R.id.buttonOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
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
