package de.slg.leoapp.utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.slg.essensbons.activity.EssensQRActivity;
import de.slg.it_problem.activity.ITActivity;
import de.slg.klausurplan.activity.KlausurplanActivity;
import de.slg.leoapp.activity.NotificationPreferenceActivity;
import de.slg.leoapp.activity.PreferenceActivity;
import de.slg.leoapp.activity.ProfileActivity;
import de.slg.leoapp.service.ReceiveService;
import de.slg.leoapp.sqlite.SQLiteConnectorMessenger;
import de.slg.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slg.leoapp.view.ActionLogActivity;
import de.slg.leoapp.view.ActivityStatus;
import de.slg.messenger.activity.AddGroupChatActivity;
import de.slg.messenger.activity.ChatActivity;
import de.slg.messenger.activity.ChatEditActivity;
import de.slg.messenger.activity.MessengerActivity;
import de.slg.schwarzes_brett.activity.SchwarzesBrettActivity;
import de.slg.startseite.activity.MainActivity;
import de.slg.stimmungsbarometer.activity.StimmungsbarometerActivity;
import de.slg.stundenplan.activity.AuswahlActivity;
import de.slg.stundenplan.activity.StundenplanActivity;
import de.slg.stundenplan.activity.StundenplanBildActivity;
import de.slg.umfragen.activity.SurveyActivity;

/**
 * UtilsController
 * <p>
 * Verwaltet Activities, Services u.Ä.
 *
 * @author Moritz
 * @version 2017.2610
 * @since 0.5.5
 */
@SuppressWarnings({"WeakerAccess"})
public class UtilsController {
    private Context           context;
    private SharedPreferences preferences;

    //Activities
    private MainActivity mainActivity;

    private MessengerActivity    messengerActivity;
    private ChatActivity         chatActivity;
    private ChatEditActivity     chatEditActivity;
    private AddGroupChatActivity addGroupChatActivity;

    private SchwarzesBrettActivity schwarzesBrettActivity;

    private StimmungsbarometerActivity stimmungsbarometerActivity;

    private StundenplanActivity     stundenplanActivity;
    private StundenplanBildActivity stundenplanBildActivity;
    private AuswahlActivity         auswahlActivity;

    private KlausurplanActivity klausurplanActivity;

    private EssensQRActivity essensQRActivity;

    private SurveyActivity surveyActivity;

    private ITActivity itActivity;

    private PreferenceActivity             preferenceActivity;
    private NotificationPreferenceActivity notificationPreferenceActivity;
    private ProfileActivity                profileActivity;

    //Datenbankverwaltungen
    private SQLiteConnectorMessenger   SQLiteConnectorMessenger;
    private SQLiteConnectorStundenplan SQLiteConnectorStundenplan;

    private ReceiveService receiveService;
    private AlarmManager   alarmManager;

    private PendingIntent  foodmarkReference;
    private PendingIntent  timetableReference;
    private PendingIntent  klausurplanReference;
    private PendingIntent  stimmungsbarometerReference;

    /**
     * Liefert ein Context-Objekt.
     *
     * @return Context-Objekt.
     * @see Utils#getContext()
     */
    public Context getContext() {
        if (context != null) {
            return context;
        } else if (mainActivity != null) {
            return mainActivity;
        } else if (messengerActivity != null) {
            return messengerActivity;
        } else if (chatActivity != null) {
            return chatActivity;
        } else if (chatEditActivity != null) {
            return chatEditActivity;
        } else if (addGroupChatActivity != null) {
            return addGroupChatActivity;
        } else if (schwarzesBrettActivity != null) {
            return schwarzesBrettActivity;
        } else if (stimmungsbarometerActivity != null) {
            return stimmungsbarometerActivity;
        } else if (stundenplanActivity != null) {
            return stundenplanActivity;
        } else if (stundenplanBildActivity != null) {
            return stundenplanBildActivity;
        } else if (auswahlActivity != null) {
            return auswahlActivity;
        } else if (klausurplanActivity != null) {
            return klausurplanActivity;
        } else if (essensQRActivity != null) {
            return essensQRActivity;
        } else if (preferenceActivity != null) {
            return preferenceActivity;
        } else if (notificationPreferenceActivity != null) {
            return notificationPreferenceActivity;
        } else if (profileActivity != null) {
            return profileActivity;
        } else if (surveyActivity != null) {
            return surveyActivity;
        } else if (receiveService != null)
            return receiveService.getApplicationContext();
        return null;
    }

