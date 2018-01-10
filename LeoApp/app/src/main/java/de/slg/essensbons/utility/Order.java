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
public final class Order {
    private final short  id;
    private final Date   date;
    private final short  menu;
    private final String descr;

    public Order(short id, Date date, short menu, String descr) {
        this.id = id;
        this.menu = menu;
        this.date = date;
        this.descr = descr;
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

    public short getId() {
        return id;
    }
}