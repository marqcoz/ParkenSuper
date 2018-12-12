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

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.parken.parkensuper.ParkenActivity.NOTIFICATION_ALMOST_FINISH_PS;
import static com.parken.parkensuper.ParkenActivity.NOTIFICATION_CAR_FREE;
import static com.parken.parkensuper.ParkenActivity.NOTIFICATION_EP_BOOKED;
import static com.parken.parkensuper.ParkenActivity.NOTIFICATION_EP_BOOKED_OUT;
import static com.parken.parkensuper.ParkenActivity.NOTIFICATION_FINISH_PS;
import static com.parken.parkensuper.ParkenActivity.NOTIFICATION_MOVEMENT;
import static com.parken.parkensuper.ParkenActivity.NOTIFICATION_PARKEN_OUT;
import static com.parken.parkensuper.ParkenActivity.NOTIFICATION_PAYING;
import static com.parken.parkensuper.ParkenActivity.NOTIFICATION_PAYING_CANCEL;
import static com.parken.parkensuper.ParkenActivity.NOTIFICATION_PAYING_OUT;
import static com.parken.parkensuper.ParkenActivity.NOTIFICATION_SESSION_PARKEN;
import static com.parken.parkensuper.ParkenActivity.NOTIFICATION__RECEIPT_PAYED;

public class Notificacion extends Notification {
    public Notificacion() {
    }

