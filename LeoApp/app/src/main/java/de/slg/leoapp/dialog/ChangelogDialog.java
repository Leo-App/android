package de.slg.leoapp.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;

/**
 * ChangelogDialog.
 * <p>
 * Dialog zum Anzeigen von Änderungen in neuen Versionen.
 *
 * @author Moritz
 * @since 0.6.9
 * @version 2017.0712
 */

public class ChangelogDialog extends BottomSheetDialogFragment {

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_changelog, null);
        ((TextView) contentView.findViewById(R.id.version_textview)).setText(Utils.getAppVersionName());
        dialog.setContentView(contentView);
    }

}