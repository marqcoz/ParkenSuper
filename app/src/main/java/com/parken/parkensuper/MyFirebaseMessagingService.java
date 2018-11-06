package com.parken.parkensuper;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private ShPref session;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        session = new ShPref(getApplicationContext());
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

                    case ParkenActivity.NOTIFICATION_SUPER_DELETED:

                        if (ejecutandoAplicacion(getBaseContext(), "com.parken.parkensuper")) {
                            Log.i("Aplicacion", "La aplicación esta ejecutandose!");
                            Intent intent = new Intent(getApplicationContext(), ParkenActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .putExtra("Activity", ParkenActivity.NOTIFICATIONS)
                                    .putExtra("ActivityStatus", idNoti)
                                    .putExtra("Actions", "")
                                    .putExtra("data", "");
                            startActivity(intent);

                        } else {
                            Log.i("Aplicacion", "La aplicación esta cerrada.");
                            session.setLoggedin(false);
                            session.clearAll();
                        }


                        break;

                    default:
                        break;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void obtenerAplicaciones(Context context){
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = context.getPackageManager();
        while(i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo)(i.next());
            try {
                CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                Log.d("Aplicaciones", c.toString());
                //runningApplications.add(c.toString());
            }catch(Exception e) {
                Log.d("Aplicaciones", e.getMessage());
            }
        }
    }

    private boolean ejecutandoAplicacion(Context context, String packagename) {
        ActivityManager activityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++)
        {
            if(procInfos.get(i).processName.equals(packagename))
            {
                return true; //Esta activa.
            }
        }
        return false; //Esta cerrada.
    }

}
