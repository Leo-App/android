package de.slgdev.leoapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;
import de.slgdev.messenger.utility.Assoziation;
import de.slgdev.messenger.utility.Chat;
import de.slgdev.messenger.utility.Message;
import de.slgdev.messenger.utility.MessengerUtils;

/**
 * Jede Methode tut das, was der Name sagt! Aufschlussreiches Javadoc!
 */
public class SQLiteConnectorMessenger extends SQLiteOpenHelper {

    private final SQLiteDatabase database;

    private static final String DATABASE_NAME = "messenger";

    private static final String TABLE_ASSOZIATION     = "assoziation";
    private static final String TABLE_CHATS           = "chats";
    private static final String TABLE_MESSAGES        = "messages";
    private static final String TABLE_MESSAGES_QUEUED = "messages_unsend";
    private static final String TABLE_USERS           = "users";

    private static final String CHAT_ID      = "cid";
    private static final String CHAT_NAME    = "cname";
    private static final String CHAT_TYPE    = "ctype";
    private static final String CHAT_DELETED = "cdeleted";
    private static final String CHAT_MUTE    = "cmute";

    private static final String MESSAGE_ID      = "mid";
    private static final String MESSAGE_TEXT    = "mtext";
    private static final String MESSAGE_DATE    = "mdate";
    private static final String MESSAGE_READ    = "mgelesen";
    private static final String MESSAGE_DELETED = "mdeleted";

    private static final String USER_ID          = "uid";
    private static final String USER_NAME        = "uname";
    private static final String USER_DEFAULTNAME = "udefaultname";
    private static final String USER_STUFE       = "ustufe";
    private static final String USER_PERMISSION  = "upermission";

    private static final int version = 4;