    /**
     * Setzt ein allgemeines Context-Objekt.
     *
     * @param context Context.
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Liefert eine Instanz der Einstellungen
     *
     * @return SharedPreferences-Objekt
     */
    public SharedPreferences getPreferences() {
        if (preferences == null && getContext() != null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        }
        return preferences;
    }

    /**
     * Liefert die SQLite Datenbank des Messengers.
     *
     * @return Messenger-Datenbank
     */
    public SQLiteConnectorMessenger getMessengerDatabase() {
        if (SQLiteConnectorMessenger == null)
            SQLiteConnectorMessenger = new SQLiteConnectorMessenger(getContext());
        return SQLiteConnectorMessenger;
    }

    /**
     * Liefert die SQLite Datenbank des Stundenplans.
     *
     * @return Stundenplan-Datenbank
     */
    public SQLiteConnectorStundenplan getStundenplanDatabase() {
        if (SQLiteConnectorStundenplan == null)
            SQLiteConnectorStundenplan = new SQLiteConnectorStundenplan(getContext());
        return SQLiteConnectorStundenplan;
    }

    /**
     * Liefert die aktive Activity zurück, null wenn keine aktiv ist.
     *
     * @return Aktive Activity
     * @see #hasActiveActivity()
     */
    public ActionLogActivity getActiveActivity() {
        if (mainActivity != null && mainActivity.getStatus() == ActivityStatus.ACTIVE) {
            return mainActivity;
        } else if (messengerActivity != null && messengerActivity.getStatus() == ActivityStatus.ACTIVE) {
            return messengerActivity;
        } else if (chatActivity != null && chatActivity.getStatus() == ActivityStatus.ACTIVE) {
            return chatActivity;
        } else if (chatEditActivity != null && chatEditActivity.getStatus() == ActivityStatus.ACTIVE) {
            return chatEditActivity;
        } else if (addGroupChatActivity != null && addGroupChatActivity.getStatus() == ActivityStatus.ACTIVE) {
            return addGroupChatActivity;
        } else if (schwarzesBrettActivity != null && schwarzesBrettActivity.getStatus() == ActivityStatus.ACTIVE) {
            return schwarzesBrettActivity;
        } else if (stimmungsbarometerActivity != null && stimmungsbarometerActivity.getStatus() == ActivityStatus.ACTIVE) {
            return stimmungsbarometerActivity;
        } else if (stundenplanActivity != null && stundenplanActivity.getStatus() == ActivityStatus.ACTIVE) {
            return stundenplanActivity;
        } else if (stundenplanBildActivity != null && stundenplanBildActivity.getStatus() == ActivityStatus.ACTIVE) {
            return stundenplanBildActivity;
        } else if (auswahlActivity != null && auswahlActivity.getStatus() == ActivityStatus.ACTIVE) {
            return auswahlActivity;
        } else if (klausurplanActivity != null && klausurplanActivity.getStatus() == ActivityStatus.ACTIVE) {
            return klausurplanActivity;
        } else if (essensQRActivity != null && essensQRActivity.getStatus() == ActivityStatus.ACTIVE) {
            return essensQRActivity;
        } else if (profileActivity != null && profileActivity.getStatus() == ActivityStatus.ACTIVE) {
            return profileActivity;
        } else if (surveyActivity != null && surveyActivity.getStatus() == ActivityStatus.ACTIVE) {
            return surveyActivity;
        } else {
            return null;
        }
    }

    /**
     * Liefert zurück, ob mindestens eine LeoApp-Activity auf dem Bildschirm angezeigt wird (Status = ACTIVE).
     *
     * @return Aktive Activity vorhanden?
     */
    public boolean hasActiveActivity() {
        return getActiveActivity() != null;
    }

    public void registerMessengerActivity(MessengerActivity activity) {
        messengerActivity = activity;
    }

    public void registerChatActivity(ChatActivity activity) {
        chatActivity = activity;
    }

