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

public class DBConnection {

    private SQLiteDatabase database;
    private DBHelper helper;
    private User currentUser;
    private OverviewWrapper wrapper;

    public DBConnection(Context context, User currentUser) {
        helper = new DBHelper(context);
        database = helper.getWritableDatabase();
        this.currentUser = currentUser;
    }

    public void close() {
        helper.close();
    }

    public void insertMessage(Message m) {
        if (m != null && m.allAttributesSet()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.MESSAGES_ID, m.messageId);
            values.put(DBHelper.MESSAGE_TEXT, m.messageText);
            values.put(DBHelper.MESSAGE_DATE, m.sendDate.getTime());
            values.put(DBHelper.CHAT_ID, m.chatId);
            values.put(DBHelper.USER_ID, m.senderId);
            values.put(DBHelper.MESSAGE_READ, m.senderId != currentUser.userId ? 0 : 1);
            insert(DBHelper.TABLE_MESSAGES, null, values);
            Log.i("DBConnection", "inserted: " + m.toString());
        }
    }

    public void insertUser(User u) {
        if (u != null && u.allAttributesSet()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.USER_ID, u.userId);
            values.put(DBHelper.USER_NAME, u.userName);
            values.put(DBHelper.USER_KLASSE, u.klasse);
            values.put(DBHelper.USER_PERMISSION, u.permission);
            insert(DBHelper.TABLE_USERS, null, values);
            Log.i("DBConnection", "inserted: " + u.toString());
        }
    }

    public void insertAssoziation(Assoziation a) {
        if (a != null && a.allAttributesSet()) {
            database.execSQL("DELETE FROM " + DBHelper.TABLE_ASSOZIATION + " WHERE " + DBHelper.CHAT_ID + " = " + a.chatID + " AND " + DBHelper.USER_ID + " = " + a.userID);
            ContentValues values = new ContentValues();
            values.put(DBHelper.CHAT_ID, a.chatID);
            values.put(DBHelper.USER_ID, a.userID);
            values.put(DBHelper.ASSOZIATION_REMOVED, a.removed ? 1 : 0);
            insert(DBHelper.TABLE_ASSOZIATION, null, values);
            Log.i("DBConnection", "inserted: " + a.toString());
        }
    }

    public void insertChat(Chat c) {
        if (c != null && c.allAttributesSet()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.CHAT_ID, c.chatId);
            values.put(DBHelper.CHAT_NAME, c.chatName);
            values.put(DBHelper.CHAT_TYPE, c.chatTyp.toString());
            insert(DBHelper.TABLE_CHATS, null, values);
            Log.i("DBConnection", "inserted: " + c.toString());
        }
    }

    public void setMessagesRead(Chat c) {
        if (c != null) {
            database.execSQL("UPDATE " + DBHelper.TABLE_MESSAGES + " SET " + DBHelper.MESSAGE_READ + " = 1 WHERE " + DBHelper.TABLE_MESSAGES + "." + DBHelper.CHAT_ID + " = " + c.chatId);
        }
    }

    public void removeUserFromChat(User u, Chat c) {
        database.execSQL("UPDATE " + DBHelper.TABLE_ASSOZIATION + " SET " + DBHelper.ASSOZIATION_REMOVED + " = 1 WHERE " + DBHelper.CHAT_ID + " = " + c.chatId + " AND " + DBHelper.USER_ID + " = " + u.userId);
    }

    public Message[] getMessages() {
        User[] users = getUsers();
        String[] columns = {DBHelper.MESSAGES_ID, DBHelper.MESSAGE_TEXT, DBHelper.MESSAGE_DATE, DBHelper.CHAT_ID, DBHelper.USER_ID, DBHelper.MESSAGE_READ};
        Cursor cursor = query(DBHelper.TABLE_MESSAGES, columns, null, null, null, null, DBHelper.CHAT_ID + ", " + DBHelper.MESSAGE_DATE);
        Message[] array = new Message[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext()) {
            array[i] = new Message(cursor.getInt(0), cursor.getString(1), cursor.getLong(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5) == 0 ? false : true);
            int current = array[i].senderId;
            if (current != currentUser.userId) {
                String username = null;
                for (int i1 = 0; i1 < users.length; i1++) {
                    if (users[i1].userId == current) {
                        username = users[i1].userName;
                        break;
                    }
                }
                array[i].setSenderName(username);
            } else {
                array[i].setSenderName(currentUser.userName);
            }
        }
        cursor.close();
        return array;
    }

    public User[] getUsers() {
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

    public Chat[] getChats() {
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
                if (list.getContent().letzeNachricht != null) {
                    max = list.getContent();
                    iMax = i;
                    break;
                }
            }
            for (int i = iMax; max != null && i < limit; list.next(), i++) {
                if (list.getContent().letzeNachricht != null && max != null && max.letzeNachricht != null && list.getContent().letzeNachricht.sendDate.after(max.letzeNachricht.sendDate)) {
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

    public User[] getUsersInChat(Chat c, boolean meInclusive) {
        boolean meIs = false;
        User[] users = getUsers();
        List<User> list = new List<>();
        String[] columns = {DBHelper.TABLE_ASSOZIATION + "." + DBHelper.USER_ID, DBHelper.TABLE_ASSOZIATION + "." + DBHelper.ASSOZIATION_REMOVED};
        String condition = DBHelper.TABLE_ASSOZIATION + "." + DBHelper.CHAT_ID + " = " + c.chatId + " AND " + DBHelper.TABLE_ASSOZIATION + "." + DBHelper.ASSOZIATION_REMOVED + " = 0 AND " + DBHelper.TABLE_USERS + "." + DBHelper.USER_ID + " = " + DBHelper.TABLE_ASSOZIATION + "." + DBHelper.USER_ID;
        Cursor cursor = query(DBHelper.TABLE_ASSOZIATION + ", " + DBHelper.TABLE_USERS, columns, condition, null, null, null, DBHelper.TABLE_USERS + "." + DBHelper.USER_NAME);
        cursor.moveToFirst();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int current = cursor.getInt(0);
            if (current != currentUser.userId) {
                for (int j = 0; j < users.length; j++)
                    if (users[j].userId == current) {
                        list.append(users[j]);
                        break;
                    }
            } else
                meIs = true;
        }
        if (meInclusive && meIs) {
            list.append(currentUser);
        }
        User[] array = new User[list.length()];
        list.fill(array);
        cursor.close();
        return array;
    }

    public User[] getUsersNotInChat(Chat c) {
        User[] u = getUsers();
        List<User> list = new List<>();
        list.adapt(u);
        User[] uoc = getUsersInChat(c, false);
        for (int i = 0; i < uoc.length; i++) {
            int current = uoc[i].userId;
            for (list.toFirst(); list.hasAccess(); list.next())
                if (list.getContent().userId == current) {
                    list.remove();
                    break;
                }
        }
        User[] array = new User[list.length()];
        list.fill(array);
        return array;
    }

    public Message[] getMessagesFromChat(Chat c) {
        if (c == null)
            return new Message[0];
        List<Message> messages = new List<>();
        messages.adapt(getMessages());
        for (messages.toFirst(); messages.hasAccess(); )
            if (messages.getContent().chatId != c.chatId)
                messages.remove();
            else
                messages.next();
        Message[] array = new Message[messages.length()];
        messages.fill(array);
        return array;
    }

    public Message[] getUnreadMessages() {
        String[] columns = {DBHelper.MESSAGES_ID, DBHelper.MESSAGE_TEXT, DBHelper.MESSAGE_DATE, DBHelper.CHAT_ID, DBHelper.USER_ID};
        String condiotion = DBHelper.MESSAGE_READ + " = 0";
        Cursor cursor = query(DBHelper.TABLE_MESSAGES, columns, condiotion, null, null, null, DBHelper.CHAT_ID + ", " + DBHelper.MESSAGE_DATE);
        Message[] array = new Message[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext())
            array[i] = new Message(cursor.getInt(0), cursor.getString(1), cursor.getLong(2), cursor.getInt(3), cursor.getInt(4), false);
        cursor.close();
        return array;
    }

    public Message[] getUnreadMessages(Chat c) {
        String[] columns = {DBHelper.MESSAGES_ID, DBHelper.MESSAGE_TEXT, DBHelper.MESSAGE_DATE, DBHelper.CHAT_ID, DBHelper.USER_ID};
        String condiotion = DBHelper.MESSAGE_READ + " = 0 AND " + DBHelper.CHAT_ID + " = " + c.chatId;
        Cursor cursor = query(DBHelper.TABLE_MESSAGES, columns, condiotion, null, null, null, DBHelper.CHAT_ID + ", " + DBHelper.MESSAGE_DATE);
        Message[] array = new Message[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < array.length; i++, cursor.moveToNext())
            array[i] = new Message(cursor.getInt(0), cursor.getString(1), cursor.getLong(2), cursor.getInt(3), cursor.getInt(4), false);
        cursor.close();
        return array;
    }

    public boolean isUserInChat(User u, Chat c) {
        String[] columns = {DBHelper.USER_ID};
        String condition = DBHelper.CHAT_ID + " = " + c.chatId + " AND " + DBHelper.ASSOZIATION_REMOVED + " = 0 AND " + DBHelper.USER_ID + " = " + u.userId;
        Cursor cursor = query(DBHelper.TABLE_ASSOZIATION, columns, condition, null, null, null, null);
        boolean b = cursor.getCount() > 0;
        cursor.close();
        return b;
    }

    public void clearTable(String table) {
        database.execSQL("DELETE FROM " + table);
    }

    private synchronized Cursor query(String arg1, String[] arg2, String arg3, String[] arg4, String arg5, String arg6, String arg7) {
        return database.query(arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    private long insert(String table, String nullColumnHack, ContentValues values) {
        long l = database.insert(table, nullColumnHack, values);
        if (wrapper != null)
            wrapper.notifyUpdate();
        return l;
    }

    public void setOverviewWrapper(OverviewWrapper wrapper) {
        this.wrapper = wrapper;
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

        public DBHelper(Context context) {
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
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}