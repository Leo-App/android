package de.slg.messenger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Message {
    public final int messageId;
    public final String messageText;
    public final Date sendDate;
    public final int chatId;
    public final int senderId;
    public boolean read;

    public String senderName = null;

    public Message(int messageId, String messageText, Date sendDate, int chatId, int senderId, boolean read) {
        this.messageId = messageId;
        this.messageText = "" + messageText;
        this.senderId = senderId;
        this.sendDate = sendDate;
        this.chatId = chatId;
        this.read = read;
    }

    public Message(int messageId, String messageText, long sendDate, int chatId, int senderId, boolean read) {
        this.messageId = messageId;
        this.messageText = "" + messageText;
        this.senderId = senderId;
        this.sendDate = new Date(sendDate);
        this.chatId = chatId;
        this.read = read;
    }

    @Override
    public String toString() {
        if (senderName != null)
            return senderName + ": " + messageText;
        return messageText;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public boolean allAttributesSet() {
        return messageId > 0 && messageText != null && sendDate != null && chatId > 0 && senderId > 0;
    }

    public String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy");
        if (gleicherTag(sendDate))
            simpleDateFormat = new SimpleDateFormat("'Heute'");
        else if (vorherigerTag(sendDate))
            simpleDateFormat = new SimpleDateFormat("'Gestern'");
        else if (gleichesJahr(sendDate))
            simpleDateFormat = new SimpleDateFormat("dd.MM");
        return simpleDateFormat.format(sendDate);
    }

    private boolean gleichesJahr(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    private boolean gleicherTag(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    private boolean vorherigerTag(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        c2.add(Calendar.DAY_OF_MONTH, 1);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    public String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(sendDate);
    }
}