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

public class DBConnection {
    private SQLiteDatabase database;
    private DBHelper helper;

    public DBConnection(Context context) {
        helper = new DBHelper(context);
        database = helper.getWritableDatabase();
    }

    private Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    private long insert(String table, String nullColumnHack, ContentValues values) {
        return database.insert(table, nullColumnHack, values);
    }

    public void insertMessage(Message m) {
        if (m != null) {
            database.execSQL("DELETE FROM " + DBHelper.TABLE_MESSAGES + " WHERE " + DBHelper.MESSAGES_ID + " = " + m.mid);
            ContentValues values = new ContentValues();
            values.put(DBHelper.MESSAGES_ID, m.mid);
            values.put(DBHelper.MESSAGE_TEXT, m.mtext);
            values.put(DBHelper.MESSAGE_DATE, m.mdate.getTime());
            values.put(DBHelper.CHAT_ID, m.cid);
            values.put(DBHelper.USER_ID, m.uid);
            values.put(DBHelper.MESSAGE_READ, m.uid != Utils.getUserID() ? 0 : 1);
            insert(DBHelper.TABLE_MESSAGES, null, values);
        }
    }

    public void insertUser(User u) {
        if (u != null) {
            database.execSQL("DELETE FROM " + DBHelper.TABLE_USERS + " WHERE " + DBHelper.USER_ID + " = " + u.uid);
            ContentValues values = new ContentValues();
            values.put(DBHelper.USER_ID, u.uid);
            values.put(DBHelper.USER_NAME, u.uname);
            values.put(DBHelper.USER_KLASSE, u.ustufe);
            values.put(DBHelper.USER_PERMISSION, u.upermission);
            insert(DBHelper.TABLE_USERS, null, values);
        }
    }

    public void insertAssoziation(Assoziation a) {
        if (a != null) {
            database.execSQL("DELETE FROM " + DBHelper.TABLE_ASSOZIATION + " WHERE " + DBHelper.CHAT_ID + " = " + a.cid + " AND " + DBHelper.USER_ID + " = " + a.uid);
            ContentValues values = new ContentValues();
            values.put(DBHelper.CHAT_ID, a.cid);
            values.put(DBHelper.USER_ID, a.uid);
            values.put(DBHelper.ASSOZIATION_REMOVED, a.aremoved ? 1 : 0);
            insert(DBHelper.TABLE_ASSOZIATION, null, values);
        }
    }

    public void insertChat(Chat c) {
        if (c != null) {
            database.execSQL("DELETE FROM " + DBHelper.TABLE_CHATS + " WHERE " + DBHelper.CHAT_ID + " = " + c.cid);
            ContentValues values = new ContentValues();
            values.put(DBHelper.CHAT_ID, c.cid);
            values.put(DBHelper.CHAT_NAME, c.cname);
            values.put(DBHelper.CHAT_TYPE, c.ctype.toString());
            insert(DBHelper.TABLE_CHATS, null, values);
        }
    }

    void setMessagesRead(Chat c) {
        if (c != null) {
            database.execSQL("UPDATE " + DBHelper.TABLE_MESSAGES + " SET " + DBHelper.MESSAGE_READ + " = 1 WHERE " + DBHelper.TABLE_MESSAGES + "." + DBHelper.CHAT_ID + " = " + c.cid);
        }
    }

    void removeUserFromChat(User u, Chat c) {
        database.execSQL("UPDATE " + DBHelper.TABLE_ASSOZIATION + " SET " + DBHelper.ASSOZIATION_REMOVED + " = 1 WHERE " + DBHelper.CHAT_ID + " = " + c.cid + " AND " + DBHelper.USER_ID + " = " + u.uid);
    }

    private Message[] getMessages() {
        User[] users = getUsers();
        String[] columns = {DBHelper.MESSAGES_ID, DBHelper.MESSAGE_TEXT, DBHelper.MESSAGE_DATE, DBHelper.CHAT_ID, DBHelper.USER_ID, DBHelper.MESSAGE_READ};
        Cursor cursor = query(DBHelper.TABLE_MESSAGES, columns, null, null, null, null, DBHelper.CHAT_ID + ", " + DBHelper.MESSAGE_DATE);
        Message[] array = new Message[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext()) {
            array[i] = new Message(cursor.getInt(0), cursor.getString(1), cursor.getLong(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5) != 0);
            if (array[i].uid != Utils.getUserID()) {
                for (User user : users) {
                    if (user.uid == array[i].uid) {
                        array[i].setUname(user.uname);
                        break;
                    }
                }
            } else {
                array[i].setUname(Utils.getUserName());
            }
        }
        cursor.close();
        return array;
    }