    public SQLiteConnectorMessenger(Context context) {
        super(context, DATABASE_NAME, null, version);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Utils.logDebug("Datenbank wird erstellt");
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGES + " (" +
                    MESSAGE_ID + " INTEGER PRIMARY KEY, " +
                    MESSAGE_TEXT + " TEXT NOT NULL, " +
                    MESSAGE_DATE + " TEXT NOT NULL, " +
                    CHAT_ID + " INTEGER NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    MESSAGE_READ + " INTEGER NOT NULL, " +
                    MESSAGE_DELETED + " INTEGER NOT NULL)");
        } catch (SQLException e) {
            Utils.logError(e);
        }
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHATS + " (" +
                    CHAT_ID + " INTEGER PRIMARY KEY, " +
                    CHAT_NAME + " TEXT NOT NULL, " +
                    CHAT_TYPE + " TEXT NOT NULL, " +
                    CHAT_DELETED + " INTEGER NOT NULL, " +
                    CHAT_MUTE + " INTEGER NOT NULL)");
        } catch (SQLException e) {
            Utils.logError(e);
        }
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ASSOZIATION + " (" +
                    CHAT_ID + " INTEGER NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL)");
        } catch (SQLException e) {
            Utils.logError(e);
        }
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                    USER_ID + " INTEGER PRIMARY KEY, " +
                    USER_NAME + " TEXT NOT NULL, " +
                    USER_DEFAULTNAME + " TEXT NOT NULL, " +
                    USER_STUFE + " TEXT, " +
                    USER_PERMISSION + " INTEGER NOT NULL)");
        } catch (SQLException e) {
            Utils.logError(e);
        }
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGES_QUEUED + " (" +
                    MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MESSAGE_TEXT + " TEXT NOT NULL, " +
                    CHAT_ID + " INTEGER NOT NULL)");
        } catch (SQLException e) {
            Utils.logError(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clear();
    }

    @Override
    public void close() {
        database.close();
    }

    //Message

    public void insertMessage(Message m) {
        if (m != null && !contains(m)) {
            ContentValues values = new ContentValues();
            values.put(MESSAGE_ID, m.mid);
            values.put(MESSAGE_TEXT, m.mtext);
            values.put(MESSAGE_DATE, m.mdate.getTime());
            values.put(CHAT_ID, m.cid);
            values.put(USER_ID, m.uid);
            values.put(MESSAGE_READ, m.uid == Utils.getUserID() || m.cid == MessengerUtils.currentlyDisplayedChat() ? 1 : 0);
            values.put(MESSAGE_DELETED, 0);
            insert(TABLE_MESSAGES, values);
            if (m.uid == Utils.getUserID()) {
                values.clear();
                values.put(MESSAGE_READ, 1);
                update(TABLE_MESSAGES, values, MESSAGE_DATE + " < " + m.mdate.getTime() + " AND " + CHAT_ID + " = " + m.cid);
            }
        }
    }

    public Message[] getMessagesFromChat(int cid) {
        String[]      columns   = {MESSAGE_ID, MESSAGE_TEXT, MESSAGE_DATE, USER_ID, MESSAGE_READ};
        String        selection = CHAT_ID + " = " + cid + " AND " + MESSAGE_DELETED + " = 0";
        Cursor        cursor    = query(TABLE_MESSAGES, columns, selection, MESSAGE_DATE);
        List<Message> list      = new List<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list.append(new Message(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getLong(2),
                    cid,
                    cursor.getInt(3),
                    cursor.getInt(4) == 1,
                    getUname(cursor.getInt(3))));
        }
        cursor.close();
        columns = new String[]{MESSAGE_ID, MESSAGE_TEXT};
        cursor = query(TABLE_MESSAGES_QUEUED, columns, selection.substring(0, selection.indexOf(" AND")), MESSAGE_ID);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list.append(new Message(
                    cursor.getInt(0),
                    cursor.getString(1),
                    0,
                    cid,
                    Utils.getUserID()));
        }
        cursor.close();
        return list.fill(new Message[list.size()]);
    }

    public Message[] getUnreadMessages() {
        String   table   = TABLE_MESSAGES + ", " + TABLE_CHATS;
        String[] columns = {MESSAGE_TEXT, TABLE_MESSAGES + "." + CHAT_ID, CHAT_NAME, USER_ID};
        String selection = MESSAGE_READ + " = 0 AND " +
                USER_ID + " != " + Utils.getUserID() + " AND " +
                TABLE_MESSAGES + "." + CHAT_ID + " = " + TABLE_CHATS + "." + CHAT_ID + " AND " +
                CHAT_MUTE + " = 0 AND " + TABLE_MESSAGES + "." + CHAT_ID + " != " + MessengerUtils.currentlyDisplayedChat();
        Cursor    cursor = query(table, columns, selection, TABLE_MESSAGES + "." + CHAT_ID + ", " + MESSAGE_DATE);
        Message[] array  = new Message[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext()) {
            array[i] = new Message(cursor.getString(0), cursor.getInt(1), cursor.getString(2), getUname(cursor.getInt(3)));
        }
        cursor.close();
        return array;
    }

    public boolean hasUnreadMessages() {
        Cursor  cursor = query(TABLE_MESSAGES, new String[]{MESSAGE_ID}, MESSAGE_READ + " = 0 AND " + USER_ID + " != " + Utils.getUserID() + " AND " + CHAT_ID + " != " + MessengerUtils.currentlyDisplayedChat(), null);
        boolean b      = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    public String getNotificationString() {
        String   table   = TABLE_MESSAGES + ", " + TABLE_CHATS;
        String[] columns = {MESSAGE_ID, TABLE_MESSAGES + "." + CHAT_ID};
        String selection = MESSAGE_READ + " = 0 AND " +
                USER_ID + " != " + Utils.getUserID() + " AND " +
                TABLE_MESSAGES + "." + CHAT_ID + " = " + TABLE_CHATS + "." + CHAT_ID + " AND " +
                CHAT_MUTE + " = 0 AND " +
                TABLE_MESSAGES + "." + CHAT_ID + " != " + MessengerUtils.currentlyDisplayedChat();
        Cursor cursor = query(table, columns, selection, TABLE_MESSAGES + "." + CHAT_ID);
        cursor.moveToFirst();

        int mcount = cursor.getCount();

        if (mcount > 0) {

            int cprev  = cursor.getInt(1);
            int ccount = 1;
            for (cursor.moveToNext(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (cursor.getInt(1) != cprev) {
                    ccount++;
                    cprev = cursor.getInt(1);
                }
            }

            cursor.close();

            String notificationString = mcount + " neue Nachrichten";
            if (ccount > 1) {
                notificationString += " in " + ccount + " Chats.";
            } else {
                notificationString += ".";
            }

            return notificationString;
        }
        return "";
    }

    public void setMessagesRead(int cid) {
        ContentValues values = new ContentValues();
        values.put(MESSAGE_READ, 1);
        update(TABLE_MESSAGES, values, CHAT_ID + " = " + cid);
    }

    private boolean contains(Message m) {
        Cursor  cursor = query(TABLE_MESSAGES, new String[]{MESSAGE_ID}, MESSAGE_ID + " = " + m.mid, null);
        boolean b      = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    public void deleteMessage(int mid) {
        ContentValues values = new ContentValues();
        values.put(MESSAGE_DELETED, 1);
        update(TABLE_MESSAGES, values, MESSAGE_ID + " = " + mid);
    }

    public String getLatestMessage() {
        String[] columns = {"MAX(" + MESSAGE_DATE + ")"};
        Cursor   cursor  = query(TABLE_MESSAGES, columns, null, null);
        cursor.moveToFirst();
        String erg = null;
        if (cursor.getCount() > 0)
            erg = cursor.getString(0);
        cursor.close();
        if (erg == null)
            erg = "0";
        Utils.logDebug(erg);
        return erg;
    }

    public void deleteQueuedMessage(int mid) {
        delete(TABLE_MESSAGES_QUEUED, MESSAGE_ID + " = " + mid);
    }

    public void enqueueMessage(String mtext, int cid) {
        ContentValues values = new ContentValues();
        values.put(MESSAGE_TEXT, mtext);
        values.put(CHAT_ID, cid);
        insert(TABLE_MESSAGES_QUEUED, values);

        Utils.getController().getReceiveService().notifyQueuedMessages();
    }

    public boolean hasQueuedMessages() {
        Cursor  cursor = query(TABLE_MESSAGES_QUEUED, new String[]{MESSAGE_ID}, null, null);
        boolean b      = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    public Message[] getQueuedMessages() {
        Cursor    cursor = query(TABLE_MESSAGES_QUEUED, new String[]{MESSAGE_ID, MESSAGE_TEXT, CHAT_ID}, null, null);
        Message[] array  = new Message[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext()) {
            array[i] = new Message(cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
        }
        cursor.close();
        return array;
    }

    public void dequeueMessage(int mid) {
        delete(TABLE_MESSAGES_QUEUED, MESSAGE_ID + " = " + mid);
    }

    //User

    public void insertUser(User u) {
        if (u != null) {
            if (!contains(u)) {
                ContentValues values = new ContentValues();
                values.put(USER_ID, u.uid);
                values.put(USER_NAME, u.uname);
                values.put(USER_DEFAULTNAME, u.udefaultname.toLowerCase());
                values.put(USER_STUFE, u.ustufe);
                values.put(USER_PERMISSION, u.upermission);
                insert(TABLE_USERS, values);
            } else {
                ContentValues values = new ContentValues();
                values.put(USER_NAME, u.uname);
                values.put(USER_STUFE, u.ustufe);
                values.put(USER_PERMISSION, u.upermission);
                update(TABLE_USERS, values, USER_ID + " = " + u.uid);
            }
        }
    }

    public User[] getUsers() {
        String[] columns   = {USER_ID, USER_NAME, USER_STUFE, USER_PERMISSION, USER_DEFAULTNAME};
        String   selection = USER_ID + " != " + Utils.getUserID();
        Cursor   cursor    = query(TABLE_USERS, columns, selection, USER_STUFE + ", " + USER_DEFAULTNAME);
        User[]   array     = new User[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext()) {
            array[i] = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2).replace("TEA", Utils.getString(R.string.lehrer)), cursor.getInt(3), cursor.getString(4));
        }
        cursor.close();
        return array;
    }

    private String getUname(int uid) {
        if (uid == Utils.getUserID())
            return Utils.getUserName();
        Cursor cursor = query(TABLE_USERS, new String[]{USER_NAME}, USER_ID + " = " + uid, null);
        String erg    = null;
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            erg = cursor.getString(0);
        }
        cursor.close();
        return erg;
    }

    private boolean contains(User u) {
        Cursor  cursor = query(TABLE_USERS, new String[]{USER_ID}, USER_ID + " = " + u.uid, null);
        boolean b      = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    //Chat

    public void insertChat(Chat c) {
        if (c != null) {
            if (!contains(c)) {
                ContentValues values = new ContentValues();
                values.put(CHAT_ID, c.cid);
                values.put(CHAT_NAME, c.cname);
                values.put(CHAT_TYPE, c.ctype.toString());
                values.put(CHAT_DELETED, 0);
                values.put(CHAT_MUTE, 0);
                insert(TABLE_CHATS, values);
            } else {
                ContentValues values = new ContentValues();
                values.put(CHAT_NAME, c.cname);
                update(TABLE_CHATS, values, CHAT_ID + " = " + c.cid);
            }
        }
    }

    public Chat[] getChats() {
        Cursor cursor = database.rawQuery(
                "SELECT c." +        //Index
                CHAT_ID + ", c." +      // 0
                CHAT_NAME + ", c." +    // 1
                CHAT_TYPE + ", c." +    // 2
                CHAT_MUTE + ", m." +    // 3
                MESSAGE_ID + ", m." +   // 4
                MESSAGE_TEXT + ", m." + // 5
                MESSAGE_DATE + ", m." + // 6
                USER_ID + ", m." +      // 7
                MESSAGE_READ + ", u." + // 8
                USER_NAME +             // 9
                " FROM " + TABLE_CHATS + " c" +
                " LEFT JOIN (" + TABLE_MESSAGES + " m" +
                " INNER JOIN " + TABLE_USERS + " u" +
                " ON m." + USER_ID + " = u." + USER_ID + ")" +
                " ON c." + CHAT_ID + " = m." + CHAT_ID +
                " AND m." + MESSAGE_DATE + " = " +
                "(SELECT MAX(m2." + MESSAGE_DATE + ")" +
                " FROM Messages m2" +
                " WHERE m2." + CHAT_ID + " = c." + CHAT_ID +
                " AND m2." + MESSAGE_DELETED + " = 0)" +
                " WHERE c." + CHAT_DELETED + " = 0" +
                " GROUP BY c." + CHAT_ID +
                        " ORDER BY " + CHAT_MUTE + ", " + MESSAGE_DATE + " DESC",
                null
        );
        Chat[] chats  = new Chat[cursor.getCount()];
        int    i      = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext(), i++) {
            int           cid   = cursor.getInt(0);
            String        cname = cursor.getString(1);
            Chat.ChatType ctype = Chat.ChatType.valueOf(cursor.getString(2));
            boolean       cmute = cursor.getInt(3) == 1;

            if (ctype.equals(Chat.ChatType.PRIVATE) && cname.matches("[0-9]{4} - [0-9]{4}")) {
                String[] split = cname.split(" - ");
                if (split[0].equals(String.valueOf(Utils.getUserID()))) {
                    cname = getUname(Integer.parseInt(split[1]));
                } else {
                    cname = getUname(Integer.parseInt(split[0]));
                }
            }

            Message m = null;
            if (cursor.getInt(4) != 0) {
                m = new Message(cursor.getInt(4),
                        cursor.getString(5),
                        cursor.getLong(6),
                        cursor.getInt(0),
                        cursor.getInt(7),
                        cursor.getInt(8) == 1,
                        cursor.getString(9));
            }

            chats[i] = new Chat(cid, cname, ctype, cmute, m);
        }
        cursor.close();

        return chats;
    }

    public int getChatWith(int uid) {
        String cname1 = uid + " - " + Utils.getUserID(), cname2 = Utils.getUserID() + " - " + uid;
        Cursor cursor = query(TABLE_CHATS, new String[]{CHAT_ID}, '(' + CHAT_NAME + " = '" + cname1 + "' OR " + CHAT_NAME + " = '" + cname2 + "') AND " + CHAT_TYPE + " = '" + Chat.ChatType.PRIVATE.toString() + "'", null);
        cursor.moveToFirst();
        int cid = -1;
        if (cursor.getCount() > 0) {
            cid = cursor.getInt(0);
            restoreChat(cid);
        }
        cursor.close();
        return cid;
    }

    public boolean contains(Chat c) {
        Cursor  cursor = query(TABLE_CHATS, new String[]{CHAT_ID}, CHAT_ID + " = " + c.cid, null);
        boolean b      = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    public void deleteChat(int cid) {
        ContentValues values = new ContentValues();
        values.put(CHAT_DELETED, 1);
        update(TABLE_CHATS, values, CHAT_ID + " = " + cid);
        Message[] messages = getMessagesFromChat(cid);
        for (Message m : messages) {
            deleteMessage(m.mid);
        }
    }

    private void restoreChat(int cid) {
        ContentValues values = new ContentValues();
        values.put(CHAT_DELETED, 0);
        update(TABLE_CHATS, values, CHAT_ID + " = " + cid);
    }

    public void muteChat(int cid, boolean mute) {
        ContentValues values = new ContentValues();
        values.put(CHAT_MUTE, mute ? 1 : 0);
        update(TABLE_CHATS, values, CHAT_ID + " = " + cid);
    }

    public boolean isMute(int cid) {
        Cursor cursor = query(TABLE_CHATS, new String[]{CHAT_MUTE}, CHAT_ID + " = " + cid, null);
        cursor.moveToFirst();
        boolean b = cursor.getCount() > 0 && cursor.getInt(0) == 1;
        cursor.close();
        return b;
    }

    public Chat.ChatType getType(int cid) {
        Cursor cursor = query(TABLE_CHATS, new String[]{CHAT_TYPE}, CHAT_ID + " = " + cid, null);
        cursor.moveToFirst();
        Chat.ChatType type = Chat.ChatType.GROUP;
        if (cursor.getCount() > 0)
            type = Chat.ChatType.valueOf(cursor.getString(0));
        cursor.close();
        return type;
    }

    //Suchen

    public Object[] getSuchergebnisse(String suchbegriff) {
        Cursor   cursorChats = query(TABLE_CHATS, new String[]{CHAT_ID, CHAT_NAME, CHAT_MUTE}, CHAT_TYPE + " = '" + Chat.ChatType.GROUP.toString() + "' AND " + CHAT_NAME + " LIKE '%" + suchbegriff + "%'", CHAT_NAME);
        Cursor   cursorUsers = query(TABLE_USERS, new String[]{USER_ID, USER_NAME, USER_DEFAULTNAME, USER_STUFE}, USER_ID + " != " + Utils.getUserID() + " AND " + USER_NAME + " LIKE '%" + suchbegriff + "%' OR " + USER_DEFAULTNAME + " LIKE '%" + suchbegriff + "%'", USER_STUFE + ", " + USER_DEFAULTNAME);
        Chat[]   chats       = new Chat[cursorChats.getCount()];
        User[]   users       = new User[cursorUsers.getCount()];
        Object[] ergebnisse  = new Object[chats.length + users.length];
        cursorChats.moveToFirst();
        cursorUsers.moveToFirst();
        for (int i = 0; !cursorChats.isAfterLast(); cursorChats.moveToNext(), i++) {
            chats[i] = new Chat(cursorChats.getInt(0), cursorChats.getString(1), Chat.ChatType.GROUP, cursorChats.getInt(2) == 1);
        }
        for (int i = 0; !cursorUsers.isAfterLast(); cursorUsers.moveToNext(), i++) {
            users[i] = new User(cursorUsers.getInt(0), cursorUsers.getString(1), cursorUsers.getString(3).replace("Teacher", Utils.getString(R.string.lehrer)), 0, cursorUsers.getString(2));
        }
        cursorChats.close();
        cursorUsers.close();
        System.arraycopy(users, 0, ergebnisse, 0, users.length);
        System.arraycopy(chats, 0, ergebnisse, users.length, chats.length);
        return ergebnisse;
    }

    //Assoziation

    public void insertAssoziationen(List<Assoziation> assoziations) {
        Cursor cursor = query(TABLE_ASSOZIATION, new String[]{USER_ID, CHAT_ID}, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            boolean b   = false;
            int     uid = cursor.getInt(0), cid = cursor.getInt(1);
            for (Assoziation a : assoziations) {
                if (b = a.uid == uid && a.cid == cid) {
                    assoziations.remove();
                    break;
                }
            }
            if (!b)
                delete(TABLE_ASSOZIATION, USER_ID + " = " + uid + " AND " + CHAT_ID + " = " + cid);
        }
        cursor.close();
        for (Assoziation a : assoziations) {
            ContentValues values = new ContentValues();
            values.put(USER_ID, a.uid);
            values.put(CHAT_ID, a.cid);
            insert(TABLE_ASSOZIATION, values);
        }
    }

    public boolean userInChat(int uid, int cid) {
        String[] columns   = {USER_ID};
        String   selection = CHAT_ID + " = " + cid + " AND " + USER_ID + " = " + uid;
        Cursor   cursor    = query(TABLE_ASSOZIATION, columns, selection, null);
        boolean  b         = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    public User[] getUsersInChat(int cid) {
        String query  = "SELECT " + USER_ID + ", " + USER_NAME + ", " + USER_STUFE + ", " + USER_PERMISSION + ", " + USER_DEFAULTNAME + " FROM " + TABLE_USERS + " WHERE " + USER_ID + " IN (SELECT " + USER_ID + " FROM " + TABLE_ASSOZIATION + " WHERE " + CHAT_ID + " = " + cid + ")";
        Cursor cursor = rawQuery(query);
        User[] users  = new User[cursor.getCount()];
        int    i      = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext(), i++) {
            users[i] = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2).replace("TEA", Utils.getString(R.string.lehrer)), cursor.getInt(3), cursor.getString(4));
        }
        cursor.close();
        return users;
    }

    public User[] getUsersNotInChat(int cid) {
        String query  = "SELECT " + USER_ID + ", " + USER_NAME + ", " + USER_STUFE + ", " + USER_PERMISSION + ", " + USER_DEFAULTNAME + " FROM " + TABLE_USERS + " WHERE " + USER_ID + " NOT IN (SELECT " + USER_ID + " FROM " + TABLE_ASSOZIATION + " WHERE " + CHAT_ID + " = " + cid + ")";
        Cursor cursor = rawQuery(query);
        User[] users  = new User[cursor.getCount()];
        int    i      = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext(), i++) {
            users[i] = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2).replace("TEA", Utils.getString(R.string.lehrer)), cursor.getInt(3), cursor.getString(4));
        }
        cursor.close();
        return users;
    }

    //Datenbank-Interaktion

    private Cursor query(String table, String[] columns, String selection, String orderBy) {
        return database.query(table, columns, selection, null, null, null, orderBy, null);
    }

    private Cursor rawQuery(String query) {
        return database.rawQuery(query, null);
    }

    private void insert(String table, ContentValues values) {
        database.insert(table, null, values);
    }

    private void delete(String table, String where) {
        database.delete(table, where, null);
    }

    private void update(String table, ContentValues values, String where) {
        database.update(table, values, where, null);
    }

    public void clear() {
        try {
            database.execSQL("DROP TABLE " + TABLE_MESSAGES);
            database.execSQL("DROP TABLE " + TABLE_CHATS);
            database.execSQL("DROP TABLE " + TABLE_ASSOZIATION);
            database.execSQL("DROP TABLE " + TABLE_USERS);
            database.execSQL("DROP TABLE " + TABLE_MESSAGES_QUEUED);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onCreate(database);
    }
}