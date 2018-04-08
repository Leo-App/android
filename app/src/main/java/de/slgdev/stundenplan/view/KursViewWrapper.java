package de.slgdev.stundenplan.view;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;
import de.slgdev.stundenplan.utility.Fach;

import static de.slgdev.leoapp.utility.Utils.getContext;

public class KursViewWrapper {
    private static final int colour0 = ContextCompat.getColor(
            getContext(),
            R.color.colorText
    );
    private static final int colour1 = ContextCompat.getColor(
            getContext(),
            R.color.colorAccent
    );
    private static final int colour2 = ContextCompat.getColor(
            getContext(),
            R.color.colorTextGreyed
    );

    public final View    v;
    public final Fach    f;
    public final float[] stunden;
    private      boolean gewaehlt;

    private final TextView tvFach;
    private final TextView tvKuerzel;
    private final TextView tvLehrer;
    private final TextView tvKlasse;
    private final CheckBox checkBox;

    public KursViewWrapper(View v, Fach f, float[] stunden) {
        this.v = v;
        this.f = f;
        this.stunden = stunden;

        tvFach = v.findViewById(R.id.fach_auswahl);
        tvKuerzel = v.findViewById(R.id.kürzel_auswahl);
        tvLehrer = v.findViewById(R.id.lehrer_auswahl);
        tvKlasse = v.findViewById(R.id.klasse_auswahl);
        checkBox = v.findViewById(R.id.checkBox);

        tvFach.setText(f.getName());
        tvKuerzel.setText(f.getKuerzel());
        tvLehrer.setText(f.getLehrer());

        if (Utils.getUserStufe().matches("[0-9]+")) {
            tvKlasse.setVisibility(View.VISIBLE);
            tvKlasse.setText(f.getKlasse());
        }
    }

    public void setEnabled(List<String> faecher, boolean[][] stunden) {
        if (gewaehlt) {
            v.setEnabled(true);
            refreshTextColor();
            return;
        }

        for (float d : this.stunden) {
            if (stunden[(int) (d) - 1][(int) (d * 100) % 100 - 1]) {
                v.setEnabled(false);
                refreshTextColor();
                return;
            }
        }

        if (faecher.contains(gibKuerzel())) {
            v.setEnabled(false);
            refreshTextColor();
            return;
        }

        v.setEnabled(true);
        refreshTextColor();
    }

    public String gibKuerzel() {
        if (!f.getKuerzel().startsWith("IB")) {
            return f.getKuerzel().substring(0, 2);
        } else {
            return f.getKuerzel().substring(3, 6);
        }
    }

    public void setGewaehlt(boolean b) {
        this.gewaehlt = b;
        this.checkBox.setChecked(b);

        refreshTextColor();
    }

    public void toggleGewaehlt() {
        setGewaehlt(!istGewaehlt());
    }

    public boolean istGewaehlt() {
        return gewaehlt;
    }

    private void refreshTextColor() {
        if (gewaehlt) {
            tvFach.setTextColor(colour1);
            tvKuerzel.setTextColor(colour1);
            tvLehrer.setTextColor(colour1);
        } else if (v.isEnabled()) {
            tvFach.setTextColor(colour0);
            tvKuerzel.setTextColor(colour0);
            tvLehrer.setTextColor(colour0);
        } else {
            tvFach.setTextColor(colour2);
            tvKuerzel.setTextColor(colour2);
            tvLehrer.setTextColor(colour2);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        v.setOnClickListener(listener);
    }
}
