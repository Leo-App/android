package de.slg.startseite;

import android.view.View;

class InfoCard extends Card {

    String buttonDescr;
    String descr;
    View.OnClickListener buttonListener;
    boolean enabled;

    InfoCard(boolean large, CardType type) {
        super(large, type);
        enabled = true;
    }
}
