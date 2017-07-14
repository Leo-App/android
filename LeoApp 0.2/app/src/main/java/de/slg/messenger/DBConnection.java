package de.slg.messenger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import de.slg.leoapp.List;
import de.slg.leoapp.User;
import de.slg.leoapp.Utils;

public class DBConnection {
    private final SQLiteDatabase database;

    public DBConnection(Context context) {
        DBHelper helper = new DBHelper(context);
        database = helper.getWritableDatabase();
    }

    //Message
    public void insertMessage(Message m) {
        if (m != null && !contains(m)) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.MESSAGE_ID, m.mid);
            values.put(DBHelper.MESSAGE_TEXT, m.mtext);
            values.put(DBHelper.MESSAGE_DATE, m.mdate.getTime());
            values.put(DBHelper.CHAT_ID, m.cid);
            values.put(DBHelper.USER_ID, m.uid);
            values.put(DBHelper.MESSAGE_READ, m.uid != Utils.getUserID() ? 0 : 1);
            values.put(DBHelper.MESSAGE_DELETED, 0);
            insert(DBHelper.TABLE_MESSAGES, values);
        }
    }

    private Message getLastMessage(int cid) {
        String[] columns = {DBHelper.MESSAGE_ID, DBHelper.MESSAGE_TEXT, DBHelper.MESSAGE_DATE, DBHelper.USER_ID, DBHelper.MESSAGE_READ};
        String condition = DBHelper.CHAT_ID + " = " + cid + " AND " + DBHelper.MESSAGE_DELETED + " = 0";
        Cursor cursor = database.query(DBHelper.TABLE_MESSAGES, columns, condition, null, null, null, DBHelper.MESSAGE_DATE + " DESC", "1");
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
        String[] columns = {DBHelper.MESSAGE_ID, DBHelper.MESSAGE_TEXT, DBHelper.MESSAGE_DATE, DBHelper.USER_ID, DBHelper.MESSAGE_READ};
        String condition = DBHelper.CHAT_ID + " = " + cid + " AND " + DBHelper.MESSAGE_DELETED + " = 0";
        Cursor cursor = query(DBHelper.TABLE_MESSAGES, columns, condition, null, DBHelper.MESSAGE_DATE);
        List<Message> list = new List<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Message m = new Message(cursor.getInt(0), cursor.getString(1), cursor.getLong(2), cid, cursor.getInt(3), cursor.getInt(4) == 1);
            m.setUname(getUname(m.uid));
            list.append(m);
        }
        cursor.close();
        columns = new String[]{DBHelper.MESSAGE_ID, DBHelper.MESSAGE_TEXT};
        cursor = query(DBHelper.TABLE_MESSAGES_UNSEND, columns, condition.substring(0, condition.indexOf(" AND")), null, DBHelper.MESSAGE_ID);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Message m = new Message(cursor.getInt(0), cursor.getString(1), 0, cid, Utils.getUserID(), false);
            m.setUname(Utils.getUserName());
            list.append(m);
        }
        cursor.close();
        return list.fill(new Message[list.length()]);
    }

    public NotificationCompat.MessagingStyle.Message[] getUnreadMessages() {
        String[] columns = {DBHelper.MESSAGE_TEXT, DBHelper.MESSAGE_DATE, DBHelper.USER_ID};
        String condition = DBHelper.MESSAGE_DATE + " > " + Utils.getLatestMessageDate() + " AND " + DBHelper.USER_ID + " != " + Utils.getUserID();
        Cursor cursor = query(DBHelper.TABLE_MESSAGES, columns, condition, null, DBHelper.CHAT_ID + ", " + DBHelper.MESSAGE_DATE);
        NotificationCompat.MessagingStyle.Message[] array = new NotificationCompat.MessagingStyle.Message[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext())
            array[i] = new NotificationCompat.MessagingStyle.Message(cursor.getString(0), cursor.getLong(1), getUname(cursor.getInt(2)));
        cursor.close();
        return array;
    }

    public boolean hasUnreadMessages() {
        Cursor cursor = query(DBHelper.TABLE_MESSAGES, new String[]{DBHelper.MESSAGE_ID}, DBHelper.MESSAGE_DATE + " > " + Utils.getLatestMessageDate() + " AND " + DBHelper.USER_ID + " != " + Utils.getUserID(), null, null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    public long getLatestDateInDB() {
        Cursor cursor = database.query(DBHelper.TABLE_MESSAGES, new String[]{DBHelper.MESSAGE_DATE}, null, null, null, null, DBHelper.MESSAGE_DATE + " DESC", "1");
        cursor.moveToFirst();
        long l = 0;
        if (cursor.getCount() > 0)
            l = cursor.getLong(0);
        cursor.close();
        return l;
    }

    void setMessagesRead(int cid) {
        database.execSQL("UPDATE " + DBHelper.TABLE_MESSAGES + " SET " + DBHelper.MESSAGE_READ + " = 1 WHERE " + DBHelper.CHAT_ID + " = " + cid);
    }

    void insertUnsendMessage(String mtext, int cid) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.MESSAGE_TEXT, mtext);
        values.put(DBHelper.CHAT_ID, cid);
        insert(DBHelper.TABLE_MESSAGES_UNSEND, values);
    }

    public Message[] getUnsendMessages() {
        Cursor cursor = query(DBHelper.TABLE_MESSAGES_UNSEND, new String[]{DBHelper.MESSAGE_ID, DBHelper.MESSAGE_TEXT, DBHelper.CHAT_ID}, null, null, null);
        Message[] array = new Message[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext()) {
            array[i] = new Message(cursor.getInt(0), cursor.getString(1), 0, cursor.getInt(2), 0, false);
        }
        cursor.close();
        return array;
    }

    public void removeUnsendMessage(int mid) {
        database.execSQL("DELETE FROM " + DBHelper.TABLE_MESSAGES_UNSEND + " WHERE " + DBHelper.MESSAGE_ID + " = " + mid);
    }

    private boolean contains(Message m) {
        Cursor cursor = query(DBHelper.TABLE_MESSAGES, new String[]{DBHelper.MESSAGE_ID}, DBHelper.MESSAGE_ID + " = " + m.mid, null, null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    void deleteMessage(int mid) {
        database.execSQL("UPDATE " + DBHelper.TABLE_MESSAGES + " SET " + DBHelper.MESSAGE_DELETED + " = 1 WHERE " + DBHelper.MESSAGE_ID + " = " + mid);
    }

    //User
    public void insertUser(User u) {
        if (u != null && !contains(u)) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.USER_ID, u.uid);
            values.put(DBHelper.USER_NAME, u.uname);
            values.put(DBHelper.USER_KLASSE, u.ustufe);
            values.put(DBHelper.USER_PERMISSION, u.upermission);
            insert(DBHelper.TABLE_USERS, values);
        }
    }

    User[] getUsers() {
        String[] columns = {DBHelper.USER_ID, DBHelper.USER_NAME, DBHelper.USER_KLASSE, DBHelper.USER_PERMISSION};
        Cursor cursor = query(DBHelper.TABLE_USERS, columns, null, null, DBHelper.USER_ID);
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
        Cursor cursor = query(DBHelper.TABLE_USERS, new String[]{DBHelper.USER_NAME}, DBHelper.USER_ID + " = " + uid, null, null);
        String erg = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            erg = cursor.getString(0);
        }
        cursor.close();
        return erg;
    }

    private boolean contains(User u) {
        Cursor cursor = query(DBHelper.TABLE_USERS, new String[]{DBHelper.USER_ID}, DBHelper.USER_ID + " = " + u.uid, null, null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    //Chat
    public void insertChat(Chat c) {
        if (c != null && !contains(c)) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.CHAT_ID, c.cid);
            values.put(DBHelper.CHAT_NAME, c.cname);
            values.put(DBHelper.CHAT_TYPE, c.ctype.toString());
            values.put(DBHelper.CHAT_DELETED, 0);
            insert(DBHelper.TABLE_CHATS, values);
        }
    }

    Chat[] getChats() {
        String[] columns = {DBHelper.CHAT_ID, DBHelper.CHAT_NAME, DBHelper.CHAT_TYPE};
        Cursor cursor = query(DBHelper.TABLE_CHATS, columns, null, null, DBHelper.CHAT_ID);
        List<Chat> list = new List<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Chat current = new Chat(cursor.getInt(0), cursor.getString(1), Chat.Chattype.valueOf(cursor.getString(2).toUpperCase()));
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
            for (list.toFirst(); iMax < limit - 1 && list.getContent().m == null; iMax++, list.next())
                ;
            Chat max = list.getContent();
            for (int i = iMax; max.m != null && i < limit; list.next(), i++) {
                if (list.getContent().m != null && list.getContent().m.mdate.after(max.m.mdate)) {
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
        Chat[] chats = getChats();
        String uname = getUname(uid);
        for (Chat c : chats)
            if (c.cname.equals(uname))
                return c;
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
        Cursor cursor = query(DBHelper.TABLE_CHATS, new String[]{DBHelper.CHAT_ID}, DBHelper.CHAT_ID + " = " + c.cid, null, null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    void deleteChat(int cid) {
        database.execSQL("UPDATE " + DBHelper.TABLE_CHATS + " SET " + DBHelper.CHAT_DELETED + " = 1 WHERE " + DBHelper.CHAT_ID + " = " + cid);
    }

    //Assoziation
    public void insertAssoziation(Assoziation a) {
        if (a != null) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.CHAT_ID, a.cid);
            values.put(DBHelper.USER_ID, a.uid);
            insert(DBHelper.TABLE_ASSOZIATION, values);
        }
    }

    boolean userInChat(int uid, int cid) {
        String[] columns = {DBHelper.USER_ID};
        String condition = DBHelper.CHAT_ID + " = " + cid + " AND " + DBHelper.USER_ID + " = " + uid;
        Cursor cursor = query(DBHelper.TABLE_ASSOZIATION, columns, condition, null, null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    void removeUserFormChat(int uid, int cid) {
        database.execSQL("DELETE FROM " + DBHelper.TABLE_ASSOZIATION + " WHERE " + DBHelper.USER_ID + " = " + uid + " AND " + DBHelper.CHAT_ID + " = " + cid);
    }

    User[] getUsersInChat(Chat c, boolean meInclusive) {
        boolean meIs = false;
        User[] users = getUsers();
        List<User> list = new List<>();
        String[] columns = {DBHelper.TABLE_ASSOZIATION + "." + DBHelper.USER_ID};
        String condition = DBHelper.TABLE_ASSOZIATION + "." + DBHelper.CHAT_ID + " = " + c.cid + " AND " + DBHelper.TABLE_USERS + "." + DBHelper.USER_ID + " = " + DBHelper.TABLE_ASSOZIATION + "." + DBHelper.USER_ID;
        Cursor cursor = query(DBHelper.TABLE_ASSOZIATION + ", " + DBHelper.TABLE_USERS, columns, condition, null, DBHelper.TABLE_USERS + "." + DBHelper.USER_NAME);
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


    private Cursor query(String table, String[] columns, String selection, String groupBy, String orderBy) {
        return database.query(table, columns, selection, null, groupBy, null, orderBy);
    }

    private void insert(String table, ContentValues values) {
        database.insert(table, null, values);
    }

    public void clearTable(String table) {
        database.execSQL("DELETE FROM " + table);
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

        public static final String TABLE_ASSOZIATION = "assoziation";

        static final String TABLE_USERS = "users";
        static final String USER_ID = "uid";
        static final String USER_NAME = "uname";
        static final String USER_KLASSE = "uklasse";
        static final String USER_PERMISSION = "upermission";
        static final String TABLE_MESSAGES_UNSEND = "messages_unsend";

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("DBHelper", "Datenbank wird erstellt");
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
                        CHAT_DELETED + " INTEGER NOT NULL)");
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

        }
    }
}