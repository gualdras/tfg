package com.university.gualdras.tfgapp.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.university.gualdras.tfgapp.domain.FriendProfile;
import com.university.gualdras.tfgapp.domain.MessageItem;

/**
 * Created by gualdras on 11/10/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "chat.db";

    // Table Names
    private static final String TABLE_FRIEND_PROFILE = "friendProfile";
    private static final String TABLE_MESSAGE = "message";

    // Column names
    private static final String FRIEND_PHONE_NUMBER = "friendNumber";
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
        db.execSQL("CREATE TABLE " + TABLE_FRIEND_PROFILE + "(" + FRIEND_PHONE_NUMBER + "INTEGER PRIMARY KEY, " + FRIEND_NAME + "TEXT," + FRIEND_PHOTO + "TEXT" + ")");
        db.execSQL("CREATE TABLE " + TABLE_MESSAGE + "(" + MESSAGE_ID + "INTEGER PRIMARY KEY, " + FRIEND_PHONE_NUMBER + "INTEGER," + MESSAGE_CONTENT + "TEXT" + MESSAGE_DATETIME + "TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void createFriendProfile(FriendProfile friendProfile){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FRIEND_PHONE_NUMBER, friendProfile.getMobileNumber());
        values.put(FRIEND_NAME, friendProfile.getFriendName());
        values.put(FRIEND_PHOTO, friendProfile.getPhoto());

        db.insert(TABLE_FRIEND_PROFILE, null, values);
    }

    public void updateFriendProfile(FriendProfile friendProfile){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FRIEND_NAME, friendProfile.getFriendName());
        values.put(FRIEND_PHOTO, friendProfile.getPhoto());

        // updating row
        db.update(TABLE_FRIEND_PROFILE, values, FRIEND_PHONE_NUMBER + " = ?",
                new String[]{String.valueOf(friendProfile.getMobileNumber())});
    }

    public Cursor getFriendProfile(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;

        return c;
    }

    public void createMessage(MessageItem msg){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MESSAGE_ID, (Integer) null);
        values.put(FRIEND_PHONE_NUMBER, msg.getFrom());
        values.put(MESSAGE_CONTENT, msg.getMsg());
        values.put(MESSAGE_DATETIME, msg.getDateTime());

        db.insert(TABLE_MESSAGE, null, values);
    }




}