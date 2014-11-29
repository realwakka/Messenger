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
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_FRIEND };

    public FriendsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Friend createComment(String comment) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_FRIEND, comment);
        long insertId = database.insert(MySQLiteHelper.TABLE_FRIENDS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIENDS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Friend newComment = cursorToFriend(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteComment(Friend comment) {
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_FRIENDS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Friend> getAllComments() {
        List<Friend> comments = new ArrayList<Friend>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FRIENDS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Friend comment = cursorToFriend(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private Friend cursorToFriend(Cursor cursor) {
        Friend comment = new Friend();
        comment.setId(cursor.getInt(0));
        comment.setName(cursor.getString(1));
        return comment;
    }
}
