package de.slg.startseite;

public class MiscCard extends Card {

    boolean complex;

    public MiscCard(boolean large, CardType t, boolean complex) {
        super(large, t);
        this.complex = complex;
    }
}
