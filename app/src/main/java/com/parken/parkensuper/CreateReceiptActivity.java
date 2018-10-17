package com.parken.parkensuper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CreateReceiptActivity extends AppCompatActivity {

    Button cancelar;
    Button sancionar;

    private View mProgressView;
    private View mReceiptFormView;
    private View mReceiptView;

    ConstraintLayout espacioparken;
    ConstraintLayout automovilista;
    TextView idEspacioparken;
    TextView montoTotal;

    EditText placa;
    EditText modelo;
    EditText marca;

    EditText nombre;
    EditText apellido;

    String idEspacioParken;

    String placaVehiculo;
    String marcaVehiculo;
    String modeloVehiculo;
    String nombreAutomovilista;
    String apellidoAutomovilista;

    String idAutomovilista;
    String idVehiculo;
    String idSesionParken;

    String estatusEP;

    ImageView imgAlert;
    TextView textAlert;

    private String jsonEspacios;

    private CharSequence[] espaciosparken;
    private CharSequence[] espaciosparkenId;
    private CharSequence[] espaciosparkenZona;
    private CharSequence[] espaciosparkenDireccion;
    private CharSequence[] espaciosparkenEstatus;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    ModifyParkenSpaceActivity modifyParkenSpaceActivity = new ModifyParkenSpaceActivity();
    ShPref session;

    String origin;
    boolean espacioparkenEnable = false;
    boolean isReport;
    double monto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_receipt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        session = new ShPref(this);

        mReceiptFormView = findViewById(R.id.sesion_scroll);
        mReceiptView = findViewById(R.id.sesion_form);
        mProgressView = findViewById(R.id.receipt_progress);

        cancelar = findViewById(R.id.btnCancelarSancion);
        sancionar = findViewById(R.id.btnSancionar);

        espacioparken = findViewById(R.id.constraintEParken);
        automovilista = findViewById(R.id.constraintAutomovilista);
        idEspacioparken = findViewById(R.id.textViewEP);
        montoTotal = findViewById(R.id.textViewAmountTotal);

        placa = findViewById(R.id.editTextPlaca);
        modelo = findViewById(R.id.editTextModelo);
        marca = findViewById(R.id.editTextMarca);

        nombre = findViewById(R.id.editTextNombre);
        apellido = findViewById(R.id.editTextApellido);

        imgAlert = findViewById(R.id.imageViewAlert2);
        textAlert = findViewById(R.id.textViewAlert2);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        obtenerDatos();

        espacioparken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEspaciosParkenList().show();
            }
        });

        sancionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(datosCorrectos()){

                    switch (origin){
                        case "ParkenActivity":

                           agregarVehiculo("0", marcaVehiculo, modeloVehiculo, placaVehiculo);

                            break;

                        case "ModifyParkenSpaceActivity":

                            if(estatusEP != null && estatusEP.equals("DISPONIBLE")){

                                agregarVehiculo("0", marcaVehiculo, modeloVehiculo, placaVehiculo);

                            }else {

                                crearSancion(monto,idAutomovilista,
                                        idVehiculo,
                                        session.infoId(),
                                        idEspacioParken,
                                        session.getZonaSupervisor(),
                                        idSesionParken);

                            }

                            break;

                        default:
                            break;
                    }
                }
            }

        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private boolean datosCorrectos(){
        // Reset errors.
        placa.setError(null);
        modelo.setError(null);
        marca.setError(null);
        nombre.setError(null);
        apellido.setError(null);


        //Obtenemos las variables
        idEspacioParken = idEspacioparken.getText().toString().trim();
        placaVehiculo = placa.getText().toString().trim().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        modeloVehiculo = modelo.getText().toString().trim();
        marcaVehiculo = marca.getText().toString().trim();

        nombreAutomovilista = nombre.getText().toString().trim();
        apellidoAutomovilista = apellido.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        //Validamos que todos los datos se hayan ingresado

        if (idEspacioParken.equals("Selecciona")) {
            dialogError("Selecciona un espacio Parken").show();
            focusView = placa;
            cancel = true;
        }

        if (TextUtils.isEmpty(placaVehiculo)) {
            placa.setError("El campo es obligatorio");
            focusView = placa;
            cancel = true;
        }

        if (TextUtils.isEmpty(modeloVehiculo)) {
            modelo.setError("El campo es obligatorio");
            focusView = modelo;
            cancel = true;
        }

        if (TextUtils.isEmpty(marcaVehiculo)) {
            marca.setError("El campo es obligatorio");
            focusView = marca;
            cancel = true;
        }

        if (TextUtils.isEmpty(nombreAutomovilista) && origin.equals("ParkenActivity")) {
            nombre.setError("El campo es obligatorio");
            focusView = nombre;
            cancel = true;
        }

        if (TextUtils.isEmpty(apellidoAutomovilista) && origin.equals("ParkenActivity")) {
            apellido.setError("El campo es obligatorio");
            focusView = apellido;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    private void obtenerDatos(){

        Intent intent = getIntent();
        if (null != intent) {
            origin = intent.getStringExtra("Activity");
            //origin = "ParkenActivity";

            switch (origin){
                case "ParkenActivity":

                    espacioparkenEnable = true;
                    espacioparken.setEnabled(espacioparkenEnable);
                    idEspacioparken.setText("Selecciona");
                    showProgress(true);
                    obtenerEspaciosParken(session.getZonaSupervisor());
                    automovilista.setVisibility(View.GONE);
                    nombre.setText("Automovilista");
                    apellido.setText("invitado");
                    monto = 700;

                    break;

                case "ModifyParkenSpaceActivity":

                    isReport = intent.getBooleanExtra("onReport", false);


                    espacioparkenEnable = false;
                    espacioparken.setEnabled(espacioparkenEnable);

                    String infoEspacioParken = intent.getStringExtra("DataReceipt");

                    Log.e("ObtenerDatos", infoEspacioParken);

                    try {

                        JSONObject jsonInfoEspacioParken = new JSONObject(infoEspacioParken);

                        idEspacioParken = jsonInfoEspacioParken.getString("idespacioparken");

                        //Datos espacio Parken
                        idEspacioparken.setText(idEspacioParken);

                        estatusEP = jsonInfoEspacioParken.getString("estatusespacioparken");
                        if(estatusEP.equals("DISPONIBLE")){

                            automovilista.setVisibility(View.GONE);
                            nombre.setText("Automovilista");
                            apellido.setText("invitado");


                        }else{

                            //No se podrán modificar los datos del automovilista
                            //Datos vehiculo
                            idVehiculo = jsonInfoEspacioParken.getString("idvehiculo");
                            if(idVehiculo.equals("0")){

                            }else {
                                placaVehiculo = jsonInfoEspacioParken.getString("placavehiculo");
                                marcaVehiculo = jsonInfoEspacioParken.getString("marcavehiculo");
                                modeloVehiculo = jsonInfoEspacioParken.getString("modelovehiculo");
                                placa.setText(placaVehiculo);
                                placa.setEnabled(false);
                                marca.setText(marcaVehiculo);
                                marca.setEnabled(false);
                                modelo.setText(modeloVehiculo);
                                modelo.setEnabled(false);
                            }


                            //Datos automovilista
                            idAutomovilista = jsonInfoEspacioParken.getString("idautomovilista");
                            nombreAutomovilista = jsonInfoEspacioParken.getString("nombreautomovilista");
                            apellidoAutomovilista=  jsonInfoEspacioParken.getString("apellidoautomovilista");
                            automovilista.setVisibility(View.VISIBLE);
                            nombre.setText(nombreAutomovilista);
                            nombre.setEnabled(false);
                            apellido.setText(apellidoAutomovilista);
                            apellido.setEnabled(false);

                            //Datos sesion Parken
                            idSesionParken = jsonInfoEspacioParken.getString("idsesionparken");


                        }

                        monto = 700;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;

                default:
                    break;
            }

            montoTotal.setText("$ " + monto + "0 MXN");
        }
    }

    private void agregarVehiculo(String automovilista, String marca, String modelo, final String placa){

            HashMap<String, String> parametros = new HashMap();
            parametros.put("marca", marca);
            parametros.put("modelo", modelo);
            parametros.put("placa", placa);
            parametros.put("idAutomovilista", automovilista);

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    Jeison.URL_DRIVER_ADD_CAR,
                    new JSONObject(parametros),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if(response.getString("success").equals("1")){
                                    //Log.d("LoginActivity", response.getString("success"));
                                    showProgress(false);
                                    //Sancionar
                                    crearSancion(monto,"0",
                                            response.getString("idVehiculo"),
                                            session.infoId(),
                                            idEspacioParken,
                                            session.getZonaSupervisor(),
                                            "0");
                                    //dialogNewCarSuccess(response.getString("idVehiculo")).show();

                                }else{


                                    if(!response.getString("success").equals("0")){
                                        obtenerIdVehiculoYSancionar(placa);
                                    }else{
                                        dialogError("Error al agregar el vehículo.\nIntenta más tarde.").show();
                                        mostrarViewError();
                                    }

                                }

                                return;

                            } catch (JSONException e) {
                                e.printStackTrace();
                                showProgress(false);
                                dialogError("Error al agregar el vehículo.\nIntenta más tarde.").show();
                                mostrarViewError();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showProgress(false);
                            dialogError("Error de conexión con el servidor.\nIntenta más tarde.").show();
                            mostrarViewError();

                        }
                    });

            fRequestQueue.add(jsArrayRequest);


    }


    private void obtenerIdVehiculoYSancionar(String placaVehiculo){

        HashMap<String, String> parametros = new HashMap();
        parametros.put("placa", placaVehiculo);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_GETTING_ID_CAR,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Si se recibe un 1, se agregó exitosamente
                        Log.e("ObtenerIdVehiculo",response.toString());
                        try {
                            if(response.getInt("success") == 1){
                                //Log.d("LoginActivity", response.getString("success"));
                                showProgress(false);
                                //Sancionar
                                crearSancion(monto,"0",
                                        response.getString("id"),
                                        session.infoId(),
                                        idEspacioParken,
                                        session.getZonaSupervisor(),
                                        "0");
                                //dialogNewCarSuccess(response.getString("idVehiculo")).show();

                            }else{
                                showProgress(false);

                                dialogError("Error al agregar el vehículo.\nIntenta más tarde.").show();
                                mostrarViewError();
                            }

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            dialogError("Error al agregar el vehículo.\nIntenta más tarde.").show();
                            mostrarViewError();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        dialogError("Error de conexión con el servidor.\nIntenta más tarde.").show();
                        mostrarViewError();

                    }
                });

        fRequestQueue.add(jsArrayRequest);


    }


    private void mostrarViewError(){

        //Ocultamos el view principal
        mReceiptView.setVisibility(View.GONE);

        //Mostramos los botones de error
        imgAlert.setVisibility(View.VISIBLE);
        textAlert.setVisibility(View.VISIBLE);
        textAlert.setText("No es posible conectarse con el servidor.\nIntenta de nuevo.");


    }

    private void crearSancion(double monto,
                              String idAutomovilista,
                              String idVehiculo,
                              String idSupervisor,
                              String idEspacio,
                              String idZona,
                              String  idSesion){

        final String TAG = "CrearSancion";

        HashMap<String, String> parametros = new HashMap();
        parametros.put("monto", String.valueOf(monto));
        parametros.put("idAutomovilista", idAutomovilista);
        parametros.put("idVehiculo", idVehiculo);
        parametros.put("idSupervisor", idSupervisor);
        parametros.put("idEspacio", idEspacio);
        parametros.put("idZona", idZona);
        parametros.put("idSesion", idSesion);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_CREATE_RECEIPT,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, response.toString());

                        try {
                            showProgress(false);

                            if(response.getInt("success") == 1){

                                dialogSancionCreadaExito().show();

                            }else{

                                dialogError("No se creó la sanción.\nIntenta de nuevo.").show();

                            }

                            return;

                        } catch (JSONException e) {
                            showProgress(false);
                            e.printStackTrace();
                            dialogError("No se creó la sanción.\nIntenta de nuevo.").show();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        dialogError("Error de conexión con el servidor.\nIntenta de nuevo.").show();
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

                        idEspacioParken = String.valueOf(espaciosparkenId[which]);
                        idEspacioparken.setText(idEspacioParken);

                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //activarOpciones(0);
                    }
                });

        return builder.create();
    }

    public void obtenerEspaciosParken(String zona){

        final String TAG = "ObtenerEspaciosParken";

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idZona", zona);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_GETTING_ALL_PARKEN_SPACES,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, response.toString());

                        try {
                            showProgress(false);

                            if(response.getInt("success") == 1){

                                jsonEspacios = response.getString("espaciosparken");

                                asignarEspaciosParken(jsonEspacios, response.getString("numeroespaciosparken"));


                            }else{

                                dialogError("No hay espacios Parken en la zona.").show();
                                mostrarViewError();


                            }

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            dialogError("Error al obtener los espacios Parken").show();
                            mostrarViewError();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        dialogError("Error al establecer la conexión con el servidor.").show();
                        mostrarViewError();
                        Log.d(TAG, error.toString());
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }


    public void asignarEspaciosParken(String jsonVehiculos, String numVehiculo ){

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



    public AlertDialog dialogError(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Error")
                .setMessage(msg)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(isReport){
                            startActivity(new Intent(CreateReceiptActivity.this, ModifyParkenSpaceActivity.class)
                                    .putExtra("Activity", ModifyParkenSpaceActivity.REPORT_SUCCESFUL));
                            finish();
                        }

                    }
                });

        return builder.create();
    }

    public AlertDialog dialogSancionCreadaExito() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Sanción generada")
                .setMessage("Se ha generado la sanción exitosamente. Recuerda colocar el inmovilizador al vehículo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(origin.equals("ModifyParkenSpaceActivity")) {
                            //modifyParkenSpaceActivity.modifyParkenSpaceActivity.txtEspacioParken.setText(idEspacioParken);
                        }

                        if(isReport){
                            startActivity(new Intent(CreateReceiptActivity.this, ModifyParkenSpaceActivity.class)
                            .putExtra("Activity", ModifyParkenSpaceActivity.REPORT_SUCCESFUL));
                        }
                        finish();
                    }
                });

        return builder.create();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                //onBackPressed();
                switch(origin){
                    case "ParkenActivity":
                        finish();
                        break;
                    case "ModifyParkenSpaceActivity":
                        finish();
                        break;
                        default:
                            break;
                }


                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * ShowProgress
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mReceiptFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mReceiptFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mReceiptFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mReceiptFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}
