package de.slgdev.leoapp.notification;

public interface LeoAppNotification {
    void send();
    static boolean isEnabled() {
        return true;
    }
}
