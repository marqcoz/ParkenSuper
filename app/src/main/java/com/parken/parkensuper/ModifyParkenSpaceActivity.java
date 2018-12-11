package com.parken.parkensuper;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

public class ModifyParkenSpaceActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {

    public static final String REPORT_SUCCESFUL = "Reportefinalizado";
    public static final String REPORT_UNSUCCESSFUL = "ReportefinalizadoMAL";
    public static final int EXITO = 100;
    public static final int FAIL = 200;

    private AlertDialog dialogPermissionLocationRequired;

    private int contAuxPermission = 0;


    private VolleySingleton volley;

    protected RequestQueue fRequestQueue;
    public JsonObjectRequest jsArrayRequest;

    private View mProgressView;
    private View mModifyFormView;

    ShPref session;

    private String jsonEspacios;

    String origin;


    ConstraintLayout espacioparken;
    ConstraintLayout estatusep;
    ConstraintLayout newestatusep;

    CoordinatorLayout map;

    Button actualizar;

    TextView txtEspacioParken;
    TextView txtInfoEspacioParken;
    TextView txtInfoAutomovilista;
    TextView txtInfoVehiculo;
    TextView txtInfoSesion;

    ImageView imgAlert;
    TextView txtAlert;

    TextView txtEstatusEspacioParken;
    TextView txtNewEstatusEspacioParken;

    private CharSequence[] espaciosparken;
    private CharSequence[] espaciosparkenId;
    private CharSequence[] espaciosparkenZona;
    private CharSequence[] espaciosparkenDireccion;
    private CharSequence[] espaciosparkenEstatus;

    private CharSequence[] newestatus;
    private String estatusNuevo;


    public CharSequence idEP;
    public String idReporteParken;
    public CharSequence zonaEspacioParken;
    public CharSequence direccionEspacioParken;
    public CharSequence estatusEspacioParken;

    HashMap<String,Marker> hashMapMarker = new HashMap<>();

    private GoogleMap mMap;

