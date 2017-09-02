package de.slg.leoapp;

public class User {
    public final int    uid;
    public final String uname;
    public final String udefaultname;
    public final String ustufe;
    public final int    upermission;

    public User(int uid, String uname, String ustufe, int upermission, String udefaultname) {
        this.uname = uname;
        this.uid = uid;
        this.upermission = upermission;
        this.ustufe = ustufe;
        this.udefaultname = udefaultname;
    }
}