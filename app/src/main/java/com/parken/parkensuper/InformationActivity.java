package com.parken.parkensuper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class InformationActivity extends AppCompatActivity {

    private ConstraintLayout zonaparken;
    private ConstraintLayout nombre;
    private ConstraintLayout apellido;
    private ConstraintLayout correo;
    private ConstraintLayout celular;
    private ConstraintLayout contrasena;

    private TextView zona;
    private TextView name;
    private TextView last;
    private TextView mail;
    private TextView pass;
    private TextView cel;
    private TextView msgNoProfile;

    private ImageView imgProfile;

    private LinearLayout linear;

    ShPref session;
    static InformationActivity informationActivity;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;

    private View mProgressView;
    private View mInfoFormView;
    private NestedScrollView nested;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar(true);

        session = new ShPref(this);
        informationActivity = this;

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        mInfoFormView = findViewById(R.id.nested_form_profile);
        mProgressView = findViewById(R.id.info_progress);
        nested = findViewById(R.id.nested_form_profile);

        linear = findViewById(R.id.linear_profile);

        imgProfile = findViewById(R.id.imageView2);
        msgNoProfile = findViewById(R.id.textViewMessageNoProfile);

        zonaparken = findViewById(R.id.constraintZonaParken);
        nombre = findViewById(R.id.constraintNombre);
        apellido = findViewById(R.id.constraintApellido);
        correo = findViewById(R.id.constraintCorreo);
        celular = findViewById(R.id.constraintCelular);
        contrasena = findViewById(R.id.constraintContrasena);

        zona = findViewById(R.id.textViewZona);
        name = findViewById(R.id.textViewName);
        last = findViewById(R.id.textViewLast);
        mail = findViewById(R.id.textViewMail);
        cel = findViewById(R.id.textViewCel);
        pass = findViewById(R.id.textViewPass);

        //session.setInfo(response.getString("id"),nombre, apellido, correo, password, phone, "0.0");
        showProgress(true);
        obtenerPerfilSupervisor(session.infoId());


        nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
              Intent intent = new Intent(InformationActivity.this, EditProfileActivity.class);
              intent.putExtra("id", session.infoId());
              intent.putExtra("column", "Nombre");
              intent.putExtra("value", name.getText().toString());
              startActivity(intent);
              */

                showSnackbar();


            }
        });

        apellido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                Intent intent = new Intent(InformationActivity.this, EditProfileActivity.class);
                intent.putExtra("id", session.infoId());
                intent.putExtra("column", "Apellido");
                intent.putExtra("value", last.getText().toString());
                startActivity(intent);

                */

                showSnackbar();
            }
        });

        correo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                Intent intent = new Intent(InformationActivity.this, EditProfileActivity.class);
                intent.putExtra("id", session.infoId());
                intent.putExtra("column", "email");
                intent.putExtra("value", mail.getText().toString());
                startActivity(intent);
                */

                Snackbar snackbar = Snackbar.make(view, "No es posible modificar el correo electrónico.", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                snackbar.show();

            }
        });

        celular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(InformationActivity.this, VerifyActivity.class);
                intent.putExtra("id", session.infoId());
                intent.putExtra("column", "Celular");
                intent.putExtra("value", cel.getText().toString());
                intent.putExtra("nombre", name.getText().toString());

                /*
                intent.putExtra("apellido", last.getText().toString());
                intent.putExtra("correo", mail.getText().toString());
                intent.putExtra("password", pass.getText().toString());
                */
                intent.putExtra("origin","informationActivity");
                startActivity(intent);
            }
        });

        contrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformationActivity.this, EditProfileActivity.class);
                intent.putExtra("id", session.infoId());
                intent.putExtra("column", "Contrasena");
                intent.putExtra("value", pass.getText().toString());
                startActivity(intent);

            }
        });
    }

    public void obtenerPerfilSupervisor(String idSupervisor) {
        HashMap<String, String> parametros = new HashMap();
        parametros.put("idSupervisor", idSupervisor);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_DATA,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            Log.d("InformationActivity", response.toString());
                            if(response.getString("success").equals("1")){
                                showProgress(false);
                                session.setInfo(response.getString("idSupervisor"), response.getString("Nombre"),
                                        response.getString("Apellido"),response.getString("Email"), response.getString("Contrasena"), response.getString("Celular"),
                                        response.getString("Direccion"), response.getString("Estatus"), response.getString("Zona"));

                                //session.setVehiculos("");
                                zona.setText(response.getString("Zona") + " - " + response.getString("NombreZona"));
                                name.setText(response.getString("Nombre"));
                                last.setText(response.getString("Apellido"));
                                mail.setText(response.getString("Email"));
                                cel.setText(response.getString("Celular"));
                                pass.setText(response.getString("Contrasena"));

                            }else{
                                showProgress(false);

                                messageFailed();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            messageFailed();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("LoginActivity", "Error Respuesta en JSON: " + error.getMessage());
                        showProgress(false);
                        messageFailed();
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }

    public void showSnackbar(){

        Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "No es posible modificar ésta información", Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        snackbar.show();
    }


    public void messageFailed(){
        Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Error de conexión.", Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        snackbar.show();

        //imgProfile.getLayoutParams().height = 60;
        //imgProfile.getLayoutParams().width = 60;
        imgProfile.setImageResource(R.drawable.ic_no_profile);
        msgNoProfile.setVisibility(View.VISIBLE);
        msgNoProfile.setText("Error al cargar tu perfil.");
        zonaparken.setVisibility(View.GONE);
        nombre.setVisibility(View.GONE);
        name.setText("");
        apellido.setVisibility(View.GONE);
        last.setText("");
        correo.setVisibility(View.GONE);
        mail.setText("");
        celular.setVisibility(View.GONE);
        cel.setText("");
        contrasena.setVisibility(View.GONE);
        pass.setText("");
        //nombre.setVisibility(View.INVISIBLE);


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

            mInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mInfoFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
    protected void onNewIntent(Intent intent){
        obtenerPerfilSupervisor(session.infoId());
        Snackbar snackbar;
        View sbView;

        if(intent.getStringExtra("Change") != null) {
            Log.d("NewIntentInfo", intent.getStringExtra("Change"));
            switch (intent.getStringExtra("Change")){
                case "nombre":
                    snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Nombre actualizado", Snackbar.LENGTH_LONG);
                    sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.greenLight));

                    snackbar.show();
                    break;
                case "apellido":
                    snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Apellido actualizado", Snackbar.LENGTH_LONG);
                    sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.greenLight));
                    snackbar.show();
                    break;
                case "mail":
                    snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Correo electrónico actualizado", Snackbar.LENGTH_LONG);
                    sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.greenLight));
                    snackbar.show();
                    break;
                case "password":
                    snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Contraseña actualizada", Snackbar.LENGTH_LONG);
                    sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.greenLight));
                    snackbar.show();
                    break;
                case "cel":
                    snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Celular actualizado", Snackbar.LENGTH_LONG);
                    sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.greenLight));
                    snackbar.show();
                    break;

                    default:
                        break;

            }
        }
    }
}
