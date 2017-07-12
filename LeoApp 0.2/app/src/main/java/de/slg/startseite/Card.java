package de.slg.startseite;

abstract class Card {

    final boolean large;
    String title;
    int icon;
    final CardType type;

    Card(boolean large, CardType type) {

        this.large = large;
        this.type = type;

    }

}
