package de.slg.startseite;

abstract class Card {

    final boolean large;
    final CardType type;
    String title;
    int icon;

    Card(boolean large, CardType type) {

        this.large = large;
        this.type = type;

    }

    @Override
    public String toString() {

        return super.toString() + title;

    }

}
