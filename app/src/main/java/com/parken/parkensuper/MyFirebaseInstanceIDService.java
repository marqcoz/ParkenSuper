package com.parken.parkensuper;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    private ShPref session;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.

        session = new ShPref(ParkenActivity.activityParken);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("supervisor");

        Log.d(TAG, "Refreshed token: " + refreshedToken);

        volley = VolleySingleton.getInstance(ParkenActivity.activityParken.getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        //Enviar al server el token correspondiente al automovilista
        sendTokenDriver(token, session.infoId());
    }

    private void sendTokenDriver(final String token, String id) {

        HashMap<String, String> parametros = new HashMap();
        parametros.put("id", id);
        parametros.put("token", token);
        parametros.put("user", "2");

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_TOKEN,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(response.getString("success").equals("1")){
                                Log.d(TAG, "Refreshed token: " + token);
                                session.setToken(token);
                                return;

                            }else{
                                Log.d(TAG, "Unsuccesfully refreshed token ");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Unsuccesfully refreshed token");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Unsuccesfully refreshed token: " + error);

                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }
}
