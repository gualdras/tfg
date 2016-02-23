package com.university.gualdras.tfgapp.persistence;

import android.content.ContentValues;

import com.university.gualdras.tfgapp.domain.MessageItem;

/**
 * Created by gualdras on 22/02/16.
 */
public class MessageDAO {

    public void createMessage(MessageItem msgItem){
        ContentValues values = new ContentValues(2);
        values.put(DataProvider.COL_MSG, msgItem.getMsg());
        values.put(DataProvider.COL_FROM, msgItem.getFrom());
        //getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);
    }
}
