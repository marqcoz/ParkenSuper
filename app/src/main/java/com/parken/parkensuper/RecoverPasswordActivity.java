package com.parken.parkensuper;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.volley.RequestQueue;

public class RecoverPasswordActivity extends AppCompatActivity {


    protected RequestQueue fRequestQueue;

    public static RecoverPasswordActivity activityRecoverPassword= new RecoverPasswordActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar(true);


        RecoverPasswordFragment recoverPasswordFragment = (RecoverPasswordFragment)
                getSupportFragmentManager().findFragmentById(R.id.nestedScrollForm);

        if (recoverPasswordFragment == null) {
            recoverPasswordFragment = RecoverPasswordFragment.newInstance();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.nestedScrollForm, recoverPasswordFragment)
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

}
