package com.parken.parkensuper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class SesionParkenActivity extends AppCompatActivity {

    public static final String CURRENCY = "MXN";
    public static final String ACTIVITY_PARKEN = "ParkenActivity";
    public static final String ACTIVITY_SESION = "SesionParkenActivity";
    public static final String LABEL_SELECT_ESPACIO = "Selecciona un espacio Parken...";
    public static final String LABEL_SELECT_CAR = "Selecciona un vehículo...";
    public static final String LABEL_SELECT_TIME = "Selecciona...";

    public int estatus;



    private AlertDialog _dialog;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;

    private View mSesionParkenFormView;
    private View mProgressView;


    ConstraintLayout espacioParken;
    ConstraintLayout vehiculo;
    ConstraintLayout fecha;
    ConstraintLayout tiempo;
    ConstraintLayout hora;
    ConstraintLayout montoTotal;

    TextView txtEspacioParken;
    TextView textVehiculo;
    TextView txtFecha;
    TextView txtTiempo;
    TextView txtHora;
    TextView txtPuntos;
    TextView txtMonto;
    TextView txtMonto2;
    TextView txtTotal;
    TextView txtPaypal;
    TextView txtTimer;

    Button pay;
    Button cancel;

    ShPref session;

    Intent parken;

    private CharSequence[] espaciosparken;
    private CharSequence[] espaciosparkenId;
    private CharSequence[] espaciosparkenZona;
    private CharSequence[] espaciosparkenDireccion;
    private CharSequence[] espaciosparkenEstatus;


    private String jsonEspacios;

    private  int mYear;
    private  int mMonth;
    private  int mDay;
    private  int mMin;
    private  int mHour;
    private int mSeg;
    private String opc;

    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private int selectedHour;
    private int selectedMin;

    Double precioParken = 5.0;
    public static Float valorPuntos = 1f;

    boolean horaReinicida = false;

    Calendar calendarFechaFinal;
    Calendar calendarFechaFinalFija;
    Calendar c;


    Double precioFinal;
    Double montoPrevio;
    long minutosParken;
    long tiempoPrevio;
    Double puntosRestante;
    String strDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String idEspacioParken;
    private String idZonaParken;
    private String addressEspacioParken;
    private String idSesionParken;
    private String idAutomovilista;
    private String espacioParkenJson;
    public CharSequence idVehiculo;
    public CharSequence marcaVehiculo;
    public CharSequence modeloVehiculo;
    public CharSequence placaVehiculo;

    public CharSequence idEP;
    public CharSequence zonaEspacioParken;
    public CharSequence direccionEspacioParken;
    public CharSequence estatusEspacioParken;

    private double puntosP;
    private String cuentaPayPal;
    private String origin;
    private String carro;
    private int minMinParken;
    private int minMinPago;

    public static SesionParkenActivity activitySesionParken;
    public static PaymentActivity activityPaypal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesion_parken);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar(true);

        activitySesionParken = this;
