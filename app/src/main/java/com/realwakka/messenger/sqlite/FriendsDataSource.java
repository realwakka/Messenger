package com.realwakka.messenger.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.realwakka.messenger.data.Friend;

import java.lang.reflect.Array;
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
            MySQLiteHelper.FRIENDS_COLUMN_FRIEND,MySQLiteHelper.FRIENDS_COLUMN_REGID,MySQLiteHelper.FRIENDS_COLUMN_PUBKEY };

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
        values.put(MySQLiteHelper.FRIENDS_COLUMN_PUBKEY,friend.getPubicKey());

        long insertId = database.insert(MySQLiteHelper.TABLE_FRIENDS, null,values);
        friend.setId(insertId);
    }
    public Friend getFriendByRegid(String regid){
        Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIENDS,
                allColumns, MySQLiteHelper.FRIENDS_COLUMN_REGID+"=?",new String[]{regid}, null, null, null);
        cursor.moveToFirst();
        return cursorToFriend(cursor);
    }

    public ArrayList<Friend> getFriendsList(){
        ArrayList<Friend> list = new ArrayList<Friend>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIENDS,
                allColumns, null,null, null, null, null);


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Friend comment = cursorToFriend(cursor);
            list.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        return list;
    }
    private Friend cursorToFriend(Cursor cursor) {
        return new Friend(cursor.getLong(0),cursor.getString(1),cursor.getBlob(3),cursor.getString(2));
    }

}
