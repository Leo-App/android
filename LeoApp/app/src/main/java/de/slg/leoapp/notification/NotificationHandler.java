package de.slg.leoapp.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.klausurplan.utility.Klausur;
import de.slg.leoapp.R;
import de.slg.leoapp.sqlite.SQLiteConnectorKlausurplan;
import de.slg.leoapp.sqlite.SQLiteConnectorSchwarzesBrett;
import de.slg.leoapp.sqlite.SQLiteConnectorUmfragen;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.ActivityStatus;
import de.slg.messenger.utility.Chat;
import de.slg.messenger.utility.Message;
import de.slg.startseite.activity.MainActivity;
import de.slg.stimmungsbarometer.activity.AbstimmActivity;
import de.slg.stundenplan.utility.Fach;

/**
 * NotificationHandler.
 * <p>
 * Allgemeine Klasse zum Verwalten aller Notifications. Ermöglicht das zentrale Ändern einzelner Notifications, sowie einen globalen Zugriff.
 *
 * @author Gianni
 * @version 2017.0212
 * @since 0.6.7
 */

public class NotificationHandler {
    public static final int ID_ESSENSQR    = 101;
    public static final int ID_KLAUSURPLAN = 777;
    public static final int ID_MESSENGER   = 5453;
    public static final int ID_NEWS        = 287;
    public static final int ID_SURVEY      = 314;
    public static final int ID_BAROMETER   = 234;
    public static final int ID_STUNDENPLAN = 222;

    private static NotificationManager notificationManager;
    private static Bitmap              icon;

    private static Bitmap getNotificationIcon() {
        if (icon == null)
            icon = BitmapFactory.decodeResource(Utils.getContext().getResources(), R.mipmap.notification_leo);
        return icon;
    }

    private static void initNotificationManager() {
        notificationManager = (NotificationManager) Utils.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static class FoodmarkNotification {
        private Context      context;
        private Notification notification;

        public FoodmarkNotification() {
            this.context = Utils.getContext();
            create();

            if (notificationManager == null)
                initNotificationManager();
        }

        private void create() {
            Intent resultIntent = new Intent(context, MainActivity.class)
                    .putExtra("start_intent", ID_ESSENSQR);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            Utils.getContext(),
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            notification = new NotificationCompat.Builder(context)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setSmallIcon(R.drawable.qrcode)
                    .setVibrate(new long[]{200})
                    .setContentTitle(Utils.getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setContentText(Utils.getString(R.string.notification_summary_notif))
                    .setContentIntent(resultPendingIntent)
                    .build();
        }

        public void send() {
            if (isActive())
                notificationManager.notify(ID_ESSENSQR, notification);
        }

        private boolean isActive() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_essensqr", true);
        }
    }

    public static class KlausurplanNotification {
        private Context      context;
        private Notification notification;

        public KlausurplanNotification() {
            this.context = Utils.getContext();
            create();

            if (notificationManager == null)
                initNotificationManager();
        }

        private void create() {
            Intent resultIntent = new Intent(context, MainActivity.class)
                    .putExtra("start_intent", ID_KLAUSURPLAN);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            notification = new NotificationCompat.Builder(context)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setSmallIcon(R.drawable.icon_klausurplan)
                    .setVibrate(new long[]{200})
                    .setContentTitle(Utils.getString(R.string.title_testplan))
                    .setAutoCancel(true)
                    .setContentText(Utils.getString(R.string.notification_test_content))
                    .setContentIntent(resultPendingIntent)
                    .build();
        }

        public void send() {
            if (isActive())
                notificationManager.notify(ID_KLAUSURPLAN, notification);
        }

        private boolean isActive() {
            if (Utils.getController().getPreferences().getBoolean("pref_key_notification_test", true)) {
                SQLiteConnectorKlausurplan db = new SQLiteConnectorKlausurplan(context);
                Klausur                    k  = db.getNextExam();
                db.close();
                return k != null;
            }
            return false;
        }
    }

    public static class MessengerNotification {
        private static int          unreadMessages;
        private        Context      context;
        private        Notification notification;

        public MessengerNotification() {
            this.context = Utils.getContext();
            create();

            if (notificationManager == null)
                initNotificationManager();
        }

        private void create() {
            Intent resultIntent = new Intent(context, MainActivity.class)
                    .putExtra("start_intent", ID_MESSENGER);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            notification = new NotificationCompat.Builder(context)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setVibrate(new long[]{500, 250, 500})
                    .setSmallIcon(R.drawable.ic_question_answer_white_24dp)
                    .setContentIntent(resultPendingIntent)
                    .setContentTitle(Utils.getString(R.string.messenger_notification_title))
                    .setStyle(getStyle())
                    .build();
        }

        public void send() {
            if (isActive())
                notificationManager.notify(ID_MESSENGER, notification);
        }

        private NotificationCompat.InboxStyle getStyle() {
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle()
                    .setSummaryText(Utils.getController().getMessengerDatabase().getNotificationString())
                    .setBigContentTitle(Utils.getString(R.string.messenger_notification_title));

            for (Message m : getUnreadMessages()) {
                String line = m.uname;

                if (Utils.getController().getMessengerDatabase().getType(m.cid) == Chat.ChatType.GROUP) {
                    line += " @ " + m.cname;
                }

                line += ": " + m.mtext;

                style.addLine(line);
            }

            unreadMessages = getUnreadMessages().length;

            return style;
        }

        private Message[] getUnreadMessages() {
            return Utils.getController().getMessengerDatabase().getUnreadMessages();
        }

        private boolean isActive() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_messenger", true)
                    && Utils.getController().getMessengerActivity() == null
                    && Utils.getController().getMessengerDatabase().hasUnreadMessages()
                    && unreadMessages != getUnreadMessages().length;
        }
    }

