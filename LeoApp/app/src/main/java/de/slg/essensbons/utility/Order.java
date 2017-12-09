package de.slg.essensbons.utility;

import java.util.Date;

/**
 * Order.
 *
 * POJO zum Verwalten von Bestellungen.
 *
 * @author Gianni
 * @since 0.0.1
 * @version 2017.0912
 */
public class Order {
    private final Date   date;
    private final short  menu;
    private final String descr;

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