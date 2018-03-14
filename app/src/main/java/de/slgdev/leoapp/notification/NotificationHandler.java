package de.slgdev.leoapp.notification;

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

import de.slgdev.essensbons.utility.EssensbonUtils;
import de.slgdev.klausurplan.utility.Klausur;
import de.slgdev.klausurplan.utility.KlausurplanUtils;
import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorKlausurplan;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSchwarzesBrett;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slgdev.leoapp.sqlite.SQLiteConnectorUmfragen;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.ActivityStatus;
import de.slgdev.messenger.utility.Chat;
import de.slgdev.messenger.utility.Message;
import de.slgdev.schwarzes_brett.utility.SchwarzesBrettUtils;
import de.slgdev.startseite.activity.MainActivity;
import de.slgdev.stimmungsbarometer.activity.AbstimmActivity;
import de.slgdev.stimmungsbarometer.utility.StimmungsbarometerUtils;
import de.slgdev.stundenplan.utility.Fach;
import de.slgdev.umfragen.utility.UmfragenUtils;

/**
 * NotificationHandler.
 * <p>
 * Allgemeine Klasse zum Verwalten aller Notifications. Ermöglicht das zentrale Ändern einzelner Notifications, sowie einen globalen Zugriff.
 *
 * @author Gianni
 * @version 2017.0212
 * @since 0.6.7
 */
@SuppressWarnings("WeakerAccess")
public abstract class NotificationHandler {

    public static final int ID_ESSENSBONS         = 101;
    public static final int ID_KLAUSURPLAN        = 777;
    public static final int ID_MESSENGER          = 5453;
    public static final int ID_SCHWARZES_BRETT    = 287;
    public static final int ID_UMFRAGEN           = 314;
    public static final int ID_STIMMUNGSBAROMETER = 234;
    public static final int ID_STUNDENPLAN        = 222;

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

    public static class EssensbonsNotification implements LeoAppNotification {
        private Context      context;
        private Notification notification;

        public EssensbonsNotification() {
            this.context = Utils.getContext();
            create();

            if (notificationManager == null)
                initNotificationManager();
        }

        private void create() {
            Intent resultIntent = new Intent(context, MainActivity.class)
                    .putExtra("start_intent", ID_ESSENSBONS);

            resultIntent.setAction(String.valueOf(System.currentTimeMillis()));

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            Utils.getContext(),
                            0,
                            resultIntent,
                            PendingIntent.FLAG_ONE_SHOT
                    );

            notification = new NotificationCompat.Builder(context, "leoapp_notification")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setSmallIcon(R.mipmap.icon_essensbons)
                    .setVibrate(new long[]{200})
                    .setContentTitle(Utils.getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setContentText(Utils.getString(R.string.notification_summary_notif))
                    .setContentIntent(resultPendingIntent)
                    .build();
        }

        public void send() {
            if (isActive())
                notificationManager.notify(ID_ESSENSBONS, notification);
        }

        private boolean isActive() {
            return isEnabled();
        }