    public static class NewsNotification {
        private static long         latest;
        private        Context      context;
        private        Notification notification;

        public NewsNotification() {
            this.context = Utils.getContext();
            create();

            if (notificationManager == null)
                initNotificationManager();
        }

        private void create() {
            Intent resultIntent = new Intent(context, MainActivity.class)
                    .putExtra("start_intent2", ID_NEWS);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            notification = new NotificationCompat.Builder(context)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setSmallIcon(R.drawable.ic_pin)
                    .setVibrate(new long[]{200})
                    .setAutoCancel(true)
                    .setContentTitle("Neue Einträge")
                    .setContentText("Es gibt Neuigkeiten am Schwarzen Brett")
                    .setContentIntent(resultPendingIntent)
                    .build();
        }

        public void send() {
            de.slg.schwarzes_brett.utility.Utils.notifiedSchwarzesBrett(latest);
            Utils.logError(ID_NEWS);
            if (isActive())
                notificationManager.notify(ID_NEWS, notification);
        }

        private boolean isActive() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_news", true)
                    && hasUnreadNews()
                    && (Utils.getController().getSchwarzesBrettActivity() == null
                    || Utils.getController().getSchwarzesBrettActivity().getStatus() != ActivityStatus.ACTIVE);
        }

        private boolean hasUnreadNews() {
            SQLiteConnectorSchwarzesBrett db = new SQLiteConnectorSchwarzesBrett(Utils.getContext());

            if (!db.getDatabaseAvailable())
                return false;

            SQLiteDatabase dbh = db.getReadableDatabase();
            latest = db.getLatestEntryDate(dbh);

            dbh.close();
            db.close();

            return latest > de.slg.schwarzes_brett.utility.Utils.getLatestSchwarzesBrettDate();
        }
    }

    public static class SurveyNotification {
        private static long         latest;
        private        Context      context;
        private        Notification notification;

        public SurveyNotification() {
            this.context = Utils.getContext();
            create();

            if (notificationManager == null)
                initNotificationManager();
        }

        private void create() {
            Intent resultIntent = new Intent(context, MainActivity.class)
                    .putExtra("start_intent", ID_SURVEY);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            notification = new NotificationCompat.Builder(context)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setSmallIcon(R.drawable.icon_survey)
                    .setVibrate(new long[]{200})
                    .setAutoCancel(true)
                    .setContentTitle("Neue Umfrage")
                    .setContentText("Stimme in der neuesten Umfrage ab")
                    .setContentIntent(resultPendingIntent)
                    .build();
        }

        public void send() {
            de.slg.umfragen.utility.Utils.notifiedSurvey(latest);
            if (isActive())
                notificationManager.notify(ID_SURVEY, notification);
        }

        private boolean isActive() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_survey_students", true)
                    && hasUnreadNews()
                    && (Utils.getController().getSurveyActivity() == null
                    || Utils.getController().getSurveyActivity().getStatus() != ActivityStatus.ACTIVE);
        }

        private boolean hasUnreadNews() {
            SQLiteConnectorUmfragen db = new SQLiteConnectorUmfragen(context);

            if (!db.getDatabaseAvailable())
                return false;

            SQLiteDatabase dbh = db.getReadableDatabase();
            latest = db.getLatestSurveyDate(dbh);

            dbh.close();
            db.close();

            return latest > de.slg.umfragen.utility.Utils.getLatestSurveyDate();
        }
    }

    public static class StimmungsbarometerNotification {
        private Context      context;
        private Notification notification;

        public StimmungsbarometerNotification() {
            this.context = Utils.getContext();
            create();

            if (notificationManager == null)
                initNotificationManager();
        }

        private void create() {
            Intent resultIntent = new Intent(context, AbstimmActivity.class);

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            notification = new NotificationCompat.Builder(context)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setSmallIcon(R.drawable.ic_insert_emoticon_white_24dp)
                    .setVibrate(new long[]{200})
                    .setContentTitle("Du hast noch nicht abgestimmt!")
                    .setContentText("Jetzt abstimmen")
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .build();
        }

        public void send() {
            if (isActive())
                notificationManager.notify(ID_BAROMETER, notification);
        }

        private boolean isActive() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_survey", false)
                    && de.slg.stimmungsbarometer.utility.Utils.syncVote();
        }
    }

    public static class TimetableNotification {
        private Context      context;
        private Notification notification;

        public TimetableNotification() {
            this.context = Utils.getContext();
            create();

            if (notificationManager == null)
                initNotificationManager();
        }

        private void create() {
            String msg = getNotificationText();

            notification = new NotificationCompat.Builder(context)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setSmallIcon(R.drawable.ic_event_white_24dp)
                    .setVibrate(new long[]{200})
                    .setContentTitle("Deine Stunden morgen:")
                    .setContentText(msg)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(msg))
                    .setAutoCancel(true)
                    .build();
        }

        public void send() {
            if (isActive())
                notificationManager.notify(ID_STUNDENPLAN, notification);
        }

        private boolean isActive() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_schedule", true) &&
                    getNextDayOfWeek() <= 5;
        }

        private String getNotificationText() {
            StringBuilder builder = new StringBuilder();
            Fach[]        lessons = Utils.getController().getStundenplanDatabase().gewaehlteFaecherAnTag(getNextDayOfWeek());

            if (lessons.length == 0)
                return Utils.getString(R.string.none);

            for (int i = 0; i < lessons.length; i++) {
                if (lessons[i].getName().length() > 0 && (i == 0 || !lessons[i].getName().equals(lessons[i - 1].getName()))) {
                    builder.append(lessons[i].getName());
                    if (i < lessons.length - 1) {
                        builder.append(", ");
                    }
                }
            }

            return builder.toString();
        }

        private int getNextDayOfWeek() {
            Calendar c = new GregorianCalendar();
            c.setTime(new Date());
            int i = c.get(Calendar.DAY_OF_WEEK);
            if (i == Calendar.SUNDAY)
                return 1;
            if (i == Calendar.MONDAY)
                return 2;
            if (i == Calendar.TUESDAY)
                return 3;
            if (i == Calendar.WEDNESDAY)
                return 4;
            if (i == Calendar.THURSDAY)
                return 5;
            return 6;
        }
    }
}