    public static void lanzar(Context c, int notificationID, String priority, String data){


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        boolean noti = pref.getBoolean("notify_on", true);

        if(noti) {


            NotificationManager mNotifyMgr = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c, priority);
            notificationBuilder

                    //Se agregan las opciones genericas
                    .setSmallIcon(R.drawable.ic_parken_notification)
                    .setColor(Color.BLACK);


            //Obtener las opciones de cada notificación


            mNotifyMgr.notify(notificationID, opcNotificaciones(c, notificationBuilder, notificationID, data).build());
            //return notificationBuilder.build();
        }
    }

    private static NotificationCompat.Builder opcNotificaciones(Context c, NotificationCompat.Builder notificacion, int id, String data){

        String title;
        String msg;
        String[] info;
        String action1;
        String action2;
        String action3;
        Intent intent;
        PendingIntent pendingIntent;
        Intent actionIntent1;
        PendingIntent pendingIntent1;
        Intent actionIntent2;
        PendingIntent pendingIntent2;
        Intent actionIntent3;
        PendingIntent pendingIntent3;

        intent = new Intent(c, ParkenActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("Activity", ParkenActivity.NOTIFICATIONS)
                .putExtra("ActivityStatus", id)
                .putExtra("Actions", 0);
        pendingIntent = PendingIntent.getActivity(c, id+1, intent,PendingIntent.FLAG_ONE_SHOT);

        actionIntent1 = new Intent(c, ParkenActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("Activity", ParkenActivity.NOTIFICATIONS)
                .putExtra("ActivityStatus", id)
                .putExtra("Actions", 1);
        pendingIntent1 = PendingIntent.getActivity(c, id+2, actionIntent1,PendingIntent.FLAG_ONE_SHOT);

        actionIntent2 = new Intent(c, ParkenActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("Activity", ParkenActivity.NOTIFICATIONS)
                .putExtra("ActivityStatus", id)
                .putExtra("Actions", 2);
        pendingIntent2 = PendingIntent.getActivity(c, id+3, actionIntent2,PendingIntent.FLAG_ONE_SHOT);



        switch (id){

            case ParkenActivity.NOTIFICATION_NEW_REPORT:

                /*
                info = data.split("&");
                title = info[0];
                msg = info[1];
                */


                title = "Nuevo reporte";
                msg = "Tienes un reporte nuevo";


                pendingIntent.cancel();


                intent = new Intent(c, ParkenActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("Activity", ParkenActivity.NOTIFICATIONS)
                        .putExtra("ActivityStatus", id)
                        .putExtra("Actions", 1)
                        .putExtra("data", data);
                pendingIntent = PendingIntent.getActivity(c, id+1, intent,PendingIntent.FLAG_UPDATE_CURRENT);


                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        //Al deslizar la notificación que se cancele
                        .setOngoing(false)
                        //Al presionar la notificación que se cancele
                        .setAutoCancel(true);

                break;


            case ParkenActivity.NOTIFICATION_INFO:

                info = data.split("&");
                title = info[0];
                msg = info[1];



                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        //Al deslizar la notificación que se cancele
                        .setOngoing(false)
                        //Al presionar la notificación que se cancele
                        .setAutoCancel(true);

                break;

            case ParkenActivity.NOTIFICATION_CANCEL:

                info = data.split("&");
                title = info[0];
                msg = info[1];


                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        .setOngoing(false)
                        .setAutoCancel(false);


                break;



            case NOTIFICATION_EP_BOOKED_OUT:


                cerrar(c, NOTIFICATION_EP_BOOKED);

                title = "Tiempo excedido";
                msg = "No alcanzaste a llegar al espacio Parken.\nSolicita de nuevo.";


                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        .setOngoing(false)
                        .setAutoCancel(true);
                break;

            case NOTIFICATION_PARKEN_OUT:


                cerrar(c, NOTIFICATION_EP_BOOKED);

                title = "Tiempo excedido";
                msg = "No recibimos tu respuesta a tiempo.\n Desaloja el espacio Parken o serás acreedor a una sanción.";


                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        .setOngoing(false)
                        .setAutoCancel(false);

                break;

            case ParkenActivity.NOTIFICATION_PAYING:

                cerrar(c, NOTIFICATION_EP_BOOKED);

                title = "Pagando...";
                msg = "Tienes 5 minutos para realizar tu pago.";

                intent = new Intent(c, SesionParkenActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("Activity", ParkenActivity.NOTIFICATIONS)
                        .putExtra("ActivityStatus", id)
                        .putExtra("Actions", 0);
                pendingIntent = PendingIntent.getActivity(c, id+1, intent,PendingIntent.FLAG_ONE_SHOT);

                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        .setOngoing(true)
                        .setAutoCancel(false);

                break;

            case NOTIFICATION_PAYING_CANCEL:

                cerrar(c, NOTIFICATION_PAYING);

                title = "Pago cancelado";
                msg = "Desaloja el espacio Parken o serás acreedor a una sanción.";


                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        .setOngoing(false)
                        .setAutoCancel(false);

                break;

            case NOTIFICATION_PAYING_OUT:

                cerrar(c, NOTIFICATION_PAYING);

                title = "Tiempo excedido";
                msg = "No finalizaste tu pago a tiempo.\n Desaloja el espacio Parken o serás acreedor a una sanción.";


                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        .setOngoing(false)
                        .setAutoCancel(false);

                break;

            case NOTIFICATION_SESSION_PARKEN:

                cerrar(c, NOTIFICATION_PAYING);

                info = data.split("&");
                title = "Sesión Parken";
                msg = "Has " + info[0] + " la sesión Parken.\nRecuerda desalojar el espacio a las " + info[1];


                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        .setOngoing(true)
                        .setAutoCancel(true);

                break;

            case ParkenActivity.NOTIFICATION_ALMOST_FINISH_PS:

                cerrar(c, NOTIFICATION_SESSION_PARKEN);

                info = data.split("&");
                title = info[0];
                msg = info[1];
                action1 = "Renovar";
                action2 = "Finalizar";


                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        .addAction(R.drawable.ic_address, action1, pendingIntent1)
                        .addAction(R.drawable.places_ic_clear, action2, pendingIntent2)

                        .setPriority(PRIORITY_MAX)

                        .setOngoing(false)
                        .setAutoCancel(false);


                break;

            case NOTIFICATION_FINISH_PS:

                cerrar(c, NOTIFICATION_ALMOST_FINISH_PS);

                title = "Sesión Parken finalizada";
                msg = "Desaloja tu vehículo o serás acreedor a una sancion.";
                action1 = "Finalizar";


                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        .addAction(R.drawable.ic_address, action1, pendingIntent1)

                        .setPriority(PRIORITY_MAX)

                        .setOngoing(false)
                        .setAutoCancel(false);

                break;


            case NOTIFICATION_MOVEMENT:

                title = "¿Finalizar sesión Parken?";
                msg = "Detectamos que has abandonado el espacio. ¿Deseas finalizar tu sesión?";
                action1 = "Confirmar";
                action2 = "Omitir";

                actionIntent1 = new Intent(c, ParkenActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("Activity", ParkenActivity.NOTIFICATIONS)
                        .putExtra("ActivityStatus", id)
                        .putExtra("Actions", 1)
                        .putExtra("Mode", data);
                pendingIntent1 = PendingIntent.getActivity(c, id+2, actionIntent1,PendingIntent.FLAG_ONE_SHOT);

                actionIntent2 = new Intent(c, ParkenActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("Activity", ParkenActivity.NOTIFICATIONS)
                        .putExtra("ActivityStatus", id)
                        .putExtra("Actions", 2)
                        .putExtra("Mode", data);
                pendingIntent2 = PendingIntent.getActivity(c, id+3, actionIntent2,PendingIntent.FLAG_ONE_SHOT);


                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        .addAction(R.drawable.ic_address, action1, pendingIntent1)
                        .addAction(R.drawable.places_ic_clear, action2, pendingIntent2)

                        .setPriority(PRIORITY_MAX)

                        .setOngoing(true)
                        .setAutoCancel(true);

                break;



            case NOTIFICATION__RECEIPT_PAYED:

                title = "Sanción pagada";
                msg = "Gracias por tu pago.\n En unos minutos un supervisor habilitará tu vehículo.";


                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        .setOngoing(true)
                        .setAutoCancel(true);


                break;

            case NOTIFICATION_CAR_FREE:

                title = "Vehiculo disponible";
                msg = "Se ha liberado tu vehículo " + data;


                notificacion
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))

                        .setContentIntent(pendingIntent)

                        .setOngoing(true)
                        .setAutoCancel(true);
                break;

                default:
                    break;
        }

        return notificacion;

    }

    public static void cerrar(Context c, int NotificacionId){
        String ns = NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) c.getSystemService(ns);
        nMgr.cancel(NotificacionId);
        Log.e("Cerrar notificación", String.valueOf(NotificacionId));
    }

    public static void cerrarNotiSistema(Context c){

    }

    public static void cerrarTodo(Context c){
        NotificationManager nMgr = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }
}
