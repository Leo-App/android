package de.slg.leoapp.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import de.slg.leoapp.R;

/**
 * EditTextDialog.
 * <p>
 * Einfacher Dialog mit Texteingabefeld.
 *
 * @author Gianni, Moritz
 * @version 2017.1211
 * @since 0.5.7
 */
public class EditTextDialog extends AlertDialog {

    private EditText editText;
    private String   hint;
    private String   title;

    private View.OnClickListener action;

    /**
     * Konstruktur. Initialisiert den EditText-Dialog mit einem OnClickListener für den OK Button und einem Hint für das Textfeld.
     *
     * @param context Kontextobjekt (Aktive Activity)
     * @param title   Titel für Textfeld
     * @param hint    Hint für Textfeld
     * @param action  OnClickListener für den OK-Button
     */
    public EditTextDialog(@NonNull Activity context, @NonNull String title, @NonNull String hint, @NonNull View.OnClickListener action) {
        super(context);
        this.title = title;
        this.hint = hint;
        this.action = action;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_text);

        editText = (EditText) findViewById(R.id.editText);
        editText.setHint(hint);

        ((TextView) findViewById(R.id.titel)).setText(title);

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.ok).setOnClickListener(action);

        initWindowParams();
    }

    /**
     * Liefert den Inhalt des EditText Feldes als String.
     *
     * @return User-Eingabe
     */
    public String getTextInput() {
        return editText.getText().toString();
    }

    /**
     * Setzt den Inhalt des Textfeldes auf einen übergebenen Input.
     *
     * @param input Neuer Text
     */
    public void setTextInput(String input) {
        editText.setText(input);
    }

    /**
     * Setzt den InputType des EditText-Feldes auf einen per Parameter übergebenen Wert.
     *
     * @param type Neuer InputType
     */
    public void setInputType(int type) {
        editText.setInputType(type);
    }

    private void initWindowParams() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}