        public static boolean isEnabled() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_essensqr", true) && EssensbonUtils.isLoggedIn();
        }

    }

    public static class KlausurplanNotification implements LeoAppNotification {
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

            resultIntent.setAction(String.valueOf(System.currentTimeMillis()));

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_ONE_SHOT);

            notification = new NotificationCompat.Builder(context, "leoapp_notification")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setSmallIcon(R.mipmap.icon_klausurplan)
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
            if (isEnabled() && KlausurplanUtils.databaseExists(context)) {
                SQLiteConnectorKlausurplan db = new SQLiteConnectorKlausurplan(context);
                Klausur                    k  = db.getNextExam();
                db.close();
                return k != null;
            }
            return false;
        }

        public static boolean isEnabled() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_test", true);
        }
    }

    public static class MessengerNotification implements LeoAppNotification {
        private Context      context;
        private Notification notification;

        public MessengerNotification() {
            this.context = Utils.getContext();
            create();

            if (notificationManager == null)
                initNotificationManager();
        }

        private void create() {
            Intent resultIntent = new Intent(context, MainActivity.class)
                    .putExtra("start_intent", ID_MESSENGER);

            resultIntent.setAction(String.valueOf(System.currentTimeMillis()));

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_ONE_SHOT
                    );

            notification = new NotificationCompat.Builder(context, "leoapp_notification")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setVibrate(new long[]{500, 250, 500})
                    .setSmallIcon(R.mipmap.icon_messenger)
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

            return style;
        }

        private Message[] getUnreadMessages() {
            return Utils.getController().getMessengerDatabase().getUnreadMessages();
        }

        private boolean isActive() {
            return isEnabled()
                    && Utils.getController().getMessengerActivity() == null
                    && Utils.getController().getMessengerDatabase().hasUnreadMessages();
        }

        public static boolean isEnabled() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_messenger", true);
        }

    }

    public static class SchwarzesBrettNotification implements LeoAppNotification {

        private static long         latest;
        private        Context      context;
        private        Notification notification;

        public SchwarzesBrettNotification() {
            this.context = Utils.getContext();
            create();

            if (notificationManager == null)
                initNotificationManager();
        }

        private void create() {
            Intent resultIntent = new Intent(context, MainActivity.class)
                    .putExtra("start_intent", ID_SCHWARZES_BRETT);

            resultIntent.setAction(String.valueOf(System.currentTimeMillis()));

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_ONE_SHOT);

            notification = new NotificationCompat.Builder(context, "leoapp_notification")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setSmallIcon(R.mipmap.icon_schwarzes_brett)
                    .setVibrate(new long[]{200})
                    .setAutoCancel(true)
                    .setContentTitle(context.getString(R.string.new_entries))
                    .setContentText(context.getString(R.string.news_pin_board))
                    .setContentIntent(resultPendingIntent)
                    .build();
        }

        public void send() {
            if (isActive()) {
                notificationManager.notify(ID_SCHWARZES_BRETT, notification);
                SchwarzesBrettUtils.notifiedSchwarzesBrett(latest);
            }
        }

        private boolean isActive() {
            return isEnabled()
                    && Utils.isVerified()
                    && hasUnreadNews()
                    && (Utils.getController().getSchwarzesBrettActivity() == null
                    || Utils.getController().getSchwarzesBrettActivity().getStatus() != ActivityStatus.ACTIVE);
        }

        public static boolean isEnabled() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_news", true);
        }

        private boolean hasUnreadNews() {
            SQLiteConnectorSchwarzesBrett db = new SQLiteConnectorSchwarzesBrett(Utils.getContext());

            if (!db.getDatabaseAvailable())
                return false;

            SQLiteDatabase dbh = db.getReadableDatabase();
            latest = db.getLatestEntryDate(dbh);

            dbh.close();
            db.close();

            return latest > SchwarzesBrettUtils.getLatestSchwarzesBrettDate();
        }
    }

    public static class UmfrageNotification implements LeoAppNotification {

        private static long         latest;
        private        Context      context;
        private        Notification notification;

        public UmfrageNotification() {
            this.context = Utils.getContext();
            create();

            if (notificationManager == null)
                initNotificationManager();
        }

        private void create() {
            Intent resultIntent = new Intent(context, MainActivity.class)
                    .putExtra("start_intent", ID_UMFRAGEN);

            resultIntent.setAction(String.valueOf(System.currentTimeMillis()));

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_ONE_SHOT
                    );

            notification = new NotificationCompat.Builder(context, "leoapp_notification")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setSmallIcon(R.mipmap.icon_umfragen)
                    .setVibrate(new long[]{200})
                    .setAutoCancel(true)
                    .setContentTitle(context.getString(R.string.new_survey))
                    .setContentText(context.getString(R.string.vote_survey))
                    .setContentIntent(resultPendingIntent)
                    .build();
        }

        public void send() {
            if (isActive()) {
                notificationManager.notify(ID_UMFRAGEN, notification);
                UmfragenUtils.notifiedSurvey(latest);
            }
        }

        private boolean isActive() {
            return isEnabled()
                    && Utils.isVerified()
                    && hasUnreadNews()
                    && (Utils.getController().getSurveyActivity() == null
                    || Utils.getController().getSurveyActivity().getStatus() != ActivityStatus.ACTIVE);
        }

        public static boolean isEnabled() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_survey_students", true);
        }

        private boolean hasUnreadNews() {
            SQLiteConnectorUmfragen db = new SQLiteConnectorUmfragen(context);

            if (!db.getDatabaseAvailable())
                return false;

            SQLiteDatabase dbh = db.getReadableDatabase();
            latest = db.getLatestSurveyDate(dbh);

            dbh.close();
            db.close();

            return latest > UmfragenUtils.getLatestSurveyDate();
        }
    }

    public static class StimmungsbarometerNotification implements LeoAppNotification {

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

            resultIntent.setAction(String.valueOf(System.currentTimeMillis()));

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_ONE_SHOT
                    );

            notification = new NotificationCompat.Builder(context, "leoapp_notification")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setSmallIcon(R.mipmap.icon_stimmungsbarometer)
                    .setVibrate(new long[]{200})
                    .setContentTitle(context.getString(R.string.not_voted))
                    .setContentText(context.getString(R.string.vote_now))
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .build();
        }

        public void send() {
            if (isActive())
                notificationManager.notify(ID_STIMMUNGSBAROMETER, notification);
        }

        private boolean isActive() {
            return isEnabled() && StimmungsbarometerUtils.syncVote();
        }

        public static boolean isEnabled() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_survey", false);
        }

    }

    public static class StundenplanNotification implements LeoAppNotification {

        private Context      context;
        private Notification notificationStundenplan;

        public StundenplanNotification() {
            this.context = Utils.getContext();
            create();

            if (notificationManager == null)
                initNotificationManager();
        }

        private void create() {
            String msg = getNotificationText();

            notificationStundenplan = new NotificationCompat.Builder(context, "leoapp_notification")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(getNotificationIcon())
                    .setSmallIcon(R.mipmap.icon_stundenplan)
                    .setVibrate(new long[]{200})
                    .setContentTitle(context.getString(R.string.lessons_tommorrow))
                    .setContentText(msg)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(msg))
                    .setAutoCancel(true)
                    .build();
        }

        public void send() {
            if (isActive())
                notificationManager.notify(ID_STUNDENPLAN, notificationStundenplan);
        }

        private boolean isActive() {
            SQLiteConnectorStundenplan db = new SQLiteConnectorStundenplan(Utils.getContext());
            return isEnabled() && getNextDayOfWeek() <= 5 && db.hatGewaehlt();
        }

        public static boolean isEnabled() {
            return Utils.getController().getPreferences().getBoolean("pref_key_notification_schedule", false);
        }

        private String getNotificationText() {
            SQLiteConnectorStundenplan database = new SQLiteConnectorStundenplan(Utils.getContext());
            StringBuilder              builder  = new StringBuilder();
            Fach[]                     lessons  = database.getChosenSubjectsAtDay(getNextDayOfWeek());

            if (lessons.length == 0)
                return Utils.getString(R.string.none);
//TODO Architecture
            for (int i = 0; i < lessons.length; i++) {
                if (lessons[i].getName().length() > 0 && (i == 0 || !lessons[i].getName().equals(lessons[i - 1].getName()))) {
                    builder.append(lessons[i].getName());
                    if (i < lessons.length - 1) {
                        builder.append(", ");
                    }
                }
            }

            if(builder.charAt(builder.length()-3) == ',')
                builder.deleteCharAt(builder.length()-3);

            if(builder.charAt(builder.length()-2) == ',')
                builder.deleteCharAt(builder.length()-2);


            database.close();

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