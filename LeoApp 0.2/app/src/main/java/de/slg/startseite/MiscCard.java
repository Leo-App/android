package de.slg.startseite;

public class MiscCard extends Card {

    CardType type;

    public MiscCard(boolean large, CardType t) {
        super(large);
        type = t;
    }
}
