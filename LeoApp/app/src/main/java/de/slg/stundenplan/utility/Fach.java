package de.slg.stundenplan.utility;

public class Fach {
    public final  int     id;
    private final String  name;
    private final String  kuerzel;
    private final String  raum;
    private final String  lehrer;
    private final String  klasse;
    private final int     tag;
    private final int     stunde;
    private       boolean schriftlich;
    private       String  notiz;

    public Fach(int id, String kurz, String name, String lehrer, String klasse, String raum, int tag, int stunde) {
        this.id = id;
        this.kuerzel = kurz;
        this.name = name;
        this.raum = raum;
        this.lehrer = lehrer;
        this.klasse = klasse;
        this.tag = tag;
        this.stunde = stunde;
        this.schriftlich = false;
        this.notiz = "";
    }

    private String gibStundenName(int pStunde) {
        switch (pStunde) {
            case 1:
                return "08:00 - 08:45";
            case 2:
                return "08:50 - 09:35";
            case 3:
                return "09:50 - 10:35";
            case 4:
                return "10:40 - 11:25";
            case 5:
                return "11:40 - 12:25";
            case 6:
                return "12:30 - 13:15";
            case 7:
                return "13:30 - 14:15";
            case 8:
                return "14:20 - 15:05";
            case 9:
                return "15:10 - 15:55";
            case 10:
                return "16:00 - 16:45";
            default:
                return Integer.toString(pStunde);
        }
    }

    public void setzeNotiz(String notiz) {
        this.notiz = notiz;
    }

    public void setzeSchriftlich(boolean b) {
        schriftlich = b;
    }

    public String getKuerzel() {
        return kuerzel;
    }

    public String getName() {
        return name;
    }

    public String getLehrer() {
        return lehrer;
    }

    public String getRaum() {
        return raum;
    }

    public int getTag() {
        return tag;
    }

    public int getStunde() {
        return stunde;
    }

    public String getStundenName() {
        return this.gibStundenName(stunde);
    }

    public String getNotiz() {
        return notiz;
    }

    public boolean getSchriftlich() {
        return schriftlich;
    }

    public String getKlasse() {
        return klasse;
    }

    public String getKlausurString() {
        String teil1 = kuerzel.substring(0, 2);
        String teil2 = kuerzel.substring(2, 4);
        if (teil1.charAt(1) != ' ')
            teil1 += ' ';
        if (teil2.charAt(0) == 'L')
            teil2 = "L";
        return teil1 + teil2 + " " + lehrer + " " + klasse;
    }
}