/*
        Intent intent = getIntent();
        espacioParken = intent.getStringExtra("espacioParken");
        precioParken = intent.getStringExtra("precioParken");

*/

        session = new ShPref(this);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        mSesionParkenFormView = findViewById(R.id.sesion_scroll);
        mProgressView = findViewById(R.id.sesion_progress);

        espacioParken = findViewById(R.id.constraintEParken);
        vehiculo = findViewById(R.id.constraintVehiculo);
        fecha = findViewById(R.id.constraintFechaFin);
        hora = findViewById(R.id.constraintHora);
        tiempo = findViewById(R.id.constraintTiempo);
        montoTotal = findViewById(R.id.constraintMontoTotal);




        txtEspacioParken = findViewById(R.id.textViewEP);
        textVehiculo = findViewById(R.id.textViewCar);
        txtFecha = findViewById(R.id.textViewDateEnd);
        txtTiempo = findViewById(R.id.textViewTime);
        txtHora = findViewById(R.id.textViewHour);
        txtTotal = findViewById(R.id.textViewAmountTotal);

        pay = findViewById(R.id.btnActivarSesion);
        cancel = findViewById(R.id.btnCancelarPago);


        cargarDatos();



        //iniciarTimer();

        //sendCar(session.infoId());

        espacioParken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Al presio nar obtendremos los espacios Parken disponibles para activar sesiones
                showProgress(true);
                obtenerEspaciosParkenParaSesion(session.getZonaSupervisor());
            }
        });

        vehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Agregamos un vehiculo en la bd
                Intent intent = new Intent(SesionParkenActivity.this, AddVehiculoActivity.class);
                intent.putExtra("origin", "SesionParkenActivity");
                startActivity(intent);
                //sendCar(session.infoId());
            }
        });

        textVehiculo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!textVehiculo.getText().toString().equals(LABEL_SELECT_CAR)){
                    activarOpciones(1);
                }

            }
        });


        tiempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             dialogTimerPicker(minMinParken).show();

            }
        });



        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgress(true);
                if(String.valueOf(estatusEspacioParken).equals("DISPONIBLE")){

                    //Verificar que el vehiculo seleccionado este disponible
                    if(!textVehiculo.getText().equals(LABEL_SELECT_CAR)){

                        verificarDisponiblidadVehiculo(String.valueOf(idVehiculo));

                    }else{

                        showProgress(false);
                        dialogSelectCar().show();
                    }

                }else{
                    payAttempt();
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



    private void payAttempt(){

        //Calendar afterFecha = new GregorianCalendar(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMin);
        Calendar afterFecha = new GregorianCalendar(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMin);
        Date date = afterFecha.getTime();
        strDate = sdf.format(date);

        Log.d("FechaFinal", strDate);

        if(idSesionParken != null)
            activarSesionParken(idAutomovilista, idSesionParken, String.valueOf(precioFinal+montoPrevio), String.valueOf(minutosParken+tiempoPrevio), String.valueOf(idVehiculo), strDate, String.valueOf(puntosRestante), String.valueOf(idEP), "", "2");
        else
            activarSesionParken("0", "0", String.valueOf(precioFinal+montoPrevio), String.valueOf(minutosParken+tiempoPrevio), String.valueOf(idVehiculo), strDate, String.valueOf(0.0), String.valueOf(idEP), session.getZonaSupervisor(), "3");

    }

    public void obtenerEspaciosParken(String jsonVehiculos, String numVehiculo ){

        try{

            if(!numVehiculo.equals("0")){
                JSONArray jsonArray = new JSONArray(jsonVehiculos);

                espaciosparken = new CharSequence[jsonArray.length()];
                espaciosparkenId = new CharSequence[jsonArray.length()];
                espaciosparkenZona = new CharSequence[jsonArray.length()];
                espaciosparkenDireccion = new CharSequence[jsonArray.length()];
                espaciosparkenEstatus = new CharSequence[jsonArray.length()];

                for(int i = 0; i < jsonArray.length(); i++){
                    espaciosparken[i] = jsonArray.getJSONObject(i).getString("idespacioparken");
                    espaciosparkenId[i] = jsonArray.getJSONObject(i).getString("idespacioparken");
                    espaciosparkenZona[i] = jsonArray.getJSONObject(i).getString("zona");
                    espaciosparkenDireccion[i] = jsonArray.getJSONObject(i).getString("direccion");
                    espaciosparkenEstatus[i] = jsonArray.getJSONObject(i).getString("estatusespacioparken");

                }
                /*
                espaciosparken[jsonArray.length()] = "No hay espacios disponibles modifica el estatus para activar una sesión";
                espaciosparkenId[jsonArray.length()] = "0";
                espaciosparkenZona[jsonArray.length()] = "";
                espaciosparkenDireccion[jsonArray.length()] = "";
                espaciosparkenEstatus[jsonArray.length()] = "";
                */

            }else{

                espaciosparken = new CharSequence[1];
                espaciosparkenId = new CharSequence[1];
                espaciosparkenZona = new CharSequence[1];
                espaciosparkenDireccion = new CharSequence[1];
                espaciosparkenEstatus = new CharSequence[1];
                espaciosparken[0] = "0";
                espaciosparkenId[0] = "No hay espacios disponibles modifica el estatus para activar una sesión";
                espaciosparkenZona[0] = "";
                espaciosparkenDireccion[0] = "";
                espaciosparkenEstatus[0] = "";
            }



        }catch (Exception e){
            e.printStackTrace();
        }

    }




    public void obtenerEspaciosParkenParaSesion(String zona){

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idZona", zona);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_GETTING_PARKEN_SPACES_AVAILABLES,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        showProgress(false);
                        Log.d("EPSesion", response.toString());
                        try {
                            if(response.getInt("success") == 1){



                                jsonEspacios = response.getString("espaciosparken");

                                obtenerEspaciosParken(jsonEspacios, response.getString("numeroespaciosparken"));
                                dialogEspaciosParkenList().show();


                            } else{
                                String e = "No hay espacios disponibles para iniciar una sesión.\nModifica el estatus de un espacio a DISPONIBLE para activar una sesión.";
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
                        Log.d("ConsultarVehiculos", error.toString());
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "No hay conexión con el servidor", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }

    public void obtenerEstatusEspacioParken(String id){

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idEspacioParken", id);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_GETTING_PARKEN_SPACES_STATUS,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){

                        Log.d("EPStatus", response.toString());
                        showProgress(false);

                        try {
                            if(response.getInt("success") == 1){

                                if(response.getString("estatusespacioparken").equals("DISPONIBLE")){
                                    activarOpciones(0);
                                    c = Calendar.getInstance();
                                    montoPrevio = 0.0;
                                    tiempoPrevio = 0;

                                }
                                if(response.getString("estatusespacioparken").equals("OCUPADO")){

                                    //Simplemente modificamos el textVehiculo

                                    montoPrevio = Double.valueOf(response.getString("montopago"));
                                    tiempoPrevio = Long.valueOf(response.getString("tiempo"));

                                    idSesionParken = String.valueOf(response.getInt("idsesionparken"));
                                    idAutomovilista = String.valueOf(response.getInt("idautomovilista"));
                                    puntosRestante = Double.valueOf(response.getString("puntosparken"));

                                    idVehiculo= response.getString("idvehiculo");
                                    marcaVehiculo= response.getString("marcavehiculo");
                                    modeloVehiculo= response.getString("modelovehiculo");
                                    placaVehiculo= response.getString("placavehiculo");
                                    textVehiculo.setText(modeloVehiculo + " - " + placaVehiculo);
                                    //Y bloqueamos el constraint
                                    vehiculo.setEnabled(false);

                                    //Y obtenemos la fecha final
                                    //la asignamos a c
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
                                    Date date = sdf.parse(response.getString("fechafinalformatted"));

                                    c = Calendar.getInstance();
                                    c.setTime(date);


                                }



                            } else{

                                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Error con la conexión al servidor.", Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                                snackbar.show();

                            }

                            return;

                        } catch (JSONException e) {
                            showProgress(false);
                            e.printStackTrace();
                            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Error", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                            snackbar.show();
                        }catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("EPStatus", error.toString());
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "No hay conexión con el servidor", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }



    public void verificarDisponiblidadVehiculo(String idVehiculo){

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idVehiculo", idVehiculo);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_CAR_AVAILABLE,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        showProgress(false);
                        Log.d("VerificarDisponibilidad", response.toString());
                        try {
                            if(response.getString("success").equals("1") && response.getString("Disponibilidad").equals("DISPONIBLE")){
                                //Si esta disponible entonces procedemos con el pago, si no, le decimos al coño que cambie de vehiculo
                                payAttempt();
                            } else{
                                dialogCarBusy().show();
                                textVehiculo.setText(LABEL_SELECT_CAR);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("VerificarDisponibilidad", error.toString());
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "No hay conexión con el servidor", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }

    public void activarSesionParken(final String automovilista, final String idSesionParken, final String monto, final String tiempo, final String idVehiculo, final String fechaFinal, final String puntosParken, String ep, String zp, String opc){

        final HashMap<String, String> parametros = new HashMap();

        //dialogError(opc + " - " + ep + " - " + zp).show();
        parametros.put("idAutomovilista", automovilista);
        parametros.put("idSesionParken", idSesionParken);
        parametros.put("FechaFinal", fechaFinal);
        parametros.put("Monto", monto);
        parametros.put("Tiempo", tiempo);
        parametros.put("idVehiculo", idVehiculo);
        parametros.put("puntosParken", puntosParken);
        parametros.put("opc", opc);
        if(opc.equals("3")){
            parametros.put("idEspacioParken", ep);
            parametros.put("idZonaParken", zp);
        }


/*
        parametros.put("idSesionParken", automovilista);
        parametros.put("FechaFinal", automovilista);
        parametros.put("Monto", automovilista);//Sumar al monto final
        parametros.put("Tiempo", automovilista);//Sumar al tiempo final
        parametros.put("Estatus", automovilista);

        parametros.put("idAutomovilista", automovilista);
        parametros.put("idAutomovilista", automovilista);
        parametros.put("idEspacioParken", automovilista);
        parametros.put("idZonaParken", automovilista);
*/

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_ACIVATE_SESSION_PARKEN,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ActivarSesion", response.toString());
                        showProgress(false);
                        try {
                            if(response.getString("success").equals("1")){
                                    parken = new Intent(SesionParkenActivity.this, ParkenActivity.class);
                                    parken.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    parken.putExtra("Activity", ParkenActivity.ACTIVITY_SESION_PARKEN);
                                    parken.putExtra("ActivityStatus", ParkenActivity.MESSAGE_PAY_SUCCESS);
                                    parken.putExtra("idSesionParken", idSesionParken);
                                    parken.putExtra("DireccionEP", addressEspacioParken);
                                    parken.putExtra("FechaFinal", fechaFinal);
                                    parken.putExtra("Monto", monto);
                                    parken.putExtra("TiempoEnMinutos", Integer.parseInt(tiempo));
                                    parken.putExtra("idVehiculo", idVehiculo);
                                    parken.putExtra("ModeloVehiculo", modeloVehiculo);
                                    parken.putExtra("PlacaVehiculo", placaVehiculo);

                                    //Información para el receipt
                                    parken.putExtra("PuntosP", puntosP);
                                    parken.putExtra("PrecioFinal", precioFinal);
                                    parken.putExtra("ValorPuntos", valorPuntos);
                                    parken.putExtra("PuntosRestante", puntosRestante);
                                    parken.putExtra("MinutosParken",minutosParken);
                                    parken.putExtra("SelectedMin", selectedMin);
                                    parken.putExtra("SelectedHour", selectedHour);


                            if(String.valueOf(estatusEspacioParken).equals("OCUPADO")){
                                selectedHour = c.get(Calendar.HOUR_OF_DAY);
                                selectedMin = c.get(Calendar.MINUTE);
                                selectedDay = c.get(Calendar.DAY_OF_MONTH);
                                selectedMonth = c.get(Calendar.MONTH);
                                selectedYear = c.get(Calendar.YEAR);
                            }else {
                                c = Calendar.getInstance();
                                c.add(Calendar.MINUTE, (int)minutosParken);
                                selectedHour = c.get(Calendar.HOUR_OF_DAY);
                                selectedMin = c.get(Calendar.MINUTE);
                                selectedDay = c.get(Calendar.DAY_OF_MONTH);
                                selectedMonth = c.get(Calendar.MONTH);
                                selectedYear = c.get(Calendar.YEAR);
                            }

                                    dialogPayWithPoints(precioFinal, minutosParken,selectedHour,selectedMin,selectedDay,selectedMonth + 1, selectedYear).show();

                            } else{
                                    dialogError("Error al iniciar la sesión Parken").show();
                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialogError("Error al iniciar la sesión Parken").show();
                            return;
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("ActivarSesion", error.toString());
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "No hay conexión con el servidor", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }


    public AlertDialog dialogEspaciosParkenList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Selecciona un espacio Parken: ")
                .setItems(espaciosparken,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            idEP = espaciosparkenId[which];
                            zonaEspacioParken = espaciosparkenZona[which];
                            direccionEspacioParken = espaciosparkenDireccion[which];
                            estatusEspacioParken = espaciosparkenEstatus[which];

                            if(String.valueOf(espaciosparkenEstatus[which]).equals("DISPONIBLE"))
                                pay.setText("Activar sesión Parken");

                            if(String.valueOf(espaciosparkenEstatus[which]).equals("OCUPADO"))
                                pay.setText("Renovar sesión Parken");

                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        activarOpciones(0);
                    }
                });

        return builder.create();
    }

    public AlertDialog dialogPayWithPoints(double precioFinal,
                                           long minutosParken,
                                           int selectedHour,
                                           int selectedMin,
                                           int selectedDay,
                                           int selectedMonth,
                                           int selectedYear) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        View v = inflater.inflate(R.layout.alertdialog_receipt, null);
        builder.setView(v);


        TextView txtMonto = v.findViewById(R.id.textViewMontoCobrado);
        TextView txtTiempo = v.findViewById(R.id.textViewTiempo);
        TextView txtRecordatorio = v.findViewById(R.id.textViewRecordatorio);
        Button btnAceptar = (Button) v.findViewById(R.id.btnAceptar);



        String mon = "$ " + String.valueOf(precioFinal) + "0 " + SesionParkenActivity.CURRENCY;
        txtMonto.setText(mon);

        String tiem = String.valueOf(minutosParken) + " minutos";
        txtTiempo.setText(tiem);
        String reco;
        String fecha;

        Calendar hoy = Calendar.getInstance();

        if(hoy.get(Calendar.YEAR) == selectedYear &&
                hoy.get(Calendar.MONTH)+1 == selectedMonth &&
                hoy.get(Calendar.DAY_OF_MONTH) == selectedDay){
            fecha = "";
        }else{
            fecha = String.valueOf(selectedDay) + " - " + String.valueOf(selectedMonth) +" - " +String.valueOf(selectedYear) + " - ";
        }


        if(selectedMin < 10){
            reco = fecha + selectedHour + ":0" + selectedMin + " hrs";
        }else{
            reco = fecha + selectedHour + ":" + selectedMin + " hrs";
        }
        txtRecordatorio.setText(reco);


        final AlertDialog dialog = builder.create();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });



        return dialog;
    }



    public AlertDialog dialogTimerPicker(final int minParken) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activitySesionParken);

        LayoutInflater inflater = activitySesionParken.getLayoutInflater();

        View v = inflater.inflate(R.layout.alertdialog_time, null);
        builder.setView(v);

        final EditText h = v.findViewById(R.id.editTextHoras);
        final EditText m = v.findViewById(R.id.editTextMinutos);

        TextView tm = v.findViewById(R.id.txtTimeMin);

        Button establecer = v.findViewById(R.id.btnEstablecerTiempo);
        Button clearTime= v.findViewById(R.id.btnClearTime);

        String horas = String.valueOf(minParken/60);
        String minutos;
        if((minParken)%60 == 0){
            minutos = obtenerTiempoString((long)minParken);
        } else{
            minutos = String.valueOf((minParken)%60);
        }

        tm.setText("El tiempo mínimo es de " + obtenerTiempoString((long)minParken));

        h.setText(horas);
        m.setText(minutos);

        final AlertDialog dialog = builder.create();

        m.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                int min;
                int hora;

                if(m.getText().toString().equals("")){
                    min = 0;
                }else{
                    min =Integer.parseInt(m.getText().toString());
                }

                if(h.getText().toString().equals("")) {
                    hora = 0;
                }else{
                    hora = Integer.parseInt(h.getText().toString());
                }


                if(min > 59){
                    //Se suma una hora
                    hora = hora + 1;
                    h.setText(String.valueOf(hora));
                    //Se actualizan los minutos
                    min = min - 60;
                    m.setText(String.valueOf(min));

                }


            }
        });

        clearTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                h.setText("0");
                m.setText(String.valueOf(minMinParken));
            }
        });

        establecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(h.getText().toString().equals("")){
                    h.setText("0");
                }
                if(m.getText().toString().equals("")){
                    m.setText("0");
                }

                int auxInt = Integer.parseInt(m.getText().toString());

                if(auxInt < minMinParken && h.getText().toString().equals("0")){
                    m.setText(String.valueOf(minMinParken));
                }

                int min = ( Integer.parseInt( h.getText().toString() )*60 ) + Integer.parseInt(m.getText().toString());
                //min

                minutosParken = min;

                txtTiempo.setText(obtenerTiempoString(min));

                activarOpciones(2);

                Log.d("MyTimerPicker", String.valueOf(min));

                dialog.cancel();
            }
        });

        return dialog;
    }




    public AlertDialog dialogCarBusy() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vehículo no disponible")
                .setMessage("El vehículo seleccionado se encuentra en una sesión Parken. Selecciona otro.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogSelectCar() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona un vehículo")
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



    public AlertDialog dialogSuccessParkenSession() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("EXITO")
                //.setMessage("El vehículo seleccionado se encuentra en una sesión Parken. Selecciona otro.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                            }
                        });

        return builder.create();
    }



    private String obtenerMesNombre(int mes){
        switch (mes){
            case 1:
                return "Enero";

            case 2:
                return "Febrero";

            case 3:
                return "Marzo";

            case 4:
                return "Abril";

            case 5:
                return "Mayo";

            case 6:
                return "Junio";

            case 7:
                return "Julio";

            case 8:
                return "Agosto";

            case 9:
                return "Septiembre";

            case 10:
                return "Octubre";

            case 11:
                return "Noviembre";

            case 12:
                return "Diciembre";

                default:
                    break;

        }
        return "ERROR";

    }



    public static String obtenerTiempoString(long minutos){

        long horas = minutos/60;

        String min;
        String hour;
        String h;
        String m;
        String fin;

        if(horas >0){

            hour = String.valueOf(horas);
            min = String.valueOf(minutos -(horas*60));

            if(horas ==1){
                h = " hora ";
            }else{
                h = " horas ";
            }
            if(minutos -(horas*60) == 1){
                m = " minuto";
            }else{
                if(minutos -(horas*60)== 0){
                    m = "";
                    min = "";
                }else{
                    m = " minutos";
                }

            }

            fin = hour + h + min + m;


        }else{
            min = String.valueOf(minutos);

            if(minutos ==1){
                m = " minuto";
            }else{
                m = " minutos";
            }

            fin = min + m;
        }

//      1:45 horas
//      45 minutos
//      1 hora 45 minutos
//      return hour + h + min + m;
//      return min + m;
        return fin;
    }

    public Calendar obtenerFechaNow(int seg){

        int limiteMinutos = 5;

        final Calendar c = Calendar.getInstance();
        Calendar d;
        Log.d("onDataSet", "FechaNow:"+seg);
        switch(seg){
            case 1:
                d = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                break;
            case 2:
                d = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                break;
            case 3:
                d = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)+limiteMinutos);
                Date date = d.getTime();
                strDate = sdf.format(date);
                Log.d("CalendarObtenerNow", strDate);
                break;
                default:
                    d = null;
                    break;
        }
        //Log.d("onDataSet", d);
        return d;
    }

    public void activarOpciones(int opc){

        if(opc == 0){

            if(idEP != txtEspacioParken.getText().toString() && idEP != null) {
                txtEspacioParken.setText(idEP);
                vehiculo.setEnabled(true);

                //Checamos el estatus del espacioParken
                showProgress(true);
                obtenerEstatusEspacioParken(String.valueOf(idEP));

                vehiculo.setVisibility(View.VISIBLE);
                vehiculo.setEnabled(true);
                textVehiculo.setText(LABEL_SELECT_CAR);

                //Cerramos los otros constrainst por si acaso
                txtTiempo.setText(LABEL_SELECT_TIME);
                tiempo.setVisibility(View.GONE);
                hora.setVisibility(View.GONE);
                fecha.setVisibility(View.GONE);
                montoTotal.setVisibility(View.GONE);
                //Y deshabilitamos el botón de activar

                pay.setEnabled(false);
                //pay.setBackgroundColor(Color.parseColor("#757575"));
                pay.setBackground(getDrawable(R.drawable.button_rounded_gray));

            }

        }



        if(opc == 1){

            if(txtEspacioParken.getText().equals(LABEL_SELECT_ESPACIO)){

                tiempo.setVisibility(View.GONE);

            }else{

                tiempo.setEnabled(true);
                txtTiempo.setText(LABEL_SELECT_TIME);
                tiempo.setVisibility(View.VISIBLE);
                //Cerramos los otros constrainst por si acaso
                hora.setVisibility(View.GONE);
                fecha.setVisibility(View.GONE);
                montoTotal.setVisibility(View.GONE);
                //Y deshabilitamos el botón de activar

                pay.setEnabled(false);
                //pay.setBackgroundColor(Color.parseColor("#757575"));
                pay.setBackground(getDrawable(R.drawable.button_rounded_gray));
            }

        }

        if(opc == 2){


            if(String.valueOf(estatusEspacioParken).equals("DISPONIBLE")){

                //Para este instante ya tenemos los minutos parken
                c.add(Calendar.MINUTE, (int)minutosParken);

            }else{
                //Si esta ocupado, obtenemos la fechafinal de la sesión que esta activa
                //y le agregamos los minutos seleccionado
                c.add(Calendar.MINUTE, (int)minutosParken);


            }

            tiempo.setVisibility(View.VISIBLE);
            fecha.setVisibility(View.VISIBLE);
            hora.setVisibility(View.VISIBLE);

            //Mostrar a fecha en txtFecha
            String dat = (c.get(Calendar.DAY_OF_MONTH) + " " + obtenerMesNombre(c.get(Calendar.MONTH) + 1))+ ", " + c.get(Calendar.YEAR);
            txtFecha.setText(dat);
            //Mostrar la hora en txtHora

            if(c.get(Calendar.MINUTE) >= 0 && c.get(Calendar.MINUTE) < 10){
                String sMin = "0" + String.valueOf(c.get(Calendar.MINUTE));
                dat = c.get(Calendar.HOUR_OF_DAY) + ":" + sMin + " hrs";
            }else{
                dat = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + " hrs";
            }
            txtHora.setText(dat);



            //minutosParken = obtenerTiempo(now,afterFecha);
            txtTiempo.setText(String.valueOf(obtenerTiempoString(minutosParken)));

            activarOpciones(3);


        }

/*
        Calendar now;

        if(origin.equals(ACTIVITY_SESION)){
            if(!horaReinicida) {
                calendarFechaFinal.add(Calendar.MINUTE, -5);
                horaReinicida = true;
            }
            now = calendarFechaFinal;
        }else{
            now = obtenerFechaNow(1);
        }

        Calendar afterFecha = new GregorianCalendar(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMin);
        //La nueva forma de obtener el tiempo es:
        //Crear un calendario con la fecha de hoy
        //añadirle los nuevos minutos
        //obtener la fecha
        //Mostrarla
        //nuevosMinutosParken
        final Calendar c;
        if(origin.equals(ACTIVITY_SESION)){
            //Creamos el calendario con la fecha final
            //y le agragamos los minutosParken
            c = new GregorianCalendar(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMin);
            //para este instante ya tenemos los minutos parken
            c.add(Calendar.MINUTE, (int)minutosParken);

        } else{

            c = Calendar.getInstance();
            //para este instante ya tenemos los minutos parken
            c.add(Calendar.MINUTE, (int)minutosParken);
        }

        tiempo.setVisibility(View.VISIBLE);
        fecha.setVisibility(View.VISIBLE);
        hora.setVisibility(View.VISIBLE);

        //Mostrar a fecha en txtFecha
        String dat = (c.get(Calendar.DAY_OF_MONTH) + " " + obtenerMesNombre(c.get(Calendar.MONTH) + 1))+ ", " + c.get(Calendar.YEAR);
        txtFecha.setText(dat);
        //Mostrar la hora en txtHora

        if(c.get(Calendar.MINUTE) >= 0 && c.get(Calendar.MINUTE) < 10){
            String sMin = "0" + String.valueOf(c.get(Calendar.MINUTE));
            dat = c.get(Calendar.HOUR_OF_DAY) + ":" + sMin + " hrs";
        }else{
            dat = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + " hrs";
        }
        txtHora.setText(dat);



        //minutosParken = obtenerTiempo(now,afterFecha);
        txtTiempo.setText(String.valueOf(obtenerTiempoString(minutosParken)));
        obtenerPuntosParken(session.infoId());
        */


        if(opc == 3){


            montoTotal.setVisibility(View.VISIBLE);
            precioFinal = minutosParken*precioParken;

            String montoFinal = "$ " + String.valueOf(precioFinal) + "0. " + CURRENCY;
            txtTotal.setText(montoFinal);
            activarOpciones(4);
        }


        if(opc == 4){

            //obtenerPaypal();
            pay.setEnabled(true);
            //pay.setBackgroundColor(Color.parseColor("#FF34495E"));
            pay.setBackground(getDrawable(R.drawable.button_rounded));
        }

    }

    public  void cargarDatos(){

        minMinParken = 5;

        //Flujo:
        //Mostrar unicamente espacioParkenConstraint
        espacioParken.setVisibility(View.VISIBLE);
        txtEspacioParken.setText(LABEL_SELECT_ESPACIO);

        //Deshabilitar el botón de activar sesión Parken
        pay.setEnabled(false);
        //pay.setBackgroundColor(Color.parseColor("#757575"));
        pay.setBackground(getDrawable(R.drawable.button_rounded_gray));
        //Obtener la fecha actual
        selectedYear = obtenerFechaNow(2).get(Calendar.YEAR);
        selectedMonth = obtenerFechaNow(2).get(Calendar.MONTH);
        selectedDay= obtenerFechaNow(2).get(Calendar.DAY_OF_MONTH);

        txtTiempo.setText(LABEL_SELECT_TIME);

        //txtFecha.setText(selectedDay+" "+obtenerMesNombre(selectedMonth+1)+", "+selectedYear);
        calendarFechaFinalFija = new GregorianCalendar(selectedYear,selectedMonth,selectedDay, 0,0);
        //txtHora.setText("Selecciona...");
        pay.setEnabled(false);
        //pay.setBackgroundColor(Color.parseColor("#757575"));
        pay.setBackground(getDrawable(R.drawable.button_rounded_gray));
        //pay.setVisibility(View.INVISIBLE);;
        montoPrevio = 0.0;
        tiempoPrevio = 0;




        /*

        int minutos = ParkenActivity.minutoTimerPago;
        int segundos = ParkenActivity.segundoTimerPago;

        if(ParkenActivity.segundoTimerTolerance == 59)
            minMinParken = ParkenActivity.minutoTimerTolerance + 1;
        else
            minMinParken = ParkenActivity.minutoTimerTolerance;

        if(ParkenActivity.segundoTimerPago == 59)
            minMinPago = ParkenActivity.minutoTimerPago + 1;
        else
            minMinPago = ParkenActivity.minutoTimerPago;

        Intent intent = getIntent();
        if (null != intent) {
            origin = intent.getStringExtra("Activity");
            if(origin!=null)
            Log.d("CargarDatos", origin);
            //idEspacioParken = intent.getStringExtra("jsonEspacioParken");

            espacioParkenJson = intent.getStringExtra("jsonEspacioParken");
            if(espacioParkenJson!=null)
            Log.d("CargarDatos", espacioParkenJson);
            idSesionParken = intent.getStringExtra("idSesionParken");
            if(idSesionParken!=null)
            Log.d("CargarDatos", idSesionParken);
            cuentaPayPal = intent.getStringExtra("paypal");
            if(cuentaPayPal!=null)
            Log.d("CargarDatos", cuentaPayPal);

            minutos = intent.getIntExtra("minRestante", 4);
            segundos = intent.getIntExtra("segRestante", 59);

            estatus = intent.getIntExtra("ActivityExtra", ParkenActivity.LOAD);



            if(espacioParkenJson!=null){
            try {
                JSONObject jsonArray = new JSONObject(espacioParkenJson);
                idEspacioParken = jsonArray.getString("id");
                addressEspacioParken = jsonArray.getString("direccion");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            }

        }

        fecha.setEnabled(false);
        hora.setEnabled(false);

        //origin = ACTIVITY_SESION;
        if(origin.equals(ACTIVITY_PARKEN)){
            timerTask.execute(minutos, segundos);
            if(estatus == ParkenActivity.LOAD){
                _dialog = dialogTimeAlert();
                _dialog.show();
            }

            //txtEspacioParken.setText(String.valueOf(session.getParkenSpace()));
            txtEspacioParken.setText(idEspacioParken);
            textVehiculo.setText(LABEL_SELECT_CAR);
            //Obtener la fecha actual
            selectedYear = obtenerFechaNow(2).get(Calendar.YEAR);
            selectedMonth = obtenerFechaNow(2).get(Calendar.MONTH);
            selectedDay= obtenerFechaNow(2).get(Calendar.DAY_OF_MONTH);

            txtTiempo.setText(LABEL_SELECT_TIME);

            //txtFecha.setText(selectedDay+" "+obtenerMesNombre(selectedMonth+1)+", "+selectedYear);
            calendarFechaFinalFija = new GregorianCalendar(selectedYear,selectedMonth,selectedDay, 0,0);
            //txtHora.setText("Selecciona...");
            pay.setEnabled(false);
            pay.setBackgroundColor(Color.parseColor("#757575"));
            //pay.setVisibility(View.INVISIBLE);;
            montoPrevio = 0.0;
            tiempoPrevio = 0;
            opc = "1";

        }


        if(origin.equals(ACTIVITY_SESION)){
            if(intent.getStringExtra("Monto") != null)
            montoPrevio = Double.parseDouble(intent.getStringExtra("Monto"));

            tiempoPrevio = (intent.getIntExtra("TiempoEnMinutos", -1));
            ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("Renovar sesión Parken");
            timer.setVisibility(View.GONE);
            txtEspacioParken.setText(idEspacioParken);
            vehiculo.setEnabled(false);
            if(intent.getStringExtra("idVehiculo") != null)
            idVehiculo = intent.getStringExtra("idVehiculo");
            if(intent.getStringExtra("MarcaVehiculo") != null)
            marcaVehiculo = intent.getStringExtra("MarcaVehiculo");
            if(intent.getStringExtra("ModeloVehiculo") != null)
            modeloVehiculo = intent.getStringExtra("ModeloVehiculo");
            if(intent.getStringExtra("PlacaVehiculo") != null)
            placaVehiculo = intent.getStringExtra("PlacaVehiculo");
            carro = modeloVehiculo + " - " + placaVehiculo;
            textVehiculo.setText(carro);

            calendarFechaFinal = Calendar.getInstance();
            calendarFechaFinalFija = Calendar.getInstance();
            try {
                if(intent.getStringExtra("FechaFinal") != null)
                calendarFechaFinal.setTime(sdf.parse(intent.getStringExtra("FechaFinal")));
                calendarFechaFinalFija.clear();
                calendarFechaFinalFija.setTime(sdf.parse(intent.getStringExtra("FechaFinal")));
                calendarFechaFinalFija.add(Calendar.MINUTE,minMinParken);
            } catch (ParseException e) {
                e.printStackTrace();

                //PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
                //Agregar una bandera para que el server sepa que solo va a actualizar el monto,
                //los puntos, la fechafinal y el tiempo
                //PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
            }

            calendarFechaFinal.add(Calendar.MINUTE, minMinParken);


            //Obtener la fecha final de la sesion pasada
            /*
            selectedYear = calendarFechaFinal.get(Calendar.YEAR);
            selectedMonth = calendarFechaFinal.get(Calendar.MONTH);
            selectedDay= calendarFechaFinal.get(Calendar.DAY_OF_MONTH);
            */

        /*
            selectedYear = intent.getIntExtra("selectedYear", 0);
            selectedMonth = intent.getIntExtra("selectedMonth", 0);
            selectedDay= intent.getIntExtra("selectedDay", 0);
            selectedMin =  intent.getIntExtra("selectedMin", 0);
            selectedHour = intent.getIntExtra("selectedHour", 0);
            //txtFecha.setText(selectedDay+" "+obtenerMesNombre(selectedMonth+1)+", "+selectedYear);

            //selectedHour = calendarFechaFinal.get(Calendar.HOUR_OF_DAY);
            //selectedMin = calendarFechaFinal.get(Calendar.MINUTE);
            String min;

            if (selectedMin < 10) min = "0"+ String.valueOf(selectedMin);
            else min =String.valueOf(selectedMin);

            String lastHour = String.valueOf(selectedHour) + ":" + min + " hrs";

            //txtHora.setText("Selecciona...");
            txtTiempo.setText(LABEL_SELECT_TIME);
            pay.setEnabled(false);
            pay.setText("Renovar sesión Parken");
            pay.setBackgroundColor(Color.parseColor("#757575"));
            //pay.setVisibility(View.INVISIBLE);;
            opc = "2";



        }


*/

        onTokenRefresh();
        getToken();

    }
    private static final String TAG = "MyFirebaseIIDService";

    @Override

    public  void getToken(){}
    public void onTokenRefresh(){
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
    private void setupActionBar(boolean estatus) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(estatus);
            actionBar.setTitle("Activar sesión Parken");
        }
    }

    @Override
    public void onBackPressed(){
            super.onBackPressed();
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

            mSesionParkenFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSesionParkenFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSesionParkenFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSesionParkenFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
