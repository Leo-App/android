package de.slgdev.leoapp.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;

/**
 * ChangelogDialog.
 * <p>
 * Dialog zum Anzeigen von Änderungen in neuen Versionen.
 *
 * @author Moritz, Gianni
 * @version 2017.2312
 * @since 0.6.9
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.getController().getPreferences()
                .edit()
                .putString("previousVersion", Utils.getAppVersionName())
                .apply();
    }


}