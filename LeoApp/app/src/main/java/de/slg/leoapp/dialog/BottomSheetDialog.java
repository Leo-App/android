package de.slg.leoapp.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import de.slg.leoapp.R;

/**
 * BottomSheetDialog
 * <p>
 * Bei gedrückt halten eines beliebigen Buttons öffnet sich ein BottomSheetDialog und beschreibt dessen nähere Funktion.
 * (•̀ᴗ•́)و ̑̑  Benutzerfreundlichkeit (•̀ᴗ•́)و ̑̑
 *
 * @author Gianni
 * @version 2017.2610
 * @since 0.5.7
 */

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private String title;
    private String content;

    /**
     * Setzt den Titel des Informationsdialogs.
     *
     * @param title Titel
     * @return Instanz des aktuellen Dialogs
     */
    public BottomSheetDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Setzt den Inhalt des Informationsdialogs.
     *
     * @param content Inhalt
     * @return Instanz des aktuellen Dialogs
     */
    public BottomSheetDialog setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * Siehe {@link BottomSheetDialogFragment}
     *
     * @param dialog siehe BottomSheetDialogFragment
     * @param style  siehe BottomSheetDialogFragment
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_bottom_sheet, null);
        ((TextView) contentView.findViewById(R.id.textView1)).setText(title);
        ((TextView) contentView.findViewById(R.id.textView10)).setText(content);
        dialog.setContentView(contentView);
    }
}
