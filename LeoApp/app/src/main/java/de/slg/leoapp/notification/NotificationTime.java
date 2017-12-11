package de.slg.leoapp.notification;

/**
 * NotificationTime.
 *
 * Verwaltungsklasse. POJO, welches Stunden und Minuten eines Notify-Zeitpunktes speichert.
 *
 * @author Gianni
 * @since 0.6.7
 * @version 2017.0312
 */
@SuppressWarnings("WeakerAccess")
public final class NotificationTime {
    public final int hours;
    public final int minutes;

    public NotificationTime(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

}
