package de.slg.umfragen;

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
final class Survey {

    final int id;
    final int remoteId;

    final String title;
    final String description;
    final String to;

    final String[] answers;
    final boolean  multiple;
    final boolean  voted;

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
    Survey(int id, int remoteId, String title, String description, boolean multiple, boolean voted, String to, ArrayList<String> answers) {
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
    int getAnswerAmount() {
        return answers.length;
    }
}
