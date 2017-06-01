package de.slg.essensqr;

import java.util.Date;

public class Order {

    private Date date;
    private short menu;
    private String descr;

    public Order(Date d, short m, String s) {

        menu = m;
        date = d;
        descr = s;

    }

    public Date getDate() {

        return date;

    }

    public String getDescr() {

        return descr;

    }

    public short getMenu() {

        return menu;

    }

}
