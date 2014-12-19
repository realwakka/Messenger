package com.realwakka.messenger.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by realwakka on 14. 11. 28.
 */
public class MySQLiteHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "messenger";


    public static final String TABLE_FRIENDS = "friends";
    public static final String FRIENDS_COLUMN_ID = "_id";
    public static final String FRIENDS_COLUMN_FRIEND = "friend";
    public static final String FRIENDS_COLUMN_REGID = "regid";

    private static final String FRIENDS_TABLE_CREATE="create table "+TABLE_FRIENDS+" ( "+FRIENDS_COLUMN_ID+" integer primary key autoincrement, " + FRIENDS_COLUMN_FRIEND
            + " varchar(16)," + FRIENDS_COLUMN_REGID + " integer not null);";

    public static final String TABLE_CHATS = "chats";
    public static final String CHATS_COLUMN_ID = "_id";
    public static final String CHATS_COLUMN_SENDER = "sender";
    public static final String CHATS_COLUMN_RECEIVER = "receiver";
    public static final String CHATS_COLUMN_TEXT="text";
    public static final String CHATS_COLUMN_DATE="date";

    private static final String CHATS_TABLE_CREATE="create table "+TABLE_CHATS+" ( "+CHATS_COLUMN_ID+" integer primary key autoincrement, " + CHATS_COLUMN_SENDER
            + " integer not null," + CHATS_COLUMN_RECEIVER + " integer not null,"+ CHATS_COLUMN_TEXT + " text,"+CHATS_COLUMN_DATE+" varchar(20));";

    private static final int DATABASE_VERSION = 2;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FRIENDS_TABLE_CREATE);
        db.execSQL(CHATS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+FRIENDS_TABLE_CREATE);
        db.execSQL("DROP TABLE IF EXISTS "+CHATS_TABLE_CREATE);

        onCreate(db);
    }
}
