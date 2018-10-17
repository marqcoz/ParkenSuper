package com.parken.parkensuper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        //Log.d(TAG, "Notification body: " + remoteMessage.getNotification().getBody());
        //Log.d(TAG, "Notification data: " + remoteMessage.getNotification());
        Log.d(TAG, "Notification data: " + remoteMessage.getData());


        try {

            JSONObject data = new JSONObject(remoteMessage.getData());


            if(data.getString("datos").equals("OK")){

                int idNoti = Integer.valueOf(data.getString("idNotification"));
                switch (idNoti){

                    case ParkenActivity.NOTIFICATION_NEW_REPORT:

                        Notificacion.lanzar(getApplicationContext(), idNoti, "MAX", data.toString());
                        startActivity(new Intent(getApplicationContext(), ReporteActivity.class));

                        break;

                    default:
                        break;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