    User[] getUsers() {
        String[] columns = {DBHelper.USER_ID, DBHelper.USER_NAME, DBHelper.USER_KLASSE, DBHelper.USER_PERMISSION};
        Cursor cursor = query(DBHelper.TABLE_USERS, columns, null, null, null, null, DBHelper.USER_ID);
        User[] array = new User[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext()) {
            array[i] = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3));
        }
        cursor.close();
        return array;
    }

    Chat[] getChats() {
        String[] columns = {DBHelper.CHAT_ID, DBHelper.CHAT_NAME, DBHelper.CHAT_TYPE};
        Cursor cursor = query(DBHelper.TABLE_CHATS, columns, null, null, null, null, DBHelper.CHAT_ID);
        Chat[] array = new Chat[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++) {
            array[i] = new Chat(cursor.getInt(0), cursor.getString(1), Chat.Chattype.valueOf(cursor.getString(2).toUpperCase()));
            Message[] mArray = getMessagesFromChat(array[i]);
            if (mArray.length != 0)
                array[i].setLetzteNachricht(mArray[mArray.length - 1]);
            cursor.moveToNext();
        }
        List<Chat> list = new List<>(array);
        for (int limit = list.length(); limit > 0; limit--) {
            list.toFirst();
            Chat max = null;
            int iMax = 0;
            for (int i = 0; i < limit; i++, list.next()) {
                if (list.getContent().m != null) {
                    max = list.getContent();
                    iMax = i;
                    break;
                }
            }
            for (int i = iMax; max != null && i < limit; list.next(), i++) {
                if (list.getContent().m != null && max.m != null && list.getContent().m.mdate.after(max.m.mdate)) {
                    max = list.getContent();
                    iMax = i;
                }
            }
            if (max == null) {
                list.toFirst();
                max = list.getContent();
            }
            list.toFirst();
            list.toIndex(iMax);
            list.remove();
            list.append(max);
        }
        cursor.close();
        return list.fill(array);
    }

    User[] getUsersInChat(Chat c, boolean meInclusive) {
        boolean meIs = false;
        User[] users = getUsers();
        List<User> list = new List<>();
        String[] columns = {DBHelper.TABLE_ASSOZIATION + "." + DBHelper.USER_ID, DBHelper.TABLE_ASSOZIATION + "." + DBHelper.ASSOZIATION_REMOVED};
        String condition = DBHelper.TABLE_ASSOZIATION + "." + DBHelper.CHAT_ID + " = " + c.cid + " AND " + DBHelper.TABLE_ASSOZIATION + "." + DBHelper.ASSOZIATION_REMOVED + " = 0 AND " + DBHelper.TABLE_USERS + "." + DBHelper.USER_ID + " = " + DBHelper.TABLE_ASSOZIATION + "." + DBHelper.USER_ID;
        Cursor cursor = query(DBHelper.TABLE_ASSOZIATION + ", " + DBHelper.TABLE_USERS, columns, condition, null, null, null, DBHelper.TABLE_USERS + "." + DBHelper.USER_NAME);
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

    String getUname(int uid) {
        Cursor cursor = query(DBHelper.TABLE_USERS, new String[]{DBHelper.USER_NAME}, DBHelper.USER_ID + " = " + uid, null, null, null, null);
        String erg = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            erg = cursor.getString(0);
        }
        cursor.close();
        return erg;
    }

    Message[] getMessagesFromChat(Chat c) {
        if (c == null)
            return new Message[0];
        List<Message> messages = new List<>();
        messages.adapt(getMessages());
        for (messages.toFirst(); messages.hasAccess(); )
            if (messages.getContent().cid != c.cid)
                messages.remove();
            else
                messages.next();
        Message[] array = new Message[messages.length()];
        messages.fill(array);
        return array;
    }

    public Message[] getUnreadMessages() {
        String[] columns = {DBHelper.MESSAGES_ID, DBHelper.MESSAGE_TEXT, DBHelper.MESSAGE_DATE, DBHelper.CHAT_ID, DBHelper.USER_ID};
        String condiotion = DBHelper.MESSAGE_DATE + " > " + Utils.getLastMessengerNotification().getTime();
        Cursor cursor = query(DBHelper.TABLE_MESSAGES, columns, condiotion, null, null, null, DBHelper.CHAT_ID + ", " + DBHelper.MESSAGE_DATE);
        Message[] array = new Message[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext())
            array[i] = new Message(cursor.getInt(0), cursor.getString(1), cursor.getLong(2), cursor.getInt(3), cursor.getInt(4), false);
        cursor.close();
        return array;
    }

    boolean isUserInChat(User u, Chat c) {
        String[] columns = {DBHelper.USER_ID};
        String condition = DBHelper.CHAT_ID + " = " + c.cid + " AND " + DBHelper.ASSOZIATION_REMOVED + " = 0 AND " + DBHelper.USER_ID + " = " + u.uid;
        Cursor cursor = query(DBHelper.TABLE_ASSOZIATION, columns, condition, null, null, null, null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    public boolean hasUnreadMessages() {
        Cursor cursor = query(DBHelper.TABLE_MESSAGES, new String[]{DBHelper.MESSAGES_ID}, DBHelper.MESSAGE_DATE + " > " + Utils.getLastMessengerNotification().getTime(), null, null, null, null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    void insertUnsendMessage(String mtext, int cid) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.MESSAGES_ID, "null");
        values.put(DBHelper.MESSAGE_TEXT, mtext);
        values.put(DBHelper.CHAT_ID, cid);
        insert(DBHelper.TABLE_MESSAGES_UNSEND, null, values);
    }

    public Message[] getUnsendMessages() {
        Cursor cursor = query(DBHelper.TABLE_MESSAGES_UNSEND, new String[]{DBHelper.MESSAGE_TEXT, DBHelper.CHAT_ID}, null, null, null, null, null);
        Message[] array = new Message[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext()) {
            array[i] = new Message(-1, cursor.getString(0), 0, cursor.getInt(1), 0, false);
        }
        return array;
    }

    public void close() {
        helper.close();
    }

    private class DBHelper extends SQLiteOpenHelper {
        static final String DATABASE_NAME = "messenger";
        static final String TABLE_MESSAGES = "messages";
        static final String MESSAGES_ID = "mid";
        static final String MESSAGE_TEXT = "mtext";
        static final String MESSAGE_DATE = "mdate";
        static final String MESSAGE_READ = "mgelesen";
        static final String TABLE_CHATS = "chats";
        static final String CHAT_ID = "cid";
        static final String CHAT_NAME = "cname";
        static final String CHAT_TYPE = "ctype";
        static final String TABLE_ASSOZIATION = "assoziation";
        static final String ASSOZIATION_REMOVED = "aremoved";
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
                db.execSQL("CREATE TABLE " + TABLE_MESSAGES + " (" +
                        MESSAGES_ID + " INTEGER PRIMARY KEY, " +
                        MESSAGE_TEXT + " TEXT NOT NULL, " +
                        MESSAGE_DATE + " TEXT NOT NULL, " +
                        CHAT_ID + " INTEGER NOT NULL, " +
                        USER_ID + " INTEGER NOT NULL, " +
                        MESSAGE_READ + " INTEGER NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("CREATE TABLE " + TABLE_CHATS + " (" +
                        CHAT_ID + " INTEGER PRIMARY KEY, " +
                        CHAT_NAME + " TEXT NOT NULL, " +
                        CHAT_TYPE + " TEXT NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("CREATE TABLE " + TABLE_ASSOZIATION + " (" +
                        CHAT_ID + " INTEGER NOT NULL, " +
                        USER_ID + " INTEGER NOT NULL, " +
                        ASSOZIATION_REMOVED + " INTEGER NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                        USER_ID + " INTEGER PRIMARY KEY, " +
                        USER_NAME + " TEXT NOT NULL, " +
                        USER_KLASSE + " TEXT, " +
                        USER_PERMISSION + " INTEGER NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("CREATE TABLE " + TABLE_MESSAGES_UNSEND + " (" +
                        MESSAGES_ID + " INTEGER PRIMARY KEY, " +
                        MESSAGE_TEXT + " TEXT NOT NULL, " +
                        CHAT_ID + " TEXT)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}