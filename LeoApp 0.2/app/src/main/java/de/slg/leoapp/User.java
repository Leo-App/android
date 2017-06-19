package de.slg.leoapp;

public class User {
    public final int uid;
    public final String uname;
    public String ustufe;
    public int upermission;

    public User(int uid, String uname, String ustufe, int upermission) {
        this.uname = uname;
        this.uid = uid;
        this.upermission = upermission;
        this.ustufe = ustufe;
    }
}