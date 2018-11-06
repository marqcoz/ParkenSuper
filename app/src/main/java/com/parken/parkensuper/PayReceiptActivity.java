package com.parken.parkensuper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class PayReceiptActivity extends AppCompatActivity {

    public static final String LABEL_SELECT_ESPACIO = "Selecciona un espacio Parken...";

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;

    private String jsonEspacios;

    private View mPagarSancionFormView;
    private View mProgressView;

    private CharSequence[] espaciosparken;
    private CharSequence[] espaciosparkenId;
    private CharSequence[] espaciosparkenZona;
    private CharSequence[] espaciosparkenIdSancion;
    private CharSequence[] espaciosparkenIdVehiculo;
    private CharSequence[] espaciosparkenMarcaVehiculo;
    private CharSequence[] espaciosparkenModeloVehiculo;
    private CharSequence[] espaciosparkenPlacaVehiculo;

    public CharSequence idEP;
    public CharSequence zonaEspacioParken;
    public CharSequence idSancion;
    public CharSequence idVehiculo;
    public CharSequence marcaVehiculo;
    public CharSequence modeloVehiculo;
    public CharSequence placaVehiculo;

    public double monto;
    ConstraintLayout espacioParken;
    ConstraintLayout vehiculo;
    ConstraintLayout montoTotal;

    TextView txtEspacioParken;
    TextView textVehiculo;
    TextView txtMonto;

    Button pay;
    Button cancel;

    ShPref session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_receipt);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar(true);

        session = new ShPref(this);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        mPagarSancionFormView = findViewById(R.id.paysancion_scroll);
        mProgressView = findViewById(R.id.paysancion_progress);

        espacioParken = findViewById(R.id.constraintEParken);
        vehiculo = findViewById(R.id.constraintVehiculo);
        montoTotal = findViewById(R.id.constraintMontoTotal);

        txtEspacioParken = findViewById(R.id.textViewEP);
        textVehiculo = findViewById(R.id.textViewCar);
        txtMonto = findViewById(R.id.textViewAmountTotal);

        pay = findViewById(R.id.btnPagarSancion);
        cancel = findViewById(R.id.btnCancelarPago);

        cargarDatos();

        espacioParken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Al presio nar obtendremos los espacios Parken disponibles para activar sesiones
                showProgress(true);
                obtenerEspaciosParkenParaPagarSancion(session.getZonaSupervisor());
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmPayReceipt(String.valueOf(idEP)).show();
            }
        });

        txtEspacioParken.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!txtEspacioParken.getText().toString().equals(LABEL_SELECT_ESPACIO)){
                    activarOpciones();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public  void cargarDatos(){

        //Flujo:
        //Mostrar unicamente espacioParkenConstraint
        espacioParken.setVisibility(View.VISIBLE);
        txtEspacioParken.setText(LABEL_SELECT_ESPACIO);

        //Deshabilitar el botón de activar sesión Parken
        pay.setEnabled(false);
        pay.setBackgroundColor(Color.parseColor("#757575"));

}

    public void obtenerEspaciosParken(String jsonVehiculos, String numVehiculo ){

        try{

            if(!numVehiculo.equals("0")){
                JSONArray jsonArray = new JSONArray(jsonVehiculos);

                espaciosparken = new CharSequence[jsonArray.length()];
                espaciosparkenId = new CharSequence[jsonArray.length()];
                espaciosparkenZona = new CharSequence[jsonArray.length()];
                espaciosparkenIdSancion = new CharSequence[jsonArray.length()];
                espaciosparkenIdVehiculo = new CharSequence[jsonArray.length()];
                espaciosparkenMarcaVehiculo = new CharSequence[jsonArray.length()];
                espaciosparkenModeloVehiculo = new CharSequence[jsonArray.length()];
                espaciosparkenPlacaVehiculo = new CharSequence[jsonArray.length()];


                for(int i = 0; i < jsonArray.length(); i++){
                    espaciosparken[i] = jsonArray.getJSONObject(i).getString("idespacioparken");
                    espaciosparkenId[i] = jsonArray.getJSONObject(i).getString("idespacioparken");
                    espaciosparkenZona[i] = jsonArray.getJSONObject(i).getString("zona");
                    espaciosparkenIdSancion[i] = jsonArray.getJSONObject(i).getString("idsancion");
                    espaciosparkenIdVehiculo[i]  = jsonArray.getJSONObject(i).getString("idvehiculo");
                    espaciosparkenMarcaVehiculo[i]  = jsonArray.getJSONObject(i).getString("marcavehiculo");
                    espaciosparkenModeloVehiculo[i]  = jsonArray.getJSONObject(i).getString("modelovehiculo");
                    espaciosparkenPlacaVehiculo[i] = jsonArray.getJSONObject(i).getString("placavahiculo");

                }

            }else{

                espaciosparken = new CharSequence[1];
                espaciosparkenId = new CharSequence[1];
                espaciosparkenZona = new CharSequence[1];
                espaciosparkenIdSancion= new CharSequence[1];
                espaciosparkenIdVehiculo= new CharSequence[1];
                espaciosparkenMarcaVehiculo = new CharSequence[1];
                espaciosparkenModeloVehiculo = new CharSequence[1];
                espaciosparkenPlacaVehiculo = new CharSequence[1];


                espaciosparken[0] = "0";
                espaciosparkenId[0] = "No hay espacios con sanciones pendientes";
                espaciosparkenZona[0] = "";
                espaciosparkenIdSancion[0] = "";
                espaciosparkenIdVehiculo[0] = "";
                espaciosparkenMarcaVehiculo[0] = "";
                espaciosparkenModeloVehiculo[0] = "";
                espaciosparkenPlacaVehiculo[0] = "";
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void activarOpciones(){

        //Mostrar la informacion del vehiculo
        vehiculo.setVisibility(View.VISIBLE);
        String infoVehiculo;
        infoVehiculo = marcaVehiculo + " " + modeloVehiculo + " - " + placaVehiculo;
        textVehiculo.setText(infoVehiculo);

        montoTotal.setVisibility(View.VISIBLE);
        String mon = "$ " + String.valueOf(monto) + ".0 MXN";
        txtMonto.setText(mon);

        pay.setEnabled(true);
        pay.setBackgroundColor(Color.parseColor("#FFF44336"));

        //And everything ready for pay the bill

    }

    public AlertDialog dialogEspaciosParkenList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Selecciona un espacio Parken: ")
                .setItems(espaciosparken,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        idEP = espaciosparkenId[which];
                        zonaEspacioParken = espaciosparkenZona[which];
                        idSancion = espaciosparkenIdSancion[which];
                        idVehiculo = espaciosparkenIdVehiculo[which];
                        marcaVehiculo = espaciosparkenMarcaVehiculo[which];
                        modeloVehiculo = espaciosparkenModeloVehiculo[which];
                        placaVehiculo = espaciosparkenPlacaVehiculo[which];

                        txtEspacioParken.setText(idEP);

                        dialog.dismiss();

                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    public AlertDialog dialogError(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ERROR")
                .setMessage(msg)
                //.setMessage("El vehículo seleccionado se encuentra en una sesión Parken. Selecciona otro.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogSuccess() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registro exitoso")
                .setMessage("Se registro el pago de la sanción en el sistema.")
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                })
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }


    public AlertDialog dialogConfirmPayReceipt(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pagar sanción")
                .setMessage("¿Deseas registrar el pago de la sanción para el espacio Parken " + msg + "?")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pagarSancion(String.valueOf(idSancion));
                                dialog.dismiss();
                            }
                        });

        return builder.create();
    }


    public void pagarSancion(String idSancion){

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idSancion", idSancion);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_PAYING_RECEIPT,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        showProgress(false);
                        Log.d("EPPagarSancion", response.toString());
                        try {
                            if(response.getInt("success") == 1){
                                //Si hay éxito se notifica al usuario
                                dialogSuccess().show();


                            } else{

                                String e = "Ocurrió un error al registrar el pago. Intenta de nuevo.";
                                dialogError(e).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Error", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                            snackbar.show();
                        }

                        return;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("EPPagarSancion", error.toString());
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "No hay conexión con el servidor", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }


public void obtenerEspaciosParkenParaPagarSancion(String zona){

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idZona", zona);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_GETTING_RECEIPTS_AVAILABLES,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        showProgress(false);
                        Log.d("EPPagarSancion", response.toString());
                        try {
                            if(response.getInt("success") == 1){


                                jsonEspacios = response.getString("espaciosparken");
                                monto = response.getDouble("monto");

                                obtenerEspaciosParken(jsonEspacios, response.getString("numeroespaciosparken"));
                                dialogEspaciosParkenList().show();


                            } else{

                                if(response.getInt("success") == 2) {

                                    String e = "No hay espacios parken con sanciones pendientes.";
                                    dialogError(e).show();

                                }else{

                                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Error", Snackbar.LENGTH_LONG);
                                    View sbView = snackbar.getView();
                                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                                    snackbar.show();

                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Error", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                            snackbar.show();
                        }

                        return;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("EPPagarSancion", error.toString());
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "No hay conexión con el servidor", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }


    private void setupActionBar(boolean estatus) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(estatus);
            //actionBar.setTitle("Activar sesión Parken");
        }
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

            mPagarSancionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPagarSancionFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPagarSancionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mPagarSancionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
