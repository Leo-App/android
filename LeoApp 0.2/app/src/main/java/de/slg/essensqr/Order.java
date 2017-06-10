package de.slg.essensqr;

import java.util.Date;

class Order {

    private Date date;
    private short menu;
    private String descr;

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