package de.slg.startseite;
import android.view.View;
class Card {
    final CardType type;
    String title;
    String desc;
    int    icon;
    View.OnClickListener buttonListener;
    boolean enabled;

    Card( CardType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return super.toString() + title;
    }
}
