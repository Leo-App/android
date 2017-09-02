package de.slg.essensqr;

import java.util.Date;

class Order {
    private final Date   date;
    private final short  menu;
    private final String descr;

    Order(Date d, short m, String s) {
        menu = m;
        date = d;
        descr = s;
    }

    public Date getDate() {
        return date;
    }

    String getDescr() {
        return descr;
    }

    public short getMenu() {
        return menu;
    }
}