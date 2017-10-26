package de.slg.leoapp;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;


/**
 * BottomSheetDialog
 *
 * Bei gedrückt halten eines beliebigen Buttons öffnet sich ein BottomSheetDialog und beschreibt dessen nähere Funktion.
 * (•̀ᴗ•́)و ̑̑  Benutzerfreundlichkeit (•̀ᴗ•́)و ̑̑
 *
 * @version 2017.2610
 * @since 0.5.7
 * @author Gianni
 */

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private String title;
    private String content;

    public BottomSheetDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public BottomSheetDialog setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        //noinspection RestrictedApi
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_bottom_sheet, null);
        ((TextView)contentView.findViewById(R.id.textView1)).setText(title);
        ((TextView)contentView.findViewById(R.id.textView10)).setText(content);
        dialog.setContentView(contentView);
    }

}
