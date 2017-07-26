package de.slg.messenger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.slg.leoapp.List;
import de.slg.leoapp.User;
import de.slg.leoapp.Utils;

import static de.slg.messenger.DBConnection.DBHelper.CHAT_DELETED;
import static de.slg.messenger.DBConnection.DBHelper.CHAT_ID;
import static de.slg.messenger.DBConnection.DBHelper.CHAT_MUTE;
import static de.slg.messenger.DBConnection.DBHelper.CHAT_NAME;
import static de.slg.messenger.DBConnection.DBHelper.CHAT_TYPE;
import static de.slg.messenger.DBConnection.DBHelper.MESSAGE_DATE;
import static de.slg.messenger.DBConnection.DBHelper.MESSAGE_DELETED;
import static de.slg.messenger.DBConnection.DBHelper.MESSAGE_ID;
import static de.slg.messenger.DBConnection.DBHelper.MESSAGE_READ;
import static de.slg.messenger.DBConnection.DBHelper.MESSAGE_TEXT;
import static de.slg.messenger.DBConnection.DBHelper.TABLE_ASSOZIATION;
import static de.slg.messenger.DBConnection.DBHelper.TABLE_CHATS;
import static de.slg.messenger.DBConnection.DBHelper.TABLE_MESSAGES;
import static de.slg.messenger.DBConnection.DBHelper.TABLE_MESSAGES_UNSEND;
import static de.slg.messenger.DBConnection.DBHelper.TABLE_USERS;
import static de.slg.messenger.DBConnection.DBHelper.USER_ID;
import static de.slg.messenger.DBConnection.DBHelper.USER_KLASSE;
import static de.slg.messenger.DBConnection.DBHelper.USER_NAME;
import static de.slg.messenger.DBConnection.DBHelper.USER_PERMISSION;

public class DBConnection {
    private final SQLiteDatabase database;

