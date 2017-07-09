package de.slg.startseite;

public abstract class Card {

    boolean large;
    String title;
    int icon;
    CardType type;

    public Card(boolean large, CardType type) {

        this.large = large;
        this.type = type;

    }

}
