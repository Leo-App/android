package de.slg.umfragen.utility;

import java.util.ArrayList;

/**
 * Survey
 * <p>
 * Verwaltungsklasse für das Umfragefeature. POJO mit allen Informationen über eine Umfrage.
 *
 * @author Gianni
 * @version 2017.1111
 * @since 0.5.9
 */
public final class Survey {

    public final int      remoteId;
    public final String   title;
    public final String   description;
    public final String   to;
    public final String[] answers;
    public final boolean  multiple;
    final        int      id;
    public       boolean  voted;

    /**
     * Konstruktor. Instanziiert die Attribute der Umfrage mit den entsprechenden Parametern.
     *
     * @param id          Die lokale ID der Umfrage auf dem Endgerät
     * @param remoteId    Die globale ID der Umfrage
     * @param title       Der Titel der Umfrage
     * @param description Die Beschreibung der Umfrage
     * @param multiple    Checkboxen oder Radiobuttons?
     * @param voted       Hat der verifizierte User bereits abgestimmt?
     * @param to          Adressat der Umfrage
     * @param answers     Alle Antwortmöglichkeiten
     */
    public Survey(int id, int remoteId, String title, String description, boolean multiple, boolean voted, String to, ArrayList<String> answers) {
        this.id = id;
        this.remoteId = remoteId;
        this.title = title;
        this.description = description;
        this.multiple = multiple;
        this.voted = voted;
        this.to = to;
        this.answers = answers.toArray(new String[0]);
    }

    /**
     * Gibt die Anzahl der Antwortmöglichkeiten zurück.
     *
     * @return Anzahl Antwortmöglichkeiten
     */
    public int getAnswerAmount() {
        return answers.length;
    }
}
