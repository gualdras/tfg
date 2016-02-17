package com.university.gualdras.tfgapp.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.university.gualdras.tfgapp.domain.FriendProfile;
import com.university.gualdras.tfgapp.domain.Message;

/**
 * Created by gualdras on 11/10/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "conversation";

    // Table Names
    private static final String TABLE_FRIEND_PROFILE = "friendProfile";
    private static final String TABLE_MESSAGE = "message";

    // Column names
    private static final String FRIEND_NUMBER_ID = "friendNumber";
    private static final String FRIEND_NAME = "friendName";
    private static final String FRIEND_PHOTO = "friendPhoto";

    private static final String MESSAGE_ID = "messageID";
    private static final String MESSAGE_CONTENT = "messageContent";
    private static final String MESSAGE_DATETIME = "messageDateTime";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_FRIEND_PROFILE + "(" + FRIEND_NUMBER_ID + "INTEGER PRIMARY KEY, " + FRIEND_NAME + "TEXT," + FRIEND_PHOTO + "TEXT" + ")");
        db.execSQL("CREATE TABLE " + TABLE_MESSAGE + "(" + MESSAGE_ID + "INTEGER PRIMARY KEY, " + FRIEND_NUMBER_ID + "INTEGER," + MESSAGE_CONTENT + "TEXT" + MESSAGE_DATETIME + "TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createFriendProfile(FriendProfile friendProfile){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FRIEND_NUMBER_ID, friendProfile.getMobileNumber());
        values.put(FRIEND_NAME, friendProfile.getFriendName());
        values.put(FRIEND_PHOTO, friendProfile.getPhoto());

        db.insert(TABLE_FRIEND_PROFILE, null, values);
    }

    public void createMessage(Message msg){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MESSAGE_ID, (Integer) null);
        values.put(FRIEND_NUMBER_ID, msg.getMobileNumber());
        values.put(MESSAGE_CONTENT, msg.getMsgContent());
        values.put(MESSAGE_DATETIME, msg.getDataTime());

        db.insert(TABLE_MESSAGE, null, values);
    }

    public void updateFriendProfile(FriendProfile friendProfile){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FRIEND_NAME, friendProfile.getFriendName());
        values.put(FRIEND_PHOTO, friendProfile.getPhoto());

        // updating row
        db.update(TABLE_FRIEND_PROFILE, values, FRIEND_NUMBER_ID + " = ?",
                new String[] { String.valueOf(friendProfile.getMobileNumber()) });
    }
}