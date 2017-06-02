package de.slg.leoapp;

public class User {

    public final int userId;
    public final String userName;
    public String klasse;
    public int permission;

    public User(int userId, String userName, String klasse, int permission) {
        this.userName = userName;
        this.userId = userId;
        this.permission = permission;
        this.klasse = klasse;
    }

    @Override
    public String toString() {
        return "id: " + userId + ", name: " + userName + ", permission: " + permission + ", klasse: " + klasse;
    }


    public boolean allAttributesSet() {
        return userName != null && userId > 0 && klasse != null && permission >= 0;
    }
}