    public void registerChatEditActivity(ChatEditActivity activity) {
        chatEditActivity = activity;
    }

    public void registerAddGroupChatActivity(AddGroupChatActivity activity) {
        addGroupChatActivity = activity;
    }

    public void registerStimmungsbarometerActivity(StimmungsbarometerActivity activity) {
        stimmungsbarometerActivity = activity;
    }

    public void registerStundenplanActivity(StundenplanActivity activity) {
        stundenplanActivity = activity;
    }

    public void registerStundenplanBildActivity(StundenplanBildActivity activity) {
        stundenplanBildActivity = activity;
    }

    public void registerAuswahlActivity(AuswahlActivity activity) {
        auswahlActivity = activity;
    }

    public void registerMainActivity(MainActivity activity) {
        mainActivity = activity;
    }

    public void registerSchwarzesBrettActivity(SchwarzesBrettActivity activity) {
        schwarzesBrettActivity = activity;
    }

    public void registerKlausurplanActivity(KlausurplanActivity activity) {
        klausurplanActivity = activity;
    }

    public void registerSurveyActivity(SurveyActivity activity) {
        surveyActivity = activity;
    }

    public void registerEssensbonActivity(EssensQRActivity activity) {
        essensQRActivity = activity;
    }

    public void registerPreferenceActivity(PreferenceActivity activity) {
        preferenceActivity = activity;
    }

    public void registerNotificationPreferenceActivity(NotificationPreferenceActivity activity) {
        notificationPreferenceActivity = activity;
    }

    public void registerProfileActivity(ProfileActivity activity) {
        profileActivity = activity;
    }

    public void registerITActivity(ITActivity activity) {
        itActivity = activity;
    }

    /**
     * @return Aktive Messenger-Activity, null wenn nicht aktiv.
     */
    public MessengerActivity getMessengerActivity() {
        return messengerActivity;
    }

    /**
     * @return Aktive Chat-Activity, null wenn nicht aktiv.
     */
    public ChatActivity getChatActivity() {
        return chatActivity;
    }

    /**
     * @return Aktive Klausurplan-Activity, null wenn nicht aktiv.
     */
    public KlausurplanActivity getKlausurplanActivity() {
        return klausurplanActivity;
    }

    /**
     * @return Aktive Main-Activity, null wenn nicht aktiv.
     */
    public MainActivity getMainActivity() {
        return mainActivity;
    }

    /**
     * @return Aktive SchwarzesBrett-Activity, null wenn nicht aktiv.
     */
    public SchwarzesBrettActivity getSchwarzesBrettActivity() {
        return schwarzesBrettActivity;
    }

    /**
     * @return Aktive Stundenplan-Activity, null wenn nicht aktiv.
     */
    public StundenplanActivity getStundenplanActivity() {
        return stundenplanActivity;
    }

    /**
     * @return Aktive StundenplanBild-Activity (Anzeige des Stundenplans im .bmp Format)
     */
    public StundenplanBildActivity getStundenplanBildActivity() {
        return stundenplanBildActivity;
    }

    /**
     * @return Aktive Auswahl-Activity (Auswahl der Fächer im Stundenplan)
     */
    public AuswahlActivity getAuswahlActivity() {
        return auswahlActivity;
    }

    /**
     * @return Aktive Stimmungsbarometer-Activity, null wenn nicht aktiv.
     */
    public StimmungsbarometerActivity getStimmungsbarometerActivity() {
        return stimmungsbarometerActivity;
    }

    /**
     * @return Aktive ChatEdit-Activity, null wenn nicht aktiv.
     */
    public ChatEditActivity getChatEditActivity() {
        return chatEditActivity;
    }

    /**
     * @return Aktive Messenger-Activity, null wenn nicht aktiv.
     */
    public AddGroupChatActivity getAddGroupChatActivity() {
        return addGroupChatActivity;
    }

    /**
     * @return Aktive QR-Activity (Anzeige der Essensbons)
     */
    public EssensQRActivity getEssensQRActivity() {
        return essensQRActivity;
    }

    /**
     * @return Aktive Preference-Activity (Einstellungen)
     */
    public PreferenceActivity getPreferenceActivity() {
        return preferenceActivity;
    }

