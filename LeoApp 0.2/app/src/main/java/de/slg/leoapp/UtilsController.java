package de.slg.leoapp;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

import de.slg.essensqr.EssensQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoview.ActionLogActivity;
import de.slg.leoview.ActivityStatus;
import de.slg.messenger.AddGroupChatActivity;
import de.slg.messenger.ChatActivity;
import de.slg.messenger.ChatEditActivity;
import de.slg.messenger.DBConnection;
import de.slg.messenger.MessengerActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.AuswahlActivity;
import de.slg.stundenplan.StundenplanActivity;
import de.slg.stundenplan.StundenplanBildActivity;
import de.slg.stundenplan.StundenplanDB;

/**
 * UtilsController
 *
 * Verwaltet Activities, Services u.Ä.
 *
 * @author Moritz
 * @since 0.5.5
 * @version 2017.2610
 */
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

    private PreferenceActivity             preferenceActivity;
    private NotificationPreferenceActivity notificationPreferenceActivity;

    //Datenbankverwaltungen
    private DBConnection  dbConnection;
    private StundenplanDB stundenplanDB;

    private ReceiveService receiveService;

    /**
     * Liefert ein Context-Objekt.
     *
     * @return Context-Objekt.
     * @see Utils#getContext()
     */
    Context getContext() {
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
        }
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

    public DBConnection getMessengerDatabase() {
        if (dbConnection == null)
            dbConnection = new DBConnection(getContext());
        return dbConnection;
    }

    public StundenplanDB getStundenplanDatabase() {
        if (stundenplanDB == null)
            stundenplanDB = new StundenplanDB(getContext());
        return stundenplanDB;
    }

    public ActionLogActivity getActiveActivity() {
        if (mainActivity.getStatus() == ActivityStatus.ACTIVE) {
            return mainActivity;
        } else if (messengerActivity.getStatus() == ActivityStatus.ACTIVE) {
            return messengerActivity;
        } else if (chatActivity.getStatus() == ActivityStatus.ACTIVE) {
            return chatActivity;
        } else if (chatEditActivity.getStatus() == ActivityStatus.ACTIVE) {
            return chatEditActivity;
        } else if (addGroupChatActivity.getStatus() == ActivityStatus.ACTIVE) {
            return addGroupChatActivity;
        } else if (schwarzesBrettActivity.getStatus() == ActivityStatus.ACTIVE) {
            return schwarzesBrettActivity;
        } else if (stimmungsbarometerActivity.getStatus() == ActivityStatus.ACTIVE) {
            return stimmungsbarometerActivity;
        } else if (stundenplanActivity.getStatus() == ActivityStatus.ACTIVE) {
            return stundenplanActivity;
        } else if (stundenplanBildActivity.getStatus() == ActivityStatus.ACTIVE) {
            return stundenplanBildActivity;
        } else if (auswahlActivity.getStatus() == ActivityStatus.ACTIVE) {
            return auswahlActivity;
        } else if (klausurplanActivity.getStatus() == ActivityStatus.ACTIVE) {
            return klausurplanActivity;
        } else if (essensQRActivity.getStatus() == ActivityStatus.ACTIVE) {
            return essensQRActivity;
        } else
            return null;
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

    public void registerEssensQRActivity(EssensQRActivity activity) {
        essensQRActivity = activity;
    }

    public void registerPreferenceActivity(PreferenceActivity activity) {
        preferenceActivity = activity;
    }

    public void registerNotificationPreferenceActivity(NotificationPreferenceActivity activity) {
        notificationPreferenceActivity = activity;
    }

    /**
     *
     * @return Aktive Messenger-Activity, null wenn nicht aktiv.
     */
    public MessengerActivity getMessengerActivity() {
        return messengerActivity;
    }

    /**
     *
     * @return Aktive Chat-Activity, null wenn nicht aktiv.
     */
    public ChatActivity getChatActivity() {
        return chatActivity;
    }

    /**
     *
     * @return Aktive Klausurplan-Activity, null wenn nicht aktiv.
     */
    public KlausurplanActivity getKlausurplanActivity() {
        return klausurplanActivity;
    }

    /**
     *
     * @return Aktive Main-Activity, null wenn nicht aktiv.
     */
    public MainActivity getMainActivity() {
        return mainActivity;
    }

    /**
     *
     * @return Aktive SchwarzesBrett-Activity, null wenn nicht aktiv.
     */
    public SchwarzesBrettActivity getSchwarzesBrettActivity() {
        return schwarzesBrettActivity;
    }

    /**
     *
     * @return Aktive Stundenplan-Activity, null wenn nicht aktiv.
     */
    public StundenplanActivity getStundenplanActivity() {
        return stundenplanActivity;
    }

    /**
     *
     * @return Aktive StundenplanBild-Activity (Anzeige des Stundenplans im .bmp Format)
     */
    public StundenplanBildActivity getStundenplanBildActivity() {
        return stundenplanBildActivity;
    }

    /**
     *
     * @return Aktive Auswahl-Activity (Auswahl der Fächer im Stundenplan)
     */
    public AuswahlActivity getAuswahlActivity() {
        return auswahlActivity;
    }

    /**
     *
     * @return Aktive Stimmungsbarometer-Activity, null wenn nicht aktiv.
     */
    public StimmungsbarometerActivity getStimmungsbarometerActivity() {
        return stimmungsbarometerActivity;
    }

    /**
     *
     * @return Aktive ChatEdit-Activity, null wenn nicht aktiv.
     */
    public ChatEditActivity getChatEditActivity() {
        return chatEditActivity;
    }

    /**
     *
     * @return Aktive Messenger-Activity, null wenn nicht aktiv.
     */
    public AddGroupChatActivity getAddGroupChatActivity() {
        return addGroupChatActivity;
    }

    /**
     *
     * @return Aktive QR-Activity (Anzeige der Essensbons)
     */
    public EssensQRActivity getEssensQRActivity() {
        return essensQRActivity;
    }

    /**
     *
     * @return Aktive Preference-Activity (Einstellungen)
     */
    public PreferenceActivity getPreferenceActivity() {
        return preferenceActivity;
    }

    /**
     *
     * @return Aktive BotificationPreference-Activity (Notification Einstellungen)
     */
    public NotificationPreferenceActivity getNotificationPreferenceActivity() {
        return notificationPreferenceActivity;
    }

    /**
     *
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

    public void closeAll() {
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
        if (getMainActivity() != null) {
            getMainActivity().finish();
        }

        if (getMessengerDatabase() != null) {
            getMessengerDatabase().close();
            dbConnection = null;
        }
        if (getStundenplanDatabase() != null) {
            getStundenplanDatabase().close();
            stundenplanDB = null;
        }
    }
}