    public static ModifyParkenSpaceActivity modifyParkenSpaceActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_parken_space);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        session = new ShPref(this);

        mModifyFormView = findViewById(R.id.scrollViewForm);
        mProgressView = findViewById(R.id.modify_progress);

        espacioparken = findViewById(R.id.constraintEspacioParken);
        estatusep = findViewById(R.id.constraintInfoEspacioParken);
        newestatusep = findViewById(R.id.constraintNuevoEstatusEspacioParken);
        actualizar = findViewById(R.id.btnActualizar);

        txtEspacioParken = findViewById(R.id.textViewIDEspacioParken);
        txtInfoEspacioParken = findViewById(R.id.textViewInfoEspacioParken);
        txtInfoAutomovilista = findViewById(R.id.textViewNombre);
        txtInfoVehiculo = findViewById(R.id.textViewVehiculo);
        txtInfoSesion = findViewById(R.id.textViewSesion);
        txtEstatusEspacioParken = findViewById(R.id.textViewEstatusEspacioParken);
        txtNewEstatusEspacioParken = findViewById(R.id.textViewNuevoEstatusEspacioParken);

        map = findViewById(R.id.mapCoordinator);

        imgAlert = findViewById(R.id.imageViewAlerrt);
        txtAlert = findViewById(R.id.textViewAlert);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        requestPermissionLocation();

        //Dibujar la zona correspondiente
        //Se obtienen todos los espacios parken
        //Se muestran todos en el mapa


        //Se esconden los constraint de información
        //Se esconde el constraint de nuevo estatus

        //Al presionar espacio parken se muestran todos los espacios Parken

        //Al presionar un marker especifico se muestra se modifica el text view con la información

        //Cada que se modifique el textViewIDEspacioParken se llamara el metodo
        //obtenerEstatusEspacioParken
        //Con la información dada se llenarán los otros contraints

        //Establecer las ocpciones de nuevos estatus

        //Modificar el estatus del espacio Parken
        //Y checar que otras modificaciones se realizarán

        txtEspacioParken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogEspaciosParkenList().show();

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


                showProgress(true);
                //if(!txtEspacioParken.getText().toString().equals(String.valueOf(idEP)))
                obtenerInformacionEspacioParken(txtEspacioParken.getText().toString(), 1);

            }
        });

        txtNewEstatusEspacioParken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNewEstatusOptions().show();

            }
        });

        txtNewEstatusEspacioParken.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(txtNewEstatusEspacioParken.getText().toString().equals("DISPONIBLE")) txtNewEstatusEspacioParken.setTextColor(Color.parseColor("#27ae60"));
                if(txtNewEstatusEspacioParken.getText().toString().equals("OCUPADO")) txtNewEstatusEspacioParken.setTextColor(Color.parseColor("#F44336"));
                if(txtNewEstatusEspacioParken.getText().toString().equals("REPORTADO")) txtNewEstatusEspacioParken.setTextColor(Color.BLUE);
                if(txtNewEstatusEspacioParken.getText().toString().equals("SANCIONADO")) txtNewEstatusEspacioParken.setTextColor(Color.RED);

                if(!txtNewEstatusEspacioParken.getText().toString().equals("Selecciona"))
                    actualizar.setVisibility(View.VISIBLE);
                else
                    actualizar.setVisibility(View.GONE);


            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgress(true);
                obtenerInformacionEspacioParken(txtEspacioParken.getText().toString(), 2);
            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(dialogPermissionLocationRequired == null && contAuxPermission != 1) {
                dialogPermissionLocationRequired = dialogPermissionLocationRequired();
                dialogPermissionLocationRequired.show();
            }
            return;
        }

        mMap.setMyLocationEnabled(true);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setIndoorLevelPickerEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);

        // Add a marker in Sydney and move the camera
        LatLng df = new LatLng(19.432581, -99.133161);
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(df, 10.0f));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(df, 10));

        try {

            cargarDatos();

            if(origin != null && origin.equals("ParkenActivity")) {
                mMap.setOnMarkerClickListener(this);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(){

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.d("Marker",marker.getSnippet());

        String idEspacioParken = marker.getSnippet();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 20));

        //Al presionar el marker se carga la información

        if(!txtEspacioParken.getText().toString().equals(idEspacioParken)){
            txtEspacioParken.setText(idEspacioParken);
        }


        return false;
    }


    public void cargarDatos() throws JSONException {

        Intent intent = getIntent();
        if (null != intent) {

            showProgress(true);

            origin = intent.getStringExtra("Activity");

            if (origin != null) {

                switch (origin) {
                    case "ReportGeofence":

                        //Desabilitamos el constraint espacioparken
                        txtEspacioParken.setEnabled(false);

                        ActionBar actionBar = getSupportActionBar();
                        actionBar.setDisplayHomeAsUpEnabled(false);

                        //Desabilitamos el listener markerClick
                        //mMap.setOnMarkerClickListener(this);

                        JSONObject jsonReporte = new JSONObject(intent.getStringExtra("jsonReporte"));
                        //Establecer las coordenadas
                        //JSONArray jsonReporteCoo = new JSONArray(jsonReporte.getString("coordenada"));
                        //Los invertí, no se por que
                        /*
                        double longitud = jsonReporteCoo.getJSONObject(0).getDouble("longitud");
                        double latitud = jsonReporteCoo.getJSONObject(0).getDouble("latitud");
                        */
                        double latitud = Double.valueOf(jsonReporte.getString("latitud"));
                        double longitud = Double.valueOf(jsonReporte.getString("longitud"));
                        LatLng destination = new LatLng(longitud, latitud);
                        idEP =  jsonReporte.getString("idespacioparken");

                        idReporteParken = jsonReporte.getString("idreporte");
                        //Colocamos el marker con espacioparken
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(destination)
                                .title("Espacio Parken:").snippet(
                                        String.valueOf(idEP)));
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parken));

                        //Centramos el mapa en ese espacio
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18));

                        //Para obtener la infromación del espacio parken
                        //Asignamos el id al txtview
                        txtEspacioParken.setText(idEP);

                        break;

                    case "ParkenActivity":

                        obtenerDibujoZonaParken(session.getZonaSupervisor());
                        obtenerMarkersEspaciosParken(session.getZonaSupervisor());

                        espacioparken.setVisibility(View.VISIBLE);
                        estatusep.setVisibility(View.GONE);
                        newestatusep.setVisibility(View.GONE);
                        actualizar.setVisibility(View.GONE);
                        break;

                    default:
                        break;
                }
            }
        }

    }

    //Método para dibujar las zonas parken cercanas y centra el mapa dentro de todas las zonas
    private void dibujarZonaParken(String zonaParken){

        try{

            JSONArray jsonArray = new JSONArray(zonaParken);


            for(int i = 0; i < jsonArray.length(); i++){


                JSONArray jsonArrayPolygon = new JSONArray(jsonArray.getJSONObject(i).getString("coordenadas"));
                PolygonOptions rectOptions = new PolygonOptions();

                JSONArray jsonArrayCentro = new JSONArray(jsonArray.getJSONObject(i).getString("centro"));
                LatLng cen= new LatLng(Double.parseDouble(jsonArrayCentro.getJSONObject(0).getString("longitud")), Double.parseDouble(jsonArrayCentro.getJSONObject(0).getString("latitud")));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cen, 17));

                for(int j = 0; j < jsonArrayPolygon.length(); j++){
                    LatLng coord= new LatLng(Double.parseDouble(jsonArrayPolygon.getJSONObject(j).getString("longitud")), Double.parseDouble(jsonArrayPolygon.getJSONObject(j).getString("latitud")));
                    rectOptions.add(coord);
                }

                Polygon polygon = mMap.addPolygon(rectOptions);
                polygon.setFillColor(Color.argb(50, 244, 67, 54));
                polygon.setStrokeWidth(6);
                polygon.setStrokeColor(Color.rgb(244, 67, 54));


                /*
                JSONArray jsonArrayCentroide = new JSONArray(jsonArray.getJSONObject(i).getString("centro"));
                LatLng destination = new LatLng(Double.parseDouble(jsonArrayCentroide.getJSONObject(0).getString("longitud")), Double.parseDouble(jsonArrayCentroide.getJSONObject(0).getString("latitud")));

                String num = jsonArray.getJSONObject(i).getString("precio");
                double finalPrecio = Double.parseDouble(num);
                DecimalFormat df = new DecimalFormat("#.00");
                mMap.addMarker(new MarkerOptions().position(destination).title(jsonArray.getJSONObject(i).getString("nombre"))
                        //.snippet("Precio: "+"Cargando..."))

                        .snippet("Precio: $"+  df.format(finalPrecio)))


                        //"$"+jsonArray.getJSONObject(i).getString("precio")+"MXN"))
                        .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parken));
               */

            }




        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Método para dibujar los espacios Parken de una zona parken
    private void dibujarEspaciosParken(String espaciosparken){

        try{

            JSONArray jsonArray = new JSONArray(espaciosparken);

            for(int i = 0; i < jsonArray.length(); i++){


                JSONArray jsonArrayCentroEP = new JSONArray(jsonArray.getJSONObject(i).getString("coordenada"));
                LatLng destination = new LatLng(Double.parseDouble(jsonArrayCentroEP.getJSONObject(0).getString("longitud")), Double.parseDouble(jsonArrayCentroEP.getJSONObject(0).getString("latitud")));

                /*

               mMap.addMarker(new MarkerOptions()
                       .position(destination)
                       .title("Id").snippet(jsonArray.getJSONObject(i).getString("idespacioparken")))
                            .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parken));

               */

               Marker marker = mMap.addMarker(new MarkerOptions()
                       .position(destination)
                       .title("Id").snippet(
                               jsonArray.getJSONObject(i).getString("idespacioparken")));
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parken));

                hashMapMarker.put(jsonArray.getJSONObject(i).getString("idespacioparken"), marker);


            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void obtenerDibujoZonaParken(String zona){

        final String TAG = "ObtenerDibujoZonaParken";

        HashMap<String, String> parametros = new HashMap();
        parametros.put("zonaParken", zona);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_GETTING_PARKEN_ZONE,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, response.toString());

                        try {
                            showProgress(false);

                            if(response.getInt("success") == 1){


                                dibujarZonaParken(response.getString("zonasparken"));

                            }else{

                                showError(2);

                            }

                            return;

                        } catch (JSONException e) {
                            showProgress(false);
                            e.printStackTrace();
                            showError(2);

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        showError(0);
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }

    public void obtenerMarkersEspaciosParken(String zona){

        final String TAG = "ObtenerEspaciosParken";

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idZona", zona);
        parametros.put("opc", "1");

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

                                obtenerEspaciosParken(jsonEspacios, response.getString("numeroespaciosparken"));
                                if(!origin.equals("ReportGeofence")){
                                    dibujarEspaciosParken(jsonEspacios);
                                }

                            }else{

                                showError(2);

                            }

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            showError(2);

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        showError(0);
                        Log.d(TAG, error.toString());
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }


    public void obtenerInformacionEspacioParken(String id, final int origin){

        final String TAG = "ObtenerInfoEspParken";

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idEspacioParken", id);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_GETTING_PARKEN_SPACES_STATUS,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, response.toString());

                        try {
                            showProgress(false);

                            if(response.getInt("success") == 1){

                                if(origin == 1){
                                    imprimirInformacion(response.toString());
                                }else {

                                    if(txtEstatusEspacioParken.getText().toString().equals(response.getString("estatusespacioparken"))){
                                        actualizarEspacioParken(response.toString());
                                    }else {
                                        showError(3);
                                        imprimirInformacion(response.toString());
                                    }

                                }



                            }else{

                                if(origin == 1){
                                    showError(2);
                                }else {
                                    showError(2);
                                }


                            }

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            showError(2);

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        showError(0);
                        Log.d(TAG, error.toString());
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }

    public void imprimirInformacion(String informacionEspacioParken){


        try {


            JSONObject jsonEspacioParken = new JSONObject(informacionEspacioParken);

            estatusep.setVisibility(View.VISIBLE);
            String status = jsonEspacioParken.getString("estatusespacioparken");
            if(status.equals("DISPONIBLE")){
                txtInfoEspacioParken.setVisibility(View.VISIBLE);
                txtInfoEspacioParken.setText("Estatus actual");
                txtInfoAutomovilista.setVisibility(View.GONE);
                txtInfoVehiculo.setVisibility(View.GONE);
                txtInfoSesion.setVisibility(View.GONE);


            }else{

                txtInfoEspacioParken.setVisibility(View.GONE);

                try {


                    String automovilista = jsonEspacioParken.getString("nombreautomovilista") + " " + jsonEspacioParken.getString("apellidoautomovilista");
                    String vehiculo = jsonEspacioParken.getString("modelovehiculo") + " - " + jsonEspacioParken.getString("placavehiculo");
                    String sesion = "Sesión " + jsonEspacioParken.getString("estatussesionparken") + "\nFinaliza: " + jsonEspacioParken.getString("fechafinalformatted");

                    txtInfoAutomovilista.setVisibility(View.VISIBLE);
                    txtInfoAutomovilista.setText(automovilista);
                    txtInfoVehiculo.setVisibility(View.VISIBLE);
                    txtInfoVehiculo.setText(vehiculo);
                    txtInfoSesion.setVisibility(View.VISIBLE);
                    txtInfoSesion.setText(sesion);
                }catch (JSONException e){
                    e.printStackTrace();

                        txtInfoAutomovilista.setVisibility(View.GONE);
                        txtInfoVehiculo.setVisibility(View.GONE);
                        txtInfoSesion.setVisibility(View.GONE);
                        txtInfoEspacioParken.setVisibility(View.VISIBLE);

                }

            }

            txtEstatusEspacioParken.setText(jsonEspacioParken.getString("estatusespacioparken"));

            if(status.equals("DISPONIBLE")) txtEstatusEspacioParken.setTextColor(Color.parseColor("#27ae60"));
            if(status.equals("OCUPADO")) txtEstatusEspacioParken.setTextColor(Color.parseColor("#F44336"));
            if(status.equals("REPORTADO")) txtEstatusEspacioParken.setTextColor(Color.BLUE);
            if(status.equals("SANCIONADO")) txtEstatusEspacioParken.setTextColor(Color.RED);

            newestatusep.setVisibility(View.VISIBLE);
            txtNewEstatusEspacioParken.setText("Selecciona");
            //txtNewEstatusEspacioParken.setTextColor(Color.parseColor("F44336"));
            asignarNewEstatusDisponibles(status);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void modificarSesionParken(String idSesion, String newEstatus) {

        final String TAG = "ActualizarEspacioParken";


        HashMap<String, String> parametros = new HashMap();
        parametros.put("idSesionParken", idSesion);
        parametros.put("Estatus", newEstatus);
        parametros.put("Fecha", "true");

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_MODIFY_SESSION_PARKEN,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, response.toString());

                        try {
                            showProgress(false);

                            if(response.getString("success").equals("1")){

                                dialogModificacionExitosa().show();



                            }else{

                                showError(2);
                            }

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            showError(2);

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        showError(0);
                        Log.d(TAG, error.toString());
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }

    public void modificarEspacioParken(String idEspacio, String newEstatus) {

        final String TAG = "ModificarEspacioParken";

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idEspacioParken", idEspacio);
        parametros.put("estatus", newEstatus);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_MODIFY_PARKEN_SPACE,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, response.toString());

                        try {
                            showProgress(false);

                            if(response.getInt("success") ==1){

                                dialogModificacionExitosa().show();


                            }else{

                                showError(2);
                            }

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            showError(2);

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        showError(0);
                        Log.d(TAG, error.toString());
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }


    public void cerrarReporte(String idreporte){

            final String TAG = "CerrarReporte";

            HashMap<String, String> parametros = new HashMap();
            parametros.put("idreporte", idreporte);

            JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    Jeison.URL_DRIVER_MODIFY_REPORT,
                    new JSONObject(parametros),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            try {

                                showProgress(false);
                                if(response.getInt("success") ==1){
                                    //Modificacion exitosa
                                    modificacionReporte(EXITO);
                                }else{
                                    modificacionReporte(FAIL);
                                }

                                return;

                            } catch (JSONException e) {
                                e.printStackTrace();
                                showProgress(false);
                                modificacionReporte(FAIL);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showProgress(false);
                            showError(0);
                            Log.d(TAG, error.toString());
                            modificacionReporte(FAIL);
                        }
                    });

            fRequestQueue.add(jsArrayRequest);

    }


    public void modificacionReporte(int estatus){
        if(estatus == EXITO){

            Intent reportSuccess = new Intent(ModifyParkenSpaceActivity.this, ParkenActivity.class);
            reportSuccess.putExtra("Activity", ParkenActivity.REPORT_FINISH);
            reportSuccess.putExtra("ActivityStatus", EXITO);
            startActivity(reportSuccess);
            finish();
        }

        if(estatus == FAIL){

            Intent reportSuccess = new Intent(ModifyParkenSpaceActivity.this, ParkenActivity.class);
            reportSuccess.putExtra("Activity", FAIL);
            startActivity(reportSuccess);
            finish();
        }
    }

    public void asignarNewEstatusDisponibles(String oldEstatus){


        switch (oldEstatus){

            case "DISPONIBLE":

                newestatus = new CharSequence[2];
                newestatus[0] = "SANCIONADO";
                newestatus[1] = "DISPONIBLE";

                break;

            case "OCUPADO":

                newestatus = new CharSequence[2];
                newestatus[0] = "DISPONIBLE";
                newestatus[1] = "SANCIONADO";

                break;
            case "REPORTADO":

                newestatus = new CharSequence[2];
                newestatus[0] = "DISPONIBLE";
                newestatus[1] = "SANCIONADO";


                break;

            case "RESERVADO":

                newestatus = new CharSequence[3];
                newestatus[0] = "OCUPADO";
                newestatus[1] = "SANCIONADO";
                newestatus[2] = "DISPONIBLE";

                break;

            case "SANCIONADO":

                newestatus = new CharSequence[1];
                newestatus[0] = "DISPONIBLE";

                break;

            case "SANCION PAGADA":

                newestatus = new CharSequence[1];
                newestatus[0] = "DISPONIBLE";

                break;

                default:

                    newestatus = new CharSequence[1];
                    newestatus[0] = "OCUPADO";

                    break;
        }

    }

    public void actualizarEspacioParken(String info){

        String idSesion;

        try {
            JSONObject jsonInfo = new JSONObject(info);
            idSesion = jsonInfo.getString("idsesionparken");
        } catch (JSONException e) {
            e.printStackTrace();
            idSesion = "0";
        }

            if(txtNewEstatusEspacioParken.getText().toString().equals("DISPONIBLE")){
                showProgress(true);

                if (txtEstatusEspacioParken.getText().toString().equals("SANCIONADO")) {
                    dialogConfirmarLiberacion(idSesion).show();
                }else{
                    if(idSesion.equals("0"))
                        modificarEspacioParken(txtEspacioParken.getText().toString(), txtNewEstatusEspacioParken.getText().toString());
                    else
                        modificarSesionParken(idSesion, estatusSesion("DISPONIBLE"));

                }
            }

            if(txtNewEstatusEspacioParken.getText().toString().equals("SANCIONADO") || txtNewEstatusEspacioParken.getText().toString().equals("OCUPADO")){

                dialogEmpty("Se creará una sanción").show();
                //Crear sancion
                Intent newReceipt = new Intent(this, CreateReceiptActivity.class);
                newReceipt.putExtra("Activity", "ModifyParkenSpaceActivity");
                newReceipt.putExtra("DataReceipt", info);
                newReceipt.putExtra("onReport", true);
                startActivity(newReceipt);
            }




    }

    private String estatusSesion(String estatusEspacio){
        switch (estatusEspacio){

            case "DISPONIBLE":
                return "FINALIZADA";

            case "REPORTADO":
                return "REPORTADA";

            case "SANCIONADO":
                return "SANCIONADA";

            case "RESERVADO":
                return "RESERVADO";

            case "OCUPADO":
                return "ACTIVA";

                default:
                    return "ERROR";

        }
    }

    public AlertDialog dialogEmpty(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Empty")
                .setMessage(msg)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        return builder.create();
    }

    public void requestPermissionLocation(){

        //contAuxPermission = contAuxPermission + 1;
        //Verificamos la versión de la API
        //Si es >23 pedimos acceso a la ubicación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Versiones con android 6.0 o superior
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                //if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                //      Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //dialogEmpty("Dame permiso").show();


                //} else {

                ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1);
                Log.d("CheckPermission", "Request");

                //}

            }else {

                Log.d("CheckPermission", "CargarMapa");
                loadMap();

            }

        } else {
            //Versiones anteriores a android 6.0
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != 0) {
                dialogPermissionRequired().show();
                finish();
                return;

            } else {

                Log.d("CheckPermission", "CargarMapa");
                loadMap();

            }
        }

    }

    public void showError(int error){

        String msg;

        switch (error){
            case 1:
                msg = "Error";

                //Esconder todoo
                espacioparken.setVisibility(View.GONE);
                estatusep.setVisibility(View.GONE);
                newestatusep.setVisibility(View.GONE);
                actualizar.setVisibility(View.GONE);

                map.setVisibility(View.GONE);

                //Mostrar el icono de error
                imgAlert.setVisibility(View.VISIBLE);
                txtAlert.setVisibility(View.VISIBLE);
                txtAlert.setText("Ocurrió un error.\nIntenta de nuevo.");

                break;
            case 2:

                msg = "Error";

                //Esconder todoo
                espacioparken.setVisibility(View.GONE);
                estatusep.setVisibility(View.GONE);
                newestatusep.setVisibility(View.GONE);
                actualizar.setVisibility(View.GONE);

                map.setVisibility(View.GONE);

                //Mostrar el icono de error
                imgAlert.setVisibility(View.VISIBLE);
                txtAlert.setVisibility(View.VISIBLE);
                txtAlert.setText("Ocurrió un error.\nIntenta de nuevo.");

                break;

            case 3:

                msg = "El estatus del espacio ha cambiado";

                break;
            case 0:

                //Esconder todoo
                espacioparken.setVisibility(View.GONE);
                estatusep.setVisibility(View.GONE);
                newestatusep.setVisibility(View.GONE);
                actualizar.setVisibility(View.GONE);

                map.setVisibility(View.GONE);

                //Mostrar el icono de error
                imgAlert.setVisibility(View.VISIBLE);
                txtAlert.setVisibility(View.VISIBLE);
                txtAlert.setText("Ocurrió un error.\nIntenta de nuevo.");

                msg = "Error de conexión con el servidor";

                break;

                default:
                    msg = "Error desconocido";

                    //Esconder todoo
                    espacioparken.setVisibility(View.GONE);
                    estatusep.setVisibility(View.GONE);
                    newestatusep.setVisibility(View.GONE);
                    actualizar.setVisibility(View.GONE);

                    map.setVisibility(View.GONE);

                    //Mostrar el icono de error
                    imgAlert.setVisibility(View.VISIBLE);
                    txtAlert.setVisibility(View.VISIBLE);
                    txtAlert.setText("Ocurrió un error.\nIntenta de nuevo.");

                    break;
        }

        //Snackbar
        Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content),
                msg, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        snackbar.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    Log.d("PermissionResult", "permission was granted, yay!");
                    //loadMap();


                } else {
                    // permission denied, boo!
                    //requestPermissionLocation();
                    if(dialogPermissionLocationRequired == null) {
                        dialogPermissionLocationRequired = dialogPermissionLocationRequired();
                        dialogPermissionLocationRequired.show();
                    }
                    //finish();
                    return;
                }

            }
            break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
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

                        if(!txtEspacioParken.getText().toString().equals(idEP)){
                            txtEspacioParken.setText(idEP);
                        }

                        Marker marker = hashMapMarker.get(String.valueOf(idEP));
                        marker.showInfoWindow();
                        //Centrar el marker
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 20));

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


    public AlertDialog dialogNewEstatusOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(newestatus,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        estatusNuevo = (String) newestatus[which];
                        txtNewEstatusEspacioParken.setText(estatusNuevo);


                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });

        return builder.create();
    }

    private AlertDialog dialogModificacionExitosa(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Modificación exitosa")
                .setMessage("Se ha cambiado el estatus del espacio Parken.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        if(origin.equals("ReportGeofence"))
                            cerrarReporte(idReporteParken);
                        else
                            finish();
                    }
                });

        return builder.create();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 0){
            finish();
        }
    }

    private AlertDialog dialogConfirmarLiberacion(final String idsesion){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("¿Modificar el estatus a DISPONIBLE?")
                .setMessage("El pago de la sanción no está registrada en el sistema.\n" +
                        "Registra el pago de la sanción o presiona OK únicamente si el espacio Parken está vacío. ")
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showProgress(false);
                    }
                })
                .setNeutralButton("Registrar pago", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent pagarsancion = new Intent(ModifyParkenSpaceActivity.this, PayReceiptActivity.class);
                        pagarsancion.putExtra("Activity", "ModifyParkenSpaceActivity");
                        pagarsancion.putExtra("IdEspacio", String.valueOf(idEP));
                        startActivityForResult(pagarsancion, 0);
                        showProgress(false);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(idsesion.equals("0"))
                            modificarEspacioParken(txtEspacioParken.getText().toString(), txtNewEstatusEspacioParken.getText().toString());
                        else
                            modificarSesionParken(idsesion, estatusSesion("DISPONIBLE"));

                        dialog.dismiss();
                    }
                });

        return builder.create();

    }

    private AlertDialog dialogPermissionLocationRequired() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Log.e("Conta", String.valueOf(contAuxPermission));
        contAuxPermission ++;

        builder.setTitle("Acceder a tu ubicación")
                .setMessage("Parken necesita acceder a tu ubicación para localizar zonas Parken.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        return;

                    }})
                .setNegativeButton("Omitir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                        return;
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //requestPermissionLocation();
                        dialogPermissionLocationRequired = null;
                        ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1);
                        return;
                    }

                });

        return builder.create();
    }

    private AlertDialog dialogPermissionRequired() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Acceder a tu ubicación")
                .setMessage("Ingresa a la configuración de aplicaciones para habilitar el acceso a la ubicación de tu dispositivo.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        return;

                    }})
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //requestPermissionLocation();
                        return;
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
    public void onBackPressed() {
        if(origin!= null && origin.equals("ParkenActivity"))
            super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra("Activity") != null) {

            switch (intent.getStringExtra("Activity")) {

                case REPORT_SUCCESFUL:

                    cerrarReporte(idReporteParken);

                    break;

                case REPORT_UNSUCCESSFUL:

                    modificacionReporte(FAIL);

                    break;

                default:
                    break;
            }
        }
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
            mModifyFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mModifyFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mModifyFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mModifyFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