    /**
     * @return Aktive NotificationPreference-Activity (Notification Einstellungen)
     */
    public NotificationPreferenceActivity getNotificationPreferenceActivity() {
        return notificationPreferenceActivity;
    }

    /**
     * @return Aktive Profil-Activity
     */
    public SurveyActivity getSurveyActivity() {
        return surveyActivity;
    }

    /**
     * @return Aktive Profil-Activity
     */
    public ProfileActivity getProfileActivity() {
        return profileActivity;
    }

    public ITActivity getItActivity() {
        return itActivity;
    }

    /**
     * @return Laufender Receive-Service, null wenn nicht aktiv.
     */
    public ReceiveService getReceiveService() {
        return receiveService;
    }

    /**
     * Registriert neuen ReceiveService.
     *
     * @param service ReceiveService.
     */
    public void registerReceiveService(ReceiveService service) {
        receiveService = service;
    }

    public void registerAlarmManager(AlarmManager alarmManager) {
        this.alarmManager = alarmManager;
    }

    public void registerFoodmarkNotificationReference(PendingIntent reference) {
        foodmarkReference = reference;
    }

    public void registerKlausurplanNotificationReference(PendingIntent reference) {
        klausurplanReference = reference;
    }

    public void registerTimetableNotificationReference(PendingIntent reference) {
        timetableReference = reference;
    }

    public void registerStimmungsbarometerNotificationReference(PendingIntent reference) {
        stimmungsbarometerReference = reference;
    }

    public PendingIntent getStimmungsbarometerReference() {
        return stimmungsbarometerReference;
    }

    public PendingIntent getFoodmarkReference() {
        return foodmarkReference;
    }

    public PendingIntent getTimetableReference() {
        return timetableReference;
    }

    public PendingIntent getKlausurplanReference() {
        return klausurplanReference;
    }

    public AlarmManager getAlarmManager() {
        return alarmManager;
    }

    /**
     * Schließt alle Activities (Status = DESTROYED).
     */
    public void closeActivities() {
        if (getChatEditActivity() != null) {
            getChatEditActivity().finish();
        }
        if (getChatActivity() != null) {
            getChatActivity().finish();
        }
        if (getAddGroupChatActivity() != null) {
            getAddGroupChatActivity().finish();
        }
        if (getMessengerActivity() != null) {
            getMessengerActivity().finish();
        }
        if (getAuswahlActivity() != null) {
            getAuswahlActivity().finish();
        }
        if (getStundenplanBildActivity() != null) {
            getStundenplanBildActivity().finish();
        }
        if (getStundenplanActivity() != null) {
            getStundenplanActivity().finish();
        }
        if (getSchwarzesBrettActivity() != null) {
            getSchwarzesBrettActivity().finish();
        }
        if (getStimmungsbarometerActivity() != null) {
            getStimmungsbarometerActivity().finish();
        }
        if (getEssensQRActivity() != null) {
            getEssensQRActivity().finish();
        }
        if (getKlausurplanActivity() != null) {
            getKlausurplanActivity().finish();
        }
        if (getNotificationPreferenceActivity() != null) {
            getNotificationPreferenceActivity().finish();
        }
        if (getPreferenceActivity() != null) {
            getPreferenceActivity().finish();
        }
        if (getProfileActivity() != null) {
            getProfileActivity().finish();
        }
        if (getMainActivity() != null) {
            getMainActivity().finish();
        }
        if (getSurveyActivity() != null) {
            getSurveyActivity().finish();
        }
    }

    /**
     * Schließt offene SQLite-Datenbankverbindungen.
     */
    public void closeDatabases() {
        if (SQLiteConnectorMessenger != null) {
            SQLiteConnectorMessenger.close();
            SQLiteConnectorMessenger = null;
        }
        if (SQLiteConnectorStundenplan != null) {
            SQLiteConnectorStundenplan.close();
            SQLiteConnectorStundenplan = null;
        }
    }

    /**
     * Beendet alle Services.
     */
    public void closeServices() {
        if (getReceiveService() != null) {
            getReceiveService().stopSelf();
            receiveService = null;
        }
    }
}
