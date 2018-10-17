package com.parken.parkensuper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    //Botones
    Button login;
    Button recover;

    //TextViews
    AutoCompleteTextView mail;
    EditText pass;

    //Variables
    String correo;
    String contrasena;
    String app = "2";

    //Objectos
    private View mProgressView;
    private View mLoginFormView;
    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    private Jeison url;
    public static LoginActivity activityLogin;
    private ShPref session;
    //private ParkenActivity actParken;
    RequestQueue requestQueue;
    JsonObjectRequest jsArrayRequest;


    //Inicio de toodo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activityLogin = this;

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        login = findViewById(R.id.btnLogin);
        recover = findViewById(R.id.btnRecover);
        mail = findViewById(R.id.textViewHour);
        pass = findViewById(R.id.editTextPass);


        session = new ShPref(getApplicationContext());
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        //Si se editan los TextView
        pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    
                    correo = mail.getText().toString();
                    contrasena = pass.getText().toString();
                    try {
                        attemptLogin();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        //Si se presiona el boton Iniciar Sesión
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Enviar los datos al servidor para realizar la validación
                correo = mail.getText().toString();
                contrasena = pass.getText().toString();
                try {
                    attemptLogin();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        //Si se presiona el botón Recuperar contraseña
        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recoverPassword = new Intent(LoginActivity.this, RecoverPasswordActivity.class);
                startActivity(recoverPassword);

            }
        });
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() throws InterruptedException {

        // Reset errors.
        mail.setError(null);
        pass.setError(null);

        // Store values at the time of the login attempt.
        String email = mail.getText().toString();
        String password = pass.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            pass.setError(getString(R.string.error_invalid_password));
            focusView = pass;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mail.setError(getString(R.string.error_field_required));
            focusView = mail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mail.setError(getString(R.string.error_invalid_email));
            focusView = mail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            //mAuthTask = new LoginActivity.UserLoginTask(correo, contrasena, app);
            //mAuthTask.execute((Void) null);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(recover.getWindowToken(), 0);
            enviarJson(correo, contrasena, app);

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        //return password.length() > 4;
        return true;
    }



    public void enviarJson(String correo, String contrasena, String app) {
        HashMap<String, String> parametros = new HashMap();
        parametros.put("correo", correo);
        parametros.put("contrasena", contrasena);
        parametros.put("app", app);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_LOGIN,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //onConnectionFinished();
                        Log.d("LoginActivity", response.toString());
                        try{

                            if(response.getString("success").equals("1")){
                                Log.d("LoginActivity", response.getString("success"));

                                iniciarSesion(response);

                            }else{
                                showProgress(false);
                                dialogNoSesion().show();
                                Log.d("LoginActivity", response.getString("success"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //onConnectionFailed(error.getMessage());
                        Log.d("LoginActivity", "Error Respuesta en JSON: " + error.getMessage());
                        showProgress(false);
                        dialogNoConnection().show();
                    }
                });

        fRequestQueue.add(jsArrayRequest);
        //onPreStartConnection();
    }

    private void iniciarSesion(JSONObject response) throws JSONException {

        showProgress(false);

        //Guardar la información del perfil en SharedPreferences
        session.setInfo(response.getString("id"), response.getString("Nombre"),
                response.getString("Apellido"),response.getString("Email"), response.getString("Contrasena"), response.getString("Celular"),
                response.getString("Direccion"), response.getString("Estatus"), response.getString("Zona"));

        //Establecer inicio de sesion activo en SharedPreferences
        session.setLoggedin(true);


        //Obtener el token para las notificaciones de Firebase
        MyFirebaseInstanceIDService token = new MyFirebaseInstanceIDService();
        token.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN", "Refreshed token: " + refreshedToken);

        //Suscribirse al Topic "supervisor"
        FirebaseMessaging.getInstance().subscribeToTopic("supervisor");


        //Iniciar ParkenActivity
        Intent login = new Intent(LoginActivity.this, ParkenActivity.class);
        login.putExtra("idSupervisor", response.getString("id"));
        finish();
        startActivity(login);

    }


    public AlertDialog dialogNoSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityLogin);

        builder.setTitle(getString(R.string.alert_no_login_tittle))
                .setMessage(getString(R.string.alert_no_login_message))
                .setPositiveButton(getString(R.string.affirmative),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogNoConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityLogin);

        builder.setTitle(getString(R.string.alert_no_connection_tittle))
                .setMessage(getString(R.string.alert_no_connection_message))
                .setPositiveButton(getString(R.string.affirmative),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }

    private AlertDialog dialogPermissionRequired() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityLogin);
        builder.setTitle(getString(R.string.permission_location_title)).setMessage(getString(R.string.permission_location_message)).setPositiveButton(getString(R.string.affirmative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                return;

            }
        });

        return builder.create();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
