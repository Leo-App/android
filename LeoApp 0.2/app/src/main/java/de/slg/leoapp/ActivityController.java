package de.slg.leoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.slg.essensqr.EssensQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
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

public class ActivityController {
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

    public void setContext(Context context) {
        this.context = context;
    }

    public SharedPreferences getPreferences() {
        if (preferences == null && getContext() != null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        }
        return preferences;
    }

    public DBConnection getMessengerDataBase() {
        if (dbConnection == null)
            dbConnection = new DBConnection(getContext());
        return dbConnection;
    }

    public StundenplanDB getStundplanDataBase() {
        if (stundenplanDB == null)
            stundenplanDB = new StundenplanDB(getContext());
        return stundenplanDB;
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

    public MessengerActivity getMessengerActivity() {
        return messengerActivity;
    }

    public ChatActivity getChatActivity() {
        return chatActivity;
    }

    public KlausurplanActivity getKlausurplanActivity() {
        return klausurplanActivity;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public SchwarzesBrettActivity getSchwarzesBrettActivity() {
        return schwarzesBrettActivity;
    }

    public StundenplanActivity getStundenplanActivity() {
        return stundenplanActivity;
    }

    public StundenplanBildActivity getStundenplanBildActivity() {
        return stundenplanBildActivity;
    }

    public AuswahlActivity getAuswahlActivity() {
        return auswahlActivity;
    }

    public StimmungsbarometerActivity getStimmungsbarometerActivity() {
        return stimmungsbarometerActivity;
    }

    public ChatEditActivity getChatEditActivity() {
        return chatEditActivity;
    }

    public AddGroupChatActivity getAddGroupChatActivity() {
        return addGroupChatActivity;
    }

    public EssensQRActivity getEssensQRActivity() {
        return essensQRActivity;
    }

    public PreferenceActivity getPreferenceActivity() {
        return preferenceActivity;
    }

    public NotificationPreferenceActivity getNotificationPreferenceActivity() {
        return notificationPreferenceActivity;
    }

    public void registerReceiveService(ReceiveService service) {
        receiveService = service;
    }

    public ReceiveService getReceiveService() {
        return receiveService;
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

        if (getMessengerDataBase() != null) {
            getMessengerDataBase().close();
            dbConnection = null;
        }
        if (getStundplanDataBase() != null) {
            getStundplanDataBase().close();
            stundenplanDB = null;
        }
    }
}
