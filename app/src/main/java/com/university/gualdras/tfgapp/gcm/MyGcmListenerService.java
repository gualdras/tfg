package com.university.gualdras.tfgapp.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.university.gualdras.tfgapp.ServerSharedConstants;
import com.university.gualdras.tfgapp.domain.ImageDownload;
import com.university.gualdras.tfgapp.domain.MessageItem;
import com.university.gualdras.tfgapp.persistence.DataProvider;
import com.university.gualdras.tfgapp.presentation.MainActivity;

/**
 * Created by gualdras on 5/10/15.
 */


public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private static final int MY_NOTIFICATION_ID = 1;
    /**
     * Called when message is received.
     *
     * @param senderID SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String senderID, Bundle data) {
        //TODO wakelock
        String from = data.getString(ServerSharedConstants.FROM);
        String type = data.getString(ServerSharedConstants.TYPE);
        String msg = data.getString(ServerSharedConstants.MSG);
        Log.d(TAG, "SenderID: " + senderID);
        Log.d(TAG, "Message: " + msg);

        /*if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }*/

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: -   with server.
         *     - Store message in local database.
         *     - Update UI.
         */
        MessageItem messageItem = new MessageItem(from, type, msg);

        if(type.equals(MessageItem.IMG_TYPE)){
            new ImageDownload(this, messageItem).execute();
        }
        else {
            messageItem.saveMessageReceived(this);

            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */
            sendNotification(msg);
            // [END_EXCLUDE]
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param msg GCM message received.
     */
    private void sendNotification(String msg) {
        //TODO
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle("Message")
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build());
    }
}

