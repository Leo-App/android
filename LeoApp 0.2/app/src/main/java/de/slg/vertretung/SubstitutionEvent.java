package de.slg.vertretung;

import java.util.Date;

class SubstitutionEvent {

    private String school;
    private String schoolClass;
    private String period;
    private String teacher;
    private String subject;
    private String substitute;
    private String room;
    private boolean cancellation;
    private String note;
    private Date date;


    private static final String[] DAYS = new String[]{"mon", "tue", "wed", "thu", "fri"};

    /**
     * Constructor. Sets all attributes to null and false respectively.
     */
    public SubstitutionEvent() {
        school = null;
        schoolClass = null;
        period = null;
        teacher = null;
        subject = null;
        substitute = null;
        room = null;
        cancellation = false;
        note = null;
        date = null;
    }

    /**
     * Constructor. Sets all attributes of the Substitution Event.
     *
     * @param pSchool       Name of the school
     * @param pSchoolClass  School class(e.g. 8a, Q1)
     * @param pPeriod       Periods(e.g. 3./4., 3-6)
     * @param pTeacher      Identification code of the teacher, who would normally teach this class(e.g. TST)
     * @param pSubject      Identification code of the subject, which is affected(e.g. KU)
     * @param pSubstitute   Identification code of the substitute(e.g. SUB)
     * @param pRoom         Room(e.g. A04)
     * @param pCancellation Boolean whether the class is cancelled or not
     * @param pNote         Note
     * @param pDate         The date at which the event takes place
     */
    public SubstitutionEvent(String pSchool, String pSchoolClass, String pPeriod, String pTeacher, String pSubject, String pSubstitute, String pRoom, boolean pCancellation, String pNote, Date pDate) {
        setSchool(pSchool);
        setSchoolClass(pSchoolClass);
        setPeriod(pPeriod);
        setTeacher(pTeacher);
        setSubject(pSubject);
        setSubstitute(pSubstitute);
        setRoom(pRoom);
        setCancellation(pCancellation);
        setNote(pNote);
        setDate(pDate);
    }

    /**
     * Gets the name of the school
     *
     * @return Name of the school
     */
    public String getSchool() {
        return school;
    }

    /**
     * Sets the name of the school
     *
     * @param pSchool Name of the school
     */
    private void setSchool(String pSchool) {
        school = pSchool;
    }

    /**
     * Gets the school class
     *
     * @return School class
     */
    public String getSchoolClass() {
        return schoolClass;
    }

    /**
     * Sets the school class
     *
     * @param pSchoolClass School class
     */
    private void setSchoolClass(String pSchoolClass) {
        pSchoolClass = pSchoolClass.replaceAll("\\(", "");
        pSchoolClass = pSchoolClass.replaceAll("\\)", "");
        schoolClass = pSchoolClass;
    }

    /**
     * Sets the period
     *
     * @param pPeriod Period
     */
    private void setPeriod(String pPeriod) {
        period = pPeriod;
    }

    /**
     * Gets the Identification code of the teacher, who would normally teach this class
     *
     * @return Identification code of the teacher, who would normally teach this class
     */
    public String getTeacher() {
        return teacher;
    }

    /**
     * Sets the Identification code of the teacher, who would normally teach this class
     *
     * @param pTeacher Identification code of the teacher, who would normally teach this class
     */
    private void setTeacher(String pTeacher) {
        teacher = pTeacher;
    }

    /**
     * Gets the Identification code of the subject
     *
     * @return Identification code of the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the Identification code of the subject
     *
     * @param pSubject Identification code of the subject
     */
    private void setSubject(String pSubject) {
        subject = pSubject;
    }

    /**
     * Gets the Identification Code of the substitute
     *
     * @return Identification Code of the substitute
     */
    public String getSubstitute() {
        return substitute;
    }

    /**
     * Sets the Identification Code of the substitute
     *
     * @param pSubstitute Identification Code of the substitute
     */
    private void setSubstitute(String pSubstitute) {
        substitute = pSubstitute;
    }

    /**
     * Gets the room
     *
     * @return Room
     */
    public String getRoom() {
        return room;
    }

    /**
     * Sets the room
     *
     * @param pRoom Room
     */
    private void setRoom(String pRoom) {
        room = pRoom;
    }

    /**
     * Gets whether the class is cancelled or not
     *
     * @return Boolean whether the class is cancelled or not
     */
    public boolean isCancellation() {
        return cancellation;
    }

    /**
     * Returns whether the class is Self Learning or not
     *
     * @return true in case of Self Learning, false otherwise
     */
    public boolean isSelfLearning() {
        return (getNote().toLowerCase().contains("selbstlernen") || getNote().toLowerCase().contains("evl"));
    }

    /**
     * Sets whether the class is cancelled or not
     *
     * @param pCancellation Boolean whether the class is cancelled or not
     */
    private void setCancellation(boolean pCancellation) {
        cancellation = pCancellation;
    }

    /**
     * Gets the note
     *
     * @return Note
     */
    private String getNote() {
        return note;
    }

    /**
     * Sets the note
     *
     * @param pNote Note
     */
    private void setNote(String pNote) {
        note = pNote;
    }

    /**
     * Gets the date at which the event takes place
     *
     * @return Date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date at which the event takes place
     *
     * @param pDate Date
     */
    private void setDate(Date pDate) {
        date = pDate;
    }

    public String getPeriod() {
        return period;
    }

}
