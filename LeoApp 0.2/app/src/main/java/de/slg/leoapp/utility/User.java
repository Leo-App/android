package de.slg.leoapp.utility;

/**
 * User
 * <p>
 * Verwaltungsklasse mit allen Benutzerinformationen.
 *
 * @author Moritz
 * @version 2017.2610
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public final class User {
    public static final int PERMISSION_UNVERIFIZIERT = 0;
    public static final int PERMISSION_SCHUELER      = 1;
    public static final int PERMISSION_LEHRER        = 2;
    public static final int PERMISSION_ADMIN         = 3;

    /**
     * Eindeutige User-ID
     */
    public final int    uid;

    /**
     * Änderbarer Benutzername
     */
    public final String uname;

    /**
     * Benutzername des pädagogischen Netzwerks
     */
    public final String udefaultname;

    /**
     * Aktuelle Stufe des Users
     */
    public final String ustufe;

    /**
     * User Permission-Level
     */
    public final int    upermission;

    /**
     * Konstruktor.
     *
     * @param uid          Einmalige Benutzer-ID.
     * @param uname        Benutzername.
     * @param ustufe       Jahrgangsstufe des Users, Wert für Lehrer: "TEA".
     * @param upermission  Berechtigungsstufe des Users, von 1 bis 3.
     * @param udefaultname Benutzername des mit dem Account verbundenen Schulaccounts.
     */
    public User(int uid, String uname, String ustufe, int upermission, String udefaultname) {
        this.uname = uname;
        this.uid = uid;
        this.upermission = upermission;
        this.ustufe = ustufe;
        this.udefaultname = udefaultname;
    }
}