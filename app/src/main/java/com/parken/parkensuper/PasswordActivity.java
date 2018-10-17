package com.parken.parkensuper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PasswordActivity extends AppCompatActivity {

    private String origin;
    private String id;
    private String value;
    private String column;
    private String password1;
    private String password2;

    Button refreshPass;
    AutoCompleteTextView pass1;
    AutoCompleteTextView pass2;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    private EditProfileActivity editAct = new EditProfileActivity();
    private InformationActivity infAct = new InformationActivity();

    public static PasswordActivity activityPassword;

    private View mProgressView;
    private View mPassFormView;

    private ShPref session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar("");

        refreshPass = findViewById(R.id.btnSavePassword);
        pass1 = findViewById(R.id.autoCompleteTextViewPass);
        pass2 = findViewById(R.id.autoCompleteTextViewPass2);

        mPassFormView = findViewById(R.id.password_form);
        mProgressView = findViewById(R.id.password_progress);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        session = new ShPref(this);

        Intent intent = getIntent();

            origin = intent.getStringExtra("origin");


            if(origin.equals("editProfileActivity")) {

                id = intent.getStringExtra("id");
                column = intent.getStringExtra("column");
                value =  intent.getStringExtra("value");

            }

            if(origin.equals("recoverPasswordActivity")){

                id = intent.getStringExtra("id");
                column = intent.getStringExtra("column");
                value =  intent.getStringExtra("value");

                setupActionBar(origin);
            }

            refreshPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(pass2.getWindowToken(), 0);
                    if(validarPassword()){
                        showProgress(true);
                        actualizarPerfilSupervisor(id, column.toLowerCase(), password1);
                    }
                }
            });


        pass2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(pass2.getWindowToken(), 0);
                    if(validarPassword()){
                        showProgress(true);
                        actualizarPerfilSupervisor(id, column.toLowerCase(), password1);
                    }

                    return true;
                }
                return false;
            }
        });



    }


    public boolean validarPassword(){

        View focusView = null;

        password1 = pass1.getText().toString();
        password2 = pass2.getText().toString();

        //Verificamos si los dos son iguales
        if(TextUtils.isEmpty(password1)){
            pass1.setError("Se requiere el campo");
            focusView = pass1;
            return false;
        }

        if(TextUtils.isEmpty(password2)){
            pass2.setError("Se requiere el campo");
            focusView = pass2;
            return false;
        }

        if(password1.equals(password2)){
            return true;
        }else{
            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Las contraseñas no coinciden.", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            snackbar.show();
            return false;
        }
    }

    public void actualizarPerfilSupervisor(String id, String column, String value){

        Log.e("ActualizarSupervisor", id + column + value);
        HashMap<String, String> parametros = new HashMap();
        parametros.put("id", id);
        parametros.put("column", column);
        parametros.put("value", value);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_UPDATE,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("LoginActivity", response.toString());
                        try {
                            if(response.getString("success").equals("1")){
                                showProgress(false);
                                //Si los datos se actualizaron correctamente, entonces cerramos el activity
                                //y/o cerramos el otro activity y lo reiniciamos o lo volvemos a abrir
                                //dialogUpdateSuccess().show();
                                finish();
                                Log.d("Change", "password");

                                if(origin.equals("editProfileActivity")) {
                                    startActivity(new Intent(PasswordActivity.this, InformationActivity.class)
                                            .putExtra("Change", "password"));
                                }
                                if(origin.equals("recoverPasswordActivity")){
                                    startActivity(new Intent(PasswordActivity.this, ParkenActivity.class));
                                    session.setInfo(response.getString("idSupervisor"), response.getString("Nombre"),
                                            response.getString("Apellido"),response.getString("Email"), response.getString("Contrasena"), response.getString("Celular"),
                                            response.getString("Direccion"), response.getString("Estatus"), response.getString("Zona"));

                                    session.setLoggedin(true);

                                }


                            }else{
                                showProgress(false);
                                if(response.getString("success").equals("0")){
                                    dialogUpdateFailed().show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialogUpdateFailed().show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        dialogNoConnection().show();



                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }

    public AlertDialog dialogNoConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Error")
                .setMessage("No se puede realizar la conexión con el servidor. Intente de nuevo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogUpdateFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Error")
                .setMessage("Error al actualizar el perfil. Intente de nuevo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogUpdateSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Actualización exitosa")
                .setMessage("Se ha modificado el perfil exitosamente.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                editAct.editProfileActivity.finish();
                                infAct.informationActivity.finish();
                                startActivity(new Intent(PasswordActivity.this,InformationActivity.class));
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

            mPassFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPassFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPassFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mPassFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    private void setupActionBar(String act) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            if(act.equals("recoverPasswordActivity")){
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setTitle("Reestablecer contraseña");

            }else{
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("Actualizar contraseña");

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {

            onBackPressed();
            //startActivity(new Intent(getActivity(), ParkenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(origin.equals("editProfileActivity"))
            super.onBackPressed();


    }
}
