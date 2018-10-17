package com.parken.parkensuper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;

public class VerifyActivity extends AppCompatActivity {


    String nombre;
    String apellido;
    String correo;
    String password;
    String origin;

    String code;

    String id;
    String column;
    String value;

    View form;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;

    public static VerifyActivity activityVerify;
    private ShPref session;

    @Override
    public  void onBackPressed(){

        if(session != null){

            if(session.getVerifying()){ } else{ super.onBackPressed(); }

        }else{

            super.onBackPressed();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar(true);

        Intent intent = getIntent();
        origin = intent.getStringExtra("origin");

        if(origin == null){
            origin = "createActivity";
        }

        switch (origin){

            case "createActivity":

                nombre = intent.getStringExtra("nombre");
                apellido = intent.getStringExtra("apellido");
                correo = intent.getStringExtra("correo");
                password = intent.getStringExtra("password");

                break;
            case "informationActivity":

                id = intent.getStringExtra("id");
                column = intent.getStringExtra("column");
                value = intent.getStringExtra("value");

                break;

                default:

                    break;
        }

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();
        activityVerify = this;
        session = new ShPref(activityVerify);
        session.setVerifying(false);

        VerifyFragment verifyFragment = (VerifyFragment)
                getSupportFragmentManager().findFragmentById(R.id.nestedScrollForm);

        if (verifyFragment == null) {
            verifyFragment = VerifyFragment.newInstance();

            Bundle arguments = new Bundle();
            arguments.putString("origin",origin);

            switch (origin){

                case "createActivity":

                    arguments.putString("nombre", nombre);
                    arguments.putString("apellido", apellido);
                    arguments.putString("correo", correo);
                    arguments.putString("password", password);

                    break;

                case "informationActivity":

                    arguments.putString("id", id);
                    arguments.putString("column", column);
                    arguments.putString("value", value);

                    break;

                default:
                    break;
            }

            verifyFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.nestedScrollForm, verifyFragment)
                    .commit();
        }

    }

    public void setupActionBar(boolean estatus) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(estatus);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                onBackPressed();
                finish();

                return true;
        }


        return super.onOptionsItemSelected(item);
    }



}

