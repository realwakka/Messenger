package com.realwakka.messenger.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.realwakka.messenger.data.Chat;
import com.realwakka.messenger.data.Friend;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by realwakka on 12/19/14.
 */
public class ChatsDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.CHATS_COLUMN_ID,
            MySQLiteHelper.CHATS_COLUMN_SENDER,MySQLiteHelper.CHATS_COLUMN_RECEIVER,MySQLiteHelper.CHATS_COLUMN_TEXT };

    public ChatsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List<Chat> getAllChatsByRegid(String regid){
        List<Chat> list = new ArrayList<Chat>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CHATS,
                allColumns, MySQLiteHelper.CHATS_COLUMN_RECEIVER+"=? OR "+MySQLiteHelper.CHATS_COLUMN_SENDER+"=?", new String[]{regid,regid}, null, null, null);


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Chat chat = cursorToChat(cursor);
            list.add(chat);
            cursor.moveToNext();
        }

        cursor.close();
        return list;

    }

    public long addChat(Chat chat){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.CHATS_COLUMN_RECEIVER, chat.getTo_reg());
        values.put(MySQLiteHelper.CHATS_COLUMN_SENDER, chat.getFrom_reg());
        values.put(MySQLiteHelper.CHATS_COLUMN_TEXT, chat.getText());

        long insertId = database.insert(MySQLiteHelper.TABLE_FRIENDS, null,values);

        return insertId;
    }

    private Chat cursorToChat(Cursor cursor){
        return new Chat(0,cursor.getString(0),cursor.getString(1),cursor.getString(2),new Date());
    }

}