    public DBConnection(Context context) {
        DBHelper helper = new DBHelper(context, 2);
        database = helper.getWritableDatabase();
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
            values.put(MESSAGE_READ, m.uid != Utils.getUserID() ? 0 : 1);
            values.put(MESSAGE_DELETED, 0);
            insert(TABLE_MESSAGES, values);
        }
    }

    private Message getLastMessage(int cid) {
        String[] columns = {MESSAGE_ID, MESSAGE_TEXT, MESSAGE_DATE, USER_ID, MESSAGE_READ};
        String selection = CHAT_ID + " = " + cid + " AND " + MESSAGE_DELETED + " = 0";
        Cursor cursor = database.query(TABLE_MESSAGES, columns, selection, null, null, null, MESSAGE_DATE + " DESC", "1");
        cursor.moveToFirst();
        Message m = null;
        if (cursor.getCount() > 0) {
            m = new Message(cursor.getInt(0), cursor.getString(1), cursor.getLong(2), cid, cursor.getInt(3), cursor.getInt(4) == 1);
            m.setUname(getUname(m.uid));
        }
        cursor.close();
        return m;
    }

    Message[] getMessagesFromChat(int cid) {
        String[] columns = {MESSAGE_ID, MESSAGE_TEXT, MESSAGE_DATE, USER_ID, MESSAGE_READ};
        String condition = CHAT_ID + " = " + cid + " AND " + MESSAGE_DELETED + " = 0";
        Cursor cursor = query(TABLE_MESSAGES, columns, condition, MESSAGE_DATE);
        List<Message> list = new List<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Message m = new Message(cursor.getInt(0), cursor.getString(1), cursor.getLong(2), cid, cursor.getInt(3), cursor.getInt(4) == 1);
            m.setUname(getUname(m.uid));
            list.append(m);
        }
        cursor.close();
        columns = new String[]{MESSAGE_ID, MESSAGE_TEXT};
        cursor = query(TABLE_MESSAGES_UNSEND, columns, condition.substring(0, condition.indexOf(" AND")), MESSAGE_ID);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Message m = new Message(cursor.getInt(0), cursor.getString(1), 0, cid, Utils.getUserID(), false);
            m.setUname(Utils.getUserName());
            list.append(m);
        }
        cursor.close();
        return list.fill(new Message[list.length()]);
    }

    public Message[] getUnreadMessages() {
        String table = TABLE_MESSAGES + ", " + TABLE_CHATS;
        String[] columns = {MESSAGE_TEXT, MESSAGE_DATE, TABLE_MESSAGES + "." + CHAT_ID, USER_ID};
        String selection = MESSAGE_DATE + " > " + Utils.getLatestMessageDate() + " AND " +
                USER_ID + " != " + Utils.getUserID() + " AND " +
                TABLE_MESSAGES + "." + CHAT_ID + " = " + TABLE_CHATS + "." + CHAT_ID + " AND " +
                CHAT_MUTE + " = 0";
        Cursor cursor = query(table, columns, selection, TABLE_MESSAGES + "." + CHAT_ID + ", " + MESSAGE_DATE);
        Message[] array = new Message[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext()) {
            array[i] = new Message(0, cursor.getString(0), cursor.getLong(1), cursor.getInt(2), cursor.getInt(3), false);
            array[i].setUname(getUname(cursor.getInt(3)));
        }
        cursor.close();
        return array;
    }

    public boolean hasUnreadMessages() {
        Cursor cursor = query(TABLE_MESSAGES, new String[]{MESSAGE_ID}, MESSAGE_DATE + " > " + Utils.getLatestMessageDate() + " AND " + USER_ID + " != " + Utils.getUserID(), null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    public long getLatestDateInDB() {
        String table = TABLE_MESSAGES + ", " + TABLE_CHATS;
        String[] columns = {MESSAGE_DATE};
        String selection = USER_ID + " != " + Utils.getUserID() + " AND " +
                TABLE_MESSAGES + "." + CHAT_ID + " = " + TABLE_CHATS + "." + CHAT_ID + " AND " +
                CHAT_MUTE + " = 0";
        Cursor cursor = query(table, columns, selection, MESSAGE_DATE + " DESC");
        cursor.moveToFirst();
        long l = 0;
        if (cursor.getCount() > 0)
            l = cursor.getLong(0);
        cursor.close();
        return l;
    }

    void setMessagesRead(int cid) {
        database.execSQL("UPDATE " + TABLE_MESSAGES + " SET " + MESSAGE_READ + " = 1 WHERE " + CHAT_ID + " = " + cid);
    }

    void insertUnsendMessage(String mtext, int cid) {
        ContentValues values = new ContentValues();
        values.put(MESSAGE_TEXT, mtext);
        values.put(CHAT_ID, cid);
        insert(TABLE_MESSAGES_UNSEND, values);
    }

    public Message[] getUnsendMessages() {
        Cursor cursor = query(TABLE_MESSAGES_UNSEND, new String[]{MESSAGE_ID, MESSAGE_TEXT, CHAT_ID}, null, null);
        Message[] array = new Message[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext()) {
            array[i] = new Message(cursor.getInt(0), cursor.getString(1), 0, cursor.getInt(2), 0, false);
        }
        cursor.close();
        return array;
    }

    public void removeUnsendMessage(int mid) {
        database.delete(TABLE_MESSAGES_UNSEND, MESSAGE_ID + " = " + mid, null);
    }

    private boolean contains(Message m) {
        Cursor cursor = query(TABLE_MESSAGES, new String[]{MESSAGE_ID}, MESSAGE_ID + " = " + m.mid, null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    void deleteMessage(int mid) {
        ContentValues values = new ContentValues();
        values.put(MESSAGE_DELETED, 1);
        database.update(TABLE_MESSAGES, values, MESSAGE_ID + " = " + mid, null);
    }

    //User
    public void insertUser(User u) {
        if (u != null && !contains(u)) {
            ContentValues values = new ContentValues();
            values.put(USER_ID, u.uid);
            values.put(USER_NAME, u.uname);
            values.put(USER_KLASSE, u.ustufe);
            values.put(USER_PERMISSION, u.upermission);
            insert(TABLE_USERS, values);
        }
    }

    User[] getUsers() {
        String[] columns = {USER_ID, USER_NAME, USER_KLASSE, USER_PERMISSION};
        Cursor cursor = query(TABLE_USERS, columns, null, USER_ID);
        User[] array = new User[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext()) {
            array[i] = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
        }
        cursor.close();
        return array;
    }

    private String getUname(int uid) {
        if (uid == Utils.getUserID())
            return Utils.getUserName();
        Cursor cursor = query(TABLE_USERS, new String[]{USER_NAME}, USER_ID + " = " + uid, null);
        String erg = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            erg = cursor.getString(0);
        }
        cursor.close();
        return erg;
    }

    private boolean contains(User u) {
        Cursor cursor = query(TABLE_USERS, new String[]{USER_ID}, USER_ID + " = " + u.uid, null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    //Chat
    public void insertChat(Chat c) {
        if (c != null && !contains(c)) {
            ContentValues values = new ContentValues();
            values.put(CHAT_ID, c.cid);
            values.put(CHAT_NAME, c.cname);
            values.put(CHAT_TYPE, c.ctype.toString());
            values.put(CHAT_DELETED, 0);
            values.put(CHAT_MUTE, 0);
            insert(TABLE_CHATS, values);
        }
    }

    Chat[] getChats(boolean withDeleted) {
        String[] columns = {CHAT_ID, CHAT_NAME, CHAT_MUTE, CHAT_TYPE};
        String selection = null;
        if (!withDeleted)
            selection = CHAT_DELETED + " = 0";
        Cursor cursor = query(TABLE_CHATS, columns, selection, CHAT_ID);
        List<Chat> list = new List<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Chat current = new Chat(cursor.getInt(0), cursor.getString(1), cursor.getInt(2) == 1, Chat.Chattype.valueOf(cursor.getString(3).toUpperCase()));
            list.append(current);
            current.setLetzteNachricht(getLastMessage(current.cid));
            if (current.ctype.equals(Chat.Chattype.PRIVATE)) {
                String[] split = current.cname.split(" - ");
                if (split[0].equals("" + Utils.getUserID())) {
                    current.cname = getUname(Integer.parseInt(split[1]));
                } else {
                    current.cname = getUname(Integer.parseInt(split[0]));
                }
            }
        }
        cursor.close();
        for (int limit = list.length(); limit > 0; limit--) {
            int iMax = 0;
            for (list.toFirst();
                 iMax < limit - 1 && (list.getContent().m == null || list.getContent().mute);
                 iMax++, list.next())
                ;
            Chat max = list.getContent();
            for (int i = iMax; max.m != null && i < limit; list.next(), i++) {
                if (!list.getContent().mute && list.getContent().m != null && list.getContent().m.mdate.after(max.m.mdate)) {
                    max = list.getContent();
                    iMax = i;
                }
            }
            list.toIndex(iMax);
            list.remove();
            list.append(max);
        }
        return list.fill(new Chat[list.length()]);
    }

    Chat getChatWith(int uid) {
        Chat[] chats = getChats(true);
        String uname = getUname(uid);
        for (Chat c : chats)
            if (c.cname.equals(uname)) {
                restoreChat(c.cid);
                return c;
            }
        return null;
    }

    void setChatname(Chat chat) {
        if (chat.ctype.equals(Chat.Chattype.PRIVATE)) {
            String[] split = chat.cname.split(" - ");
            if (split[0].equals("" + Utils.getUserID())) {
                chat.cname = getUname(Integer.parseInt(split[1]));
            } else {
                chat.cname = getUname(Integer.parseInt(split[0]));
            }
        }
    }

    private boolean contains(Chat c) {
        Cursor cursor = query(TABLE_CHATS, new String[]{CHAT_ID}, CHAT_ID + " = " + c.cid, null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    void deleteChat(int cid) {
        ContentValues values = new ContentValues();
        values.put(CHAT_DELETED, 1);
        database.update(TABLE_CHATS, values, CHAT_ID + " = " + cid, null);
        Message[] messages = getMessagesFromChat(cid);
        for (Message m : messages) {
            deleteMessage(m.mid);
        }
    }

    private void restoreChat(int cid) {
        ContentValues values = new ContentValues();
        values.put(CHAT_DELETED, 0);
        database.update(TABLE_CHATS, values, CHAT_ID + " = " + cid, null);
    }

    void muteChat(int cid, boolean mute) {
        ContentValues values = new ContentValues();
        values.put(CHAT_MUTE, mute ? 1 : 0);
        database.update(TABLE_CHATS, values, CHAT_ID + " = " + cid, null);
    }

    //Assoziation
    public void insertAssoziation(Assoziation a) {
        if (a != null) {
            ContentValues values = new ContentValues();
            values.put(CHAT_ID, a.cid);
            values.put(USER_ID, a.uid);
            insert(TABLE_ASSOZIATION, values);
        }
    }

    boolean userInChat(int uid, int cid) {
        String[] columns = {USER_ID};
        String condition = CHAT_ID + " = " + cid + " AND " + USER_ID + " = " + uid;
        Cursor cursor = query(TABLE_ASSOZIATION, columns, condition, null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    void removeUserFormChat(int uid, int cid) {
        database.delete(TABLE_ASSOZIATION, USER_ID + " = " + uid + " AND " + CHAT_ID + " = " + cid, null);
    }

    User[] getUsersInChat(Chat c, boolean meInclusive) {
        boolean meIs = false;
        User[] users = getUsers();
        List<User> list = new List<>();
        String[] columns = {TABLE_ASSOZIATION + "." + USER_ID};
        String condition = TABLE_ASSOZIATION + "." + CHAT_ID + " = " + c.cid + " AND " + TABLE_USERS + "." + USER_ID + " = " + TABLE_ASSOZIATION + "." + USER_ID;
        Cursor cursor = query(TABLE_ASSOZIATION + ", " + TABLE_USERS, columns, condition, TABLE_USERS + "." + USER_NAME);
        cursor.moveToFirst();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int current = cursor.getInt(0);
            if (current != Utils.getUserID()) {
                for (User user : users)
                    if (user.uid == current) {
                        list.append(user);
                        break;
                    }
            } else
                meIs = true;
        }
        if (meInclusive && meIs) {
            list.append(Utils.getCurrentUser());
        }
        User[] array = new User[list.length()];
        list.fill(array);
        cursor.close();
        return array;
    }

    User[] getUsersNotInChat(Chat c) {
        User[] u = getUsers();
        List<User> list = new List<>();
        list.adapt(u);
        User[] uoc = getUsersInChat(c, false);
        for (User anUoc : uoc) {
            int current = anUoc.uid;
            for (list.toFirst(); list.hasAccess(); list.next())
                if (list.getContent().uid == current) {
                    list.remove();
                    break;
                }
        }
        User[] array = new User[list.length()];
        list.fill(array);
        return array;
    }


    private Cursor query(String table, String[] columns, String selection, String orderBy) {
        return database.query(table, columns, selection, null, null, null, orderBy);
    }

    private void insert(String table, ContentValues values) {
        database.insert(table, null, values);
    }

    public void clearTable() {
        database.execSQL("DELETE FROM " + DBHelper.TABLE_ASSOZIATION);
    }

    public class DBHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "messenger";
        static final String TABLE_MESSAGES = "messages";
        static final String MESSAGE_ID = "mid";
        static final String MESSAGE_TEXT = "mtext";
        static final String MESSAGE_DATE = "mdate";
        static final String MESSAGE_READ = "mgelesen";
        static final String MESSAGE_DELETED = "mdeleted";

        static final String TABLE_CHATS = "chats";
        static final String CHAT_ID = "cid";
        static final String CHAT_NAME = "cname";
        static final String CHAT_TYPE = "ctype";
        static final String CHAT_DELETED = "cdeleted";
        static final String CHAT_MUTE = "cmute";

        static final String TABLE_ASSOZIATION = "assoziation";

        static final String TABLE_USERS = "users";
        static final String USER_ID = "uid";
        static final String USER_NAME = "uname";
        static final String USER_KLASSE = "uklasse";
        static final String USER_PERMISSION = "upermission";
        static final String TABLE_MESSAGES_UNSEND = "messages_unsend";

        DBHelper(Context context, int version) {
            super(context, DATABASE_NAME, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.wtf("DBHelper", "Datenbank wird erstellt");
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
                e.printStackTrace();
            }
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHATS + " (" +
                        CHAT_ID + " INTEGER PRIMARY KEY, " +
                        CHAT_NAME + " TEXT NOT NULL, " +
                        CHAT_TYPE + " TEXT NOT NULL, " +
                        CHAT_DELETED + " INTEGER NOT NULL, " +
                        CHAT_MUTE + " INTEGER NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ASSOZIATION + " (" +
                        CHAT_ID + " INTEGER NOT NULL, " +
                        USER_ID + " INTEGER NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                        USER_ID + " INTEGER PRIMARY KEY, " +
                        USER_NAME + " TEXT NOT NULL, " +
                        USER_KLASSE + " TEXT, " +
                        USER_PERMISSION + " INTEGER NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGES_UNSEND + " (" +
                        MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MESSAGE_TEXT + " TEXT NOT NULL, " +
                        CHAT_ID + " INTEGER NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL("DROP TABLE " + TABLE_MESSAGES);
                db.execSQL("DROP TABLE " + TABLE_CHATS);
                db.execSQL("DROP TABLE " + TABLE_ASSOZIATION);
                db.execSQL("DROP TABLE " + TABLE_USERS);
                db.execSQL("DROP TABLE " + TABLE_MESSAGES_UNSEND);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            onCreate(db);
        }
    }
}