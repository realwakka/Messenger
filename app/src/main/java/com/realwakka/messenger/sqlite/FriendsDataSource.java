package com.realwakka.messenger.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.realwakka.messenger.data.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by realwakka on 14. 11. 28.
 */
public class FriendsDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.FRIENDS_COLUMN_ID,
            MySQLiteHelper.FRIENDS_COLUMN_FRIEND,MySQLiteHelper.FRIENDS_COLUMN_REGID };

    public FriendsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addFriend(Friend friend) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.FRIENDS_COLUMN_FRIEND, friend.getName());
        values.put(MySQLiteHelper.FRIENDS_COLUMN_REGID, friend.getRegid());

        long insertId = database.insert(MySQLiteHelper.TABLE_FRIENDS, null,values);

        friend.setId(insertId);

    }


}
