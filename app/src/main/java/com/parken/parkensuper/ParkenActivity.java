package com.parken.parkensuper;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ParkenActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        ResultCallback<Status>,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    //Nombre de variables-----------------------------------------------------------------------------
    public static final String ACTION_NAVIGATE_ON_THE_WAY ="com.parken.parkenv03.NAVEGAR_EN_CAMINO";
    public static final String ACTION_CANCEL_ON_THE_WAY ="com.parken.parkenv03.CANCELAR_EN_CAMINO";
    private static final int NOTIFICATION_ID = 0;
    private static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static int NAVIGATE_REQUEST_CODE = 2;
    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
    public static final String VIEW_START = "PARKEN_START";
    public static final String VIEW_PARKEN = "PARKEN";
    private static final String VIEW_ON_REPORT= "ON_REPORT";
    public static final String VIEW_PARKEN_SPACE_BOOKED = "sesionParkenReservado";
    private static final String VIEW_PARKEN_SESSION_ACTIVE = "sesionParkenActiva";
    private static final String VIEW_PARKEN_REPORT = "sancionPendiente";
    private static final String VIEW_DIALOG_PARKEN = "mostrarDialogParken";
    private static final String VIEW_DRIVER_PAYING = "sesionParkenPagando";
    private static final float RADIUS_GEOFENCE_PARKEN_SPACE_BOOKED = 500f;
    private static final float RADIUS_GEOFENCE_PARKEN_SESSION = 500f;
    private static final float RADIUS_GEOFENCE_ON_THE_WAY = 100f;
    public static final String METHOD_PARKEN_SPACE_BOOKED = "GEOFENCE_IN";
    public static final String METHOD_PARKEN_SPACE_CHECK = "GEOFENCE_OUT";
    public static final int LOAD = 100;
    public static final int RELOAD = 150;
    private static final int REFRESH = 200;
    private static final int TIMEOUT = 4509;
    private static final int REPORT = 12309;
    private static final int CANCEL = 2395;
    private static final int FINISH = 7338;
    public static final int PARKEN_SPACE_BOOKED = 50;
    public static final int PARKEN_SPACE_FOUND = 60;
    private static final int NOTIFICATION_ID_ON_THE_WAY = 1;
    private static final int NOTIFICATION_ID_GEOFENCE_ON_THE_WAY = 98;
    private static final int NOTIFICATION_NEW_EP_FOUND = 4;
    private static final int NOTIFICATION_CONFIRM_END_SP = 34;

    private final int UPDATE_INTERVAL =  1000;
    private final int FASTEST_INTERVAL = 900;

    public static final String ACTIVITY_PARKEN = "ParkenActivity";
    public static final String PARKEN_ONNEWINTENT = "onNewIntent";
    public static final String PARKEN_LOAD = "ParkenLoading";
    public static final String ACTIVITY_REPORTE = "ReoprteActivity";
    public static final String ACTIVITY_SESION_PARKEN = "SesionParkenActivity";
    public static final String INTENT_GEOFENCE_ON_REPORT= "GeofenceOnReport";
    public static final String REPORT_FINISH = "Reportefinalizado";
    public static final String ACTIVITY_PAY_RECEIPT = "SancionPagoActivity";
    public static final String METHOD_ON_LOCATION_CHANGED = "onLocationChanged";

    public static final String INTENT_GEOFENCE_PARKEN_BOOKED = "GeofenceParkenBooked";

    public static final String GEOFENCE_ON_REPORT = VIEW_ON_REPORT;
    public static final String GEOFENCE_PARKEN_BOOKED = VIEW_PARKEN_SPACE_BOOKED;
    public static final String GEOFENCE_PARKEN_SESSION_ACTIVE = VIEW_PARKEN_SESSION_ACTIVE;


    public static final String MESSAGE_NO_EP= "No hay EP disponibles";
    public static final String MESSAGE_FAILED= "Error al buscar";
    public static final String MESSAGE_EP_BOOKED= "EP Reservado";
    public static final String MESSAGE_AUTOMOVILISTA_BOOKED= "EP ya reservado";
    public static final String MESSAGE_PAY_FAILED = "Fallo el pago";
    public static final String MESSAGE_PAY_SUCCESS = "Pago exitoso";
    public static final String MESSAGE_PAY_CANCELED = "pagocancelado";

    public static final String NOTIFICATIONS = "notificationcenter";
    public static final int NOTIFICATION_INFO = 1;
    public static final int NOTIFICATION_CANCEL = 20;
    public static final int NOTIFICATION_NEW_REPORT = 100;
    public static final int NOTIFICATION_SUPER_DELETED = 200;
    public static final int NOTIFICATION_EP_BOOKED = 300;
    public static final int NOTIFICATION_EP_BOOKED_OUT = 350;
    public static final int NOTIFICATION_PARKEN_OUT = 400;
    public static final int NOTIFICATION_PAYING = 500;
    public static final int NOTIFICATION_PAYING_CANCEL = 530;
    public static final int NOTIFICATION_PAYING_OUT = 560;
    public static final int NOTIFICATION_SESSION_PARKEN = 600;
    public static final int NOTIFICATION_ALMOST_FINISH_PS = 700;
    public static final int NOTIFICATION_FINISH_PS = 800;
    public static final int NOTIFICATION_MOVEMENT = 900;
    public static final int NOTIFICATION_NEW_RECEIPT = 1000;
    public static final int NOTIFICATION__RECEIPT_PAYED = 1100;
    public static final int NOTIFICATION_CAR_FREE = 1200;

    public static final String TEST = "TEST";
    public static final String MOVEMENTS = "movimientos";
    public static final int NOTIFICATION_EP_BOOKED_CANCELED = 5968;


    public static final String RENEW = "renovar";
    public static final String FINISHED = "finalizar";

    private String STATE_ON_THE_WAY;
    private String STATE_PAY_PARKEN;
    private boolean STATE_SPACE_BOOKED;
    private boolean STATE_PARKEN_SESSION;
    private boolean STATE_PARKEN_REPORT;

    private static final int FINISH_BUTTON = 35;
    private static final int FINISH_TIME = 20;





    //----------------------------------------------------------------------------------------------
    //private NotificationReceiver mReceiver = new NotificationReceiver();

    private TextView txtEmailDriver;
    private TextView txtNameDriver;
    private TextView txtEstatusEParken;
    private TextView txtDireccionEParken;
    private TextView txtIDEParken;
    private TextView txtNotaEParken;
    private TextView txtReloj;

    private TextView txtEstatusReporte;
    private TextView textViewIDEP;
    private TextView txtNotaReportes;
    private TextView txtTipoReporte;
    private TextView txtAlert;
    private TextView txtAlertNoInternet;

    private LinearLayout alertLayout;


    private FloatingActionButton center;

    private Button espacioParken;
    private Button navegar;
    private Button atender;
    private Button renovar;
    private Button finalizar;
    private Button payReceipt;
    private Button logout;


    private ConstraintLayout profile;
    private ConstraintLayout alertReceipt;
    private ConstraintLayout botones;

    private View mProgressView;
    private View mParkenFormView;
    private View alertLay;
    public View infoLay;
    public View notitas;

    private double lat;
    private double lng;


    private boolean requestEspacioParken = false;
    private boolean alertOn = false;


    private String reportParken;

    private String zonaParkenJson;
    private String espacioParkenJson;
    private String sancionJson;
    private String idSesionParken;
    private String fechaFinal;
    private String montoFinal;
    private Calendar fechaPago;
    private String idVehiculo;
    private String modeloVehiculo;
    private String placaVehiculo;

    private String idPayPal = "project@parken.com";
    private String drawerTitle;

    private int mapaReady = 0;
    private int mapaListo = 0;
    private int contAuxPermission = 0;

    private GoogleMap mMap;
    private Marker m;
    private Polyline polyline;
    private VolleySingleton volley;
    private ShPref session;
    protected RequestQueue fRequestQueue;
    public JsonObjectRequest jsArrayRequest;
    private ActivityRecognitionClient mActivityRecognitionClient;
    ConnectivityManager connectivityManager;

    //TimerTask timerTask;
    //TimerCheckMovementTask timerCheckMovementTask;
    DownloadTask downloadTask;

    private LatLng destino;

    private Location lastLocation;

    private LatLngBounds DF;
    private LatLngBounds EP;

    //DialogAlerts ---------------------------------------------------------------------------------

    private AlertDialog _dialog;
    private AlertDialog dialogPermissionLocationRequired;
    private AlertDialog dialogAlertNoLocation;
    private AlertDialog dialogFailedVista;
    private AlertDialog dialogFailedValores;
    private AlertDialog dialogParken;
    private AlertDialog dialogEPTimeOut;
    private AlertDialog dialogConfirmEndSP;
    //----------------------------------------------------------------------------------------------

    public static ParkenActivity activityParken;
    //private ZonaParkenActivity actZonaParken;

    private GeofencingClient mGeofencingClient;
    private GoogleApiClient googleApiClient;

    private LocationRequest locationRequest;



    //Variables importantes que me permitiran saber la vista actual de la app-----------------------
    private String vista;
    private boolean notificationSPSent = false;
    private String notificacionTime;
    private int tiempoEnMinutos;
    private int minPausa = 0;
    private int segPausa = 59;
    private int minContador;
    private int segContador;
    public static double longitudDestino;
    public static double latitudDestino;
    public static double longitudDestinoFinal;
    public static double latitudDestinoFinal;
    public static double longitudEspacioParken;
    public static double latitudEspacioParken;
    private static String nombreDestino;
    public static String idEspacioParken;
    public static String addressEspacioParken;
    public static  String idZonaParken;

    public boolean reembolso = false;
    public boolean geofenceParkenExit = false;

    //----------------------------------------------------------------------------------------------
    //Socket

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Jeison.URL_SOCKET);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    //Variables para finalizar----------------------------------------------------------------------

    public boolean ENTERING = false;
    public boolean EXITING = false;
    public boolean DRIVING = false;
    public boolean CONFIRMATION = false;
    public boolean TIME = false;
    public boolean MOVE = false;


    boolean moveIn = false;
    boolean serverConnected = false;
    boolean obtenerVistaServer = false;
    int obtenerVistaServerResponse = 1;
    boolean vistaServer = false;
    boolean timersOn = false;
    int obtenerValoresDelServerResponse = 0;
    boolean problemConnection = false;

    Snackbar snackbarNoServer;

    //----------------------------------------------------------------------------------------------


    //Variables MODOS para finalizar sesión Parken--------------------------------------------------

    public static final int PROCESANDO = 208;
    public static final int FINALIZADA = 349;
    public static final int REPORTADA = 109;
    public static final int REEMBOLSO = 578;
    public static final int SANCIONADA = 869;

    public static final String StatusFinalizada = "FINALIZADA";
    public static final String StatusProcesando = "PROCESANDO";
    public static final String StatusReportada = "REPORTADA";
    public static final String StatusReembolso = "REEMBOLSO";
    public static final String StatusSancionada = "SANCIONADA";

    //----------------------------------------------------------------------------------------------
    //Puntos Parken
    public static double PuntosParkenReembolso;
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    //Timers

    public static int minutoEPBooked;
    public static int segundoEPBooked;

    public static int minutoDialogParken;
    public static int segundoDialogParken;

    public static int minutoTimerPago;
    public static int segundoTimerPago;

    public static int minutoTimerTolerance;
    public static int segundoTimerTolerance;

    public static int minutoTimerTolerancePlus;
    public static int segundoTimerTolerancePlus;

    public static int minutoCheckMove;
    public static int segundoCheckMove;

    //----------------------------------------------------------------------------------------------
    ProgressBar progressTimer;

    //Metodos
    //Peticiones al Servidor
    //Vistas en el mapa
    //Modificaciones al mapa
    //Metodos útilies
    //Metodos sobreescritos


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("AppEstatus", "onCreate");

        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_parken);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createNotificationChannel();

        loadMap();

        activityParken = this;
        session = new ShPref(activityParken);

        //****************
        // ALERTA
        session.setOnTheWay(false);
        //***********

        if(!session.loggedin()) {

          finish();
          startActivity(new Intent(getApplicationContext(), LoginActivity.class));
          return;

        }

        requestPermissionLocation();

        mGeofencingClient = LocationServices.getGeofencingClient(this);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        /*



        //Initialize and register the notification receiver
        IntentFilter intentFilter = new IntentFilter();

        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        intentFilter.addAction(ACTION_NAVIGATE_ON_THE_WAY);
        intentFilter.addAction(ACTION_CANCEL_ON_THE_WAY);
        registerReceiver(mReceiver, intentFilter);


        LayoutInflater inflater = activityParken.getLayoutInflater();
        View v = inflater.inflate(R.layout.alertdialog_parken, null);

*/

        navegar = findViewById(R.id.btnNavegar);
        atender = findViewById(R.id.btnAtender);

        renovar = findViewById(R.id.btnRenovar);
        finalizar = findViewById(R.id.btnFinalizar);

        payReceipt = findViewById(R.id.btnPayReceipt);

        center = findViewById(R.id.floatingCenterMap);

        botones = findViewById(R.id.constraintLayoutButtons);

        logout = findViewById(R.id.btnCerrarSesion);

        mParkenFormView = findViewById(R.id.map_form);
        mProgressView = findViewById(R.id.parken_progress);
        infoLay = findViewById(R.id.InfoLayout);
        notitas = findViewById(R.id.linearLayoutNotititas);
        //declareElements();

        alertLay = findViewById(R.id.AlertLayout);

        alertReceipt = findViewById(R.id.AlertReceiptLayout);

        txtEstatusReporte = findViewById(R.id.textViewEstatusR);
        txtDireccionEParken = findViewById(R.id.textViewDireccion);
        textViewIDEP = findViewById(R.id.textViewIDEP);
        txtNotaReportes = findViewById(R.id.textViewNotitas);
        txtTipoReporte = findViewById(R.id.textViewTipoReporte);

        txtReloj = findViewById(R.id.textViewRelojito);
        txtAlert = findViewById(R.id.textViewAlert);
        txtAlertNoInternet = findViewById(R.id.textViewAlertNoInternet);

        infoLay = findViewById(R.id.InfoLayout);





        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerheView = navigationView.getHeaderView(0);

        txtEmailDriver = headerheView.findViewById(R.id.textViewMailDriver);
        txtEmailDriver.setText(session.infoEmail());
        txtNameDriver = headerheView.findViewById(R.id.textViewNameDriver);
        profile = headerheView.findViewById(R.id.linearLayoutInfo);
        String nameHeader = session.infoNombre() + " " + session.infoApellido();
        txtNameDriver.setText(nameHeader);

        createGoogleApi();
        googleApiClient.connect();


        //Listener de todos los botones
        /*
        Botón BUSCAR
        Mostrar un fragment con la búsqueda de lugares
         */

        /*
        Botón NAVEGAR
        Al presionar el botón NAVEGAR se abrirá un navegador GPS
        con las coordenadas de la dirección destino
         */
        navegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //abrirGPSBrowser(session.getLatDestino(), session.getLngDestino(), session.getNombreDestino());
                abrirGPSBrowser(latitudDestino, longitudDestino, nombreDestino);

            }
        });


        /*
        Botón CANCELAR
        Al presionar el boton de CANCELAR se resetearán
        todas las variables
         */
        //Mostrar la confirmación
        //Debemos limpiar las variables
        //Limpiar el mapa
        //Mostrar el botón de busqueda
        atender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //dialogConfirmCancelOnReport().show();
                //Atender el reporte, ejecutar la acción  que hace la geocerca
                dialogConfirmReportResolve().show();

            }
        });


        /*
        Botón RENOVAR
        Al presionar el botón RENOVAR minetras una sesión Parken
        esta activa aparecerá la vista para comprar más tiempo
         */
        renovar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // renovarSesion();


            }
        });


        /*
        Botón FINALIZAR
        Al presionar el botón de FINALIZAR
        se cerrará la sesión Parken que se encuentra activa
         */
        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Finalizar", "PressButton");
                //dialogFinishParken().show();
            }
        });


        /*
        Botón PAGAR SANCION
        Al presionar el botón de PAGAR SANCION
        Se mostrará el formulario para pagar la sanción nueva
         */
        payReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pagarSancion(sancionJson);

            }
        });


        /*
        Botón PERFIL
        Al presionar la información del perfil del automovilista
        se iniciará el activity para ver/modificar el perfil
         */
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkenActivity.this, InformationActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                dialogConfirmLogOut().show();
            }
        });

        center.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {

                centrarMapa();

            }
        });




    }


    /**
     * Maps
     */
    public void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(dialogPermissionLocationRequired == null && contAuxPermission != 1) {
                dialogPermissionLocationRequired = dialogPermissionLocationRequired();
                dialogPermissionLocationRequired.show();
            }
            return;
        }
        mMap = googleMap;
        float zoom = 10f;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(19.428970, -99.133464), zoom);
        mMap.animateCamera(cameraUpdate);
        //mMap.moveCamera(cameraUpdate);

        Log.d("OnMapParkenReady", "Recarga");


        readyMap();
        mapaReady = 1;
        mapaListo = 1;


        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                if(lastLocation==null) {
                    //vista = verificarStatus();
                    //session.setVista(vista);
                    mapaListo = 0;
                }else{
                    //vista = verificarStatus();
                    //session.setVista(vista);
                    if(vista != null){
                        Log.d("OnMapLoad", "Cargo el mapa");
                        Log.d("VistaOnMapLoad", vista);
                        //selectView(vista, "onMapLoad", null);

                    }

                }
                Log.d("LoadMapa", "Cargo el mapa");

                //dialogParken().show();


            }
        });

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                center.setVisibility(View.VISIBLE);
            }
        });

    }

    public void readyMap(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(dialogPermissionLocationRequired == null && contAuxPermission != 1) {
                dialogPermissionLocationRequired = dialogPermissionLocationRequired();
                dialogPermissionLocationRequired.show();
            }
            return;
        }

        if(mMap != null) {
            Log.d("readyMap", "Load");
            mMap.setMyLocationEnabled(true);
            mMap.clear();
            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setMyLocationButtonEnabled(false);
            uiSettings.setMapToolbarEnabled(false);
            uiSettings.setZoomControlsEnabled(false);
            uiSettings.setCompassEnabled(false);
            uiSettings.setIndoorLevelPickerEnabled(false);
            uiSettings.setTiltGesturesEnabled(false);

            if(lastLocation != null){
                Log.d("readyMap", "LastLocation");
                float zoom = 15f;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()),
                        zoom);
                mMap.animateCamera(cameraUpdate);
            }
        }
    }

    public void centerMap(LatLngBounds bound){
        if(mMap != null){
            //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, 0));
            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngBounds(bound, 15);
            //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, 0));
            mMap.animateCamera(miUbicacion);

            /*
            float zoom = 15f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()),
                    zoom);
            mMap.animateCamera(cameraUpdate);
            */
        }
    }

    public void newMap(Double latitud, Double longitud, Boolean marker){
        mMap.clear();
        // Add a marker in Sydney and move the camera
        LatLng destination = new LatLng(latitud, longitud);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(destination, 16);
        if(marker) {
            mMap.addMarker(new MarkerOptions().position(destination).title("Latitud: " + latitud + " Longitud: " + longitud));
        }else{

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 16));
        mMap.animateCamera(miUbicacion);
    }

    public void centrarMapa(){
        if(lastLocation !=null){
            CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()),15);
            mMap.animateCamera(miUbicacion);
            center.setVisibility(View.GONE);
        }else{

            if(dialogAlertNoLocation == null) {
                dialogAlertNoLocation = dialogAlertNoLocation();
                dialogAlertNoLocation.show();
            }
        }
    }

    /**
     * Bounds
     */
    public LatLngBounds obtenerBoundPrincipal(){

        int points = 2;
        double coordinatesNorthLat[] = new double[points];
        double coordinatesNorthLng[] = new double[points];
        double coordinatesSouthLat[] = new double[points];
        double coordinatesSouthLng[] = new double[points];

        if(lastLocation != null){

            LatLng destination = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

            double radius = 200;

            LatLng targetNorthEast = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 45);
            LatLng targetSouthWest = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 225);

            coordinatesNorthLat[0] = targetNorthEast.latitude;
            coordinatesNorthLng[0] = targetNorthEast.longitude;
            coordinatesSouthLat[0] = targetSouthWest.latitude;
            coordinatesSouthLng[0] = targetSouthWest.longitude;

            //destination = new LatLng(session.getLatDestino(), session.getLngDestino());
            //Log.d("obtenerBoundPrincipal", String.valueOf(latitudDestino) +" - "+ String.valueOf(longitudDestino));
            destination = new LatLng(latitudDestino, longitudDestino);
            //destination = destino;

            radius = 200;

            targetNorthEast = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 45);
            targetSouthWest = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 225);

            coordinatesNorthLat[1] = targetNorthEast.latitude;
            coordinatesNorthLng[1] = targetNorthEast.longitude;
            coordinatesSouthLat[1] = targetSouthWest.latitude;
            coordinatesSouthLng[1] = targetSouthWest.longitude;

            return obtenerBoundary(coordinatesNorthLat, coordinatesNorthLng, coordinatesSouthLat, coordinatesSouthLng);

        }else{

            return null;
        }
    }

    public LatLngBounds obtenerBoundary(double[] northLat, double[] northLng, double [] southLat, double[] southLng){

        LatLng min = new LatLng(getMin(southLat),getMin(southLng));
        LatLng max = new LatLng(getMax(northLat),getMax(northLng));

        //Log.d("CoordenadaMin",getMin(southLat)+", "+getMin(southLng));
        //Log.d("CoordenadaMax",getMax(northLat)+", "+getMax(northLng));

        return new LatLngBounds(min, max);
    }



    public void onParken(int time){

        vista = VIEW_PARKEN;
        //onParken:
        //No hay reportes pendientes, el supervisor se encuentra esperando reportes



        if(time == LOAD || time == RELOAD) {
            //Asignar el titulo del toolbar a Parken
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Parken");

            //Quitamos el banner
            infoLay.setVisibility(View.GONE);
            notitas.setVisibility(View.GONE);

        }

        //Centramos el mapa
        centrarMapa();

        //Enviamos la ubicacion mediante el socket al servidor

        //Siempre, verificamos que tengamos conexion a internet
        //Verificamos la conexión a internet
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            alertLay.setVisibility(View.GONE);

            //Si aun no nos conectamos con el server
            if (!serverConnected) {
                //intemos otra vez la conexión con el servidor
                //try {
                  //  verificarSupervisor();
                //} catch (JSONException e) {
                  //  e.printStackTrace();
                //}
                //Si se logra conectar al server, ya no entra aqui
            }else{

                if(time == LOAD || time == RELOAD){

                    if(snackbarNoServer != null){
                        snackbarNoServer.dismiss();
                        snackbarNoServer = null;
                    }

                    Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Conexión con el servidor establecida", Snackbar.LENGTH_SHORT);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    snackbar.show();

                    //Conectar socket
                    connectSocket();
                    mSocket.on(Jeison.SOCKET_ON_CONNECTED, location);
                    navegar.setVisibility(View.GONE);
                    atender.setVisibility(View.GONE);
                }

                if(time == REFRESH){

                    double latitudSuper = lastLocation.getLatitude();
                    double longitudSuper = lastLocation.getLongitude();

                    HashMap<String, String> jsonLocation = new HashMap();
                    jsonLocation.put("idSupervisor", session.infoId());
                    jsonLocation.put("idZonaParken", session.getZonaSupervisor());
                    jsonLocation.put("lat", String.valueOf(latitudSuper) );
                    jsonLocation.put("lng", String.valueOf(longitudSuper));
                    mSocket.emit(Jeison.SOCKET_ON_CONNECTED, new JSONObject(jsonLocation));
                }

                if(!mSocket.connected()){
                    //Este es un caso especial, lo que vamos a hacer, es mostrar un bar que diga connectando
                    //y si esta abierto, no
                    //showBarServerNotConnectedConnecting();
                }else {
                    if(snackbarNoServer != null){
                        snackbarNoServer.dismiss();
                        snackbarNoServer = null;
                        Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Conexión con el servidor establecida", Snackbar.LENGTH_SHORT);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                        snackbar.show();
                    }
                }
            }
        }else{
            alertLay.setVisibility(View.VISIBLE);
            txtAlertNoInternet.setText("No hay conexión a Internet");
        }

    }




    public void onReport(int time, String reporte) throws JSONException {

        //Partiendo del hecho de que hay reporte o llegaun reporte,
        // se coloca automaticamente la pantala onreport, en lugar de la de reportes
        //ya tengo todoo un desmadre en mi codigo
        //Que va a pasar aqui????
        //Vamos a


        vista = VIEW_ON_REPORT;

        if(time == LOAD){
            Log.d("OnReport", "LOAD");
        }
        if(time == REFRESH){
            Log.d("OnReport", "REFRESH");
        }
        if (time == RELOAD){
            Log.d("OnReport", "RELOAD");
        }

        if(time == LOAD || time == RELOAD) {

            //Si el socket esta conectado, lo desconectamos
            if(mSocket.connected()){
                //Desconectamos el socket
                mSocket.off(Jeison.SOCKET_ON_CONNECTED, location);
                mSocket.disconnect();
            }

            //Obtenemos el JSON
            //reportParken = reporte.substring(1, reporte.length() -1);
//            Log.e("OnReport", reportParken);
            reportParken = reporte;
            JSONObject jsonReporte = new JSONObject(reporte);

            //Limpiamos el mapa de cualquier leyenda o marker
            mMap.clear();

            //Asignar el titulo del toolbar a En camino
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Reporte asignado");

            //Mostramos el banner
            infoLay.setVisibility(View.VISIBLE);
            notitas.setVisibility(View.VISIBLE);

            txtEstatusReporte.setText(jsonReporte.getString("estatusreporte"));
            txtDireccionEParken.setText(jsonReporte.getString("direccion"));
            textViewIDEP.setText(jsonReporte.getString("idespacioparken"));
            txtNotaReportes.setText("Dirígite al espacio Parken para resolver el reporte.");
            txtTipoReporte.setText(jsonReporte.getString("tiporeporte"));


            //Mantenemos la pantalla siempre encendida, durante el trayecto
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //Mostrar los botones disponibles para "En camino"
            botones.setVisibility(View.VISIBLE);
            navegar.setVisibility(View.VISIBLE);
            atender.setVisibility(View.VISIBLE);

            //Establecer las coordenadas
            //JSONArray jsonReporteCoo = new JSONArray(jsonReporte.getString("coordenada"));
            //Los invertí, no se por que
            //double longitud = jsonReporteCoo.getJSONObject(0).getDouble("latitud");
            //double latitud = jsonReporteCoo.getJSONObject(0).getDouble("longitud");
            double longitud = Double.valueOf(jsonReporte.getString("latitud"));
            double latitud = Double.valueOf(jsonReporte.getString("longitud"));
            latitudDestino = latitud;
            longitudDestino = longitud;
            //Asignamos las coordenadas del destino
            destino = new LatLng(latitudDestino, longitudDestino);

            nombreDestino = jsonReporte.getString("idespacioparken");
            //Mostramos el marker con el punto del destino
            if (nombreDestino != null) Log.d("Nombre destino", nombreDestino);
            mMap.addMarker(new MarkerOptions()
                    .position(destino)
                    .title("Espacio Parken:")
                    .snippet(nombreDestino)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.finish)));

            //Creamos la GEOCERCA (Destino)
            try {

                startGeofence(GEOFENCE_ON_REPORT);

            } catch (JSONException e) {
                e.printStackTrace();
                dialogErrorGeofence().show();
            }

        }
            //Obtener el bound central
            if(obtenerBoundPrincipal() != null){
                EP = obtenerBoundPrincipal();
                //Centrar el mapa
                centerMap(EP);
            }

            LatLng origin = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            alertLay.setVisibility(View.GONE);

            //Si aun no nos conectamos con el server
            if (!serverConnected) {
                //intemos otra vez la conexión con el servidor
                try {
                    verificarSupervisor();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Si se logra conectar al server, ya no entra aqui
            }else{

                if(time == LOAD || time == RELOAD){

                    if(snackbarNoServer != null){
                        snackbarNoServer.dismiss();
                        snackbarNoServer = null;
                    }

                    Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Conexión con el servidor establecida", Snackbar.LENGTH_SHORT);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    snackbar.show();

                }



                if(!mSocket.connected()){
                    //Este es un caso especial, lo que vamos a hacer, es mostrar un bar que diga connectando
                    //y si esta abierto, no
                    //showBarServerNotConnectedConnecting();
                }else {
                    if(snackbarNoServer != null){
                        snackbarNoServer.dismiss();
                        snackbarNoServer = null;
                        Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Conexión con el servidor establecida", Snackbar.LENGTH_SHORT);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
                        snackbar.show();
                    }
                }
            }
        }else{
            alertLay.setVisibility(View.VISIBLE);
            txtAlertNoInternet.setText("No hay conexión a Internet");
        }


    }


    public void cerrarSesion(){

        //Método para cerrar todas las conexiones al cerrar sesión
        session.setLoggedin(false);
        //getSharedPreferences("parken", Context.MODE_PRIVATE).edit().clear().commit();
        session.clearAll();
        //unregisterReceiver(mReceiver);
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    public void cancelarOnReport(){

        clearGeofence(VIEW_ON_REPORT);
        latitudDestino = 0.0;
        longitudDestino = 0.0;

        downloadTask.cancel(true);

        mMap.clear();

        //Al abrir modificaractivity
        //mantener fijo el espacioparken
        //es decir cancelar el onmarkerclikc
        //y el constraint espacioparken
        //Y no permitir que se salga del modify


    }

    public void showBarServerNotConnected(){
        if(snackbarNoServer == null)
            snackbarNoServer = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Error de conexión con el servidor", Snackbar.LENGTH_INDEFINITE);

        if(!snackbarNoServer.isShown()){
            View sbView = snackbarNoServer.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbarNoServer.show();
        }
    }

    public void showBarServerNotConnectedConnecting(){
        if(snackbarNoServer == null)
            snackbarNoServer = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Conectando...", Snackbar.LENGTH_INDEFINITE);

        if(!snackbarNoServer.isShown()){
            View sbView = snackbarNoServer.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbarNoServer.show();
        }
    }



    public void abrirGPSBrowser(Double latitud, Double longitud, String label){

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California"));
        List<ResolveInfo> activities = getPackageManager().queryIntentActivities(mapIntent, PackageManager.GET_META_DATA);

        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        if (!isIntentSafe) {
            Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "No tienes apps instaladas para navegar.", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();

        }else{

            ArrayList<ResolveInfo> listaApps = new ArrayList<ResolveInfo>();
            Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
            Intent mapIntent2 = new Intent(Intent.ACTION_VIEW, location);
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities2 = packageManager.queryIntentActivities(mapIntent, PackageManager.GET_META_DATA);
            String app = "NONE";

            for(ResolveInfo info: activities2){
                final String nameApp = info.loadLabel(packageManager).toString();

                if(nameApp.equals("Maps")){
                    app = "Maps";
                    break;
                }
                if(nameApp.equals("Waze")){
                    app = "Waze";
                    break;
                }
            }

            Uri intentUri=null;

            //dialogEmpty(session.getGPS()).show();

            try {

                switch (app){
                    case "Waze":
                        intentUri = Uri.parse(String.format("waze://?ll=%f,%f&navigate=yes", latitud, longitud));
                        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
                        startActivity(intent);
                        break;

                    case "Maps":

                        intentUri = Uri.parse(String.format("google.navigation:q=%f,%f&mode=d", latitud, longitud));
                        mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);


                        break;

                    default:

                        abrirIntentGPS(latitud, longitud, label);

                        break;

                }

            } catch (Exception e) {
                e.printStackTrace();
                //abrirIntentGPS(latitud, longitud, label);
                dialogAlertNoGPS().show();

            }
        }
    }

    public void abrirIntentGPS(Double latitud, Double longitud, String label){

        Uri intentUri = null;
        String geoLabel = "geo:<lat>,<lon>?q=<lat>,<lon>(label)";
        String zoom = "16";
        //Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
        intentUri = Uri.parse(geoLabel.replace("lat",String.valueOf(latitud)).replace("lon", String.valueOf(longitud)).replace("label", label));
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);

    }

    public void switchNotifications(int status, int actions, String data){

        switch (status){

            case NOTIFICATION_NEW_REPORT:

                    Log.e("Notification", "NewReporte");
                Log.e("Action", String.valueOf(actions));
                    switch (actions){


                        case 1:

                            Log.e("Action", String.valueOf(actions));
                            //startActivity(new Intent(ParkenActivity.this, ReporteActivity.class));
                            //Al recibir un reporte, se abre el onReport
                            try {
                                onReport(LOAD, data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 2:

                            //cancelarEnCamino();

                            break;
                        case 3:
                            break;

                        default:
                            break;

                    }



                break;

            case NOTIFICATION_SUPER_DELETED:

                Log.e("Notification", "SuperDeleted");
                cerrarSesion();
                break;



            case NOTIFICATION_EP_BOOKED:

                if(vista == VIEW_PARKEN_SPACE_BOOKED){

                    switch (actions){

                        case 1:

                            //abrirGPSBrowser(latitudDestino, longitudDestino, "");

                            break;
                        case 2:

                            //cancelarEnCamino();

                            break;
                        case 3:
                            break;

                        default:
                            break;

                    }
                }


                break;


            case NOTIFICATION_EP_BOOKED_CANCELED:

                if(vista != null){
                    if(vista.equals(VIEW_PARKEN_SPACE_BOOKED)){
                        //espacioParkenReservadoFinalizado();
                    }

                }else{ //Si la vista es NULL, entonces no hay nada
                    //Unicamente informamos que el tiempo se terminó
                    //Ya que la sesió la eliminó el servidor

                    //dialogEPTimeOut = dialogEPTimeOut();
                    //dialogEPTimeOut.show();

                    vista = VIEW_PARKEN;
                    //selectView(vista, VIEW_START, null);

                }


                break;

            case NOTIFICATION_ALMOST_FINISH_PS:

                if(vista == VIEW_PARKEN_SESSION_ACTIVE){

                    switch (actions){
                        case 1:

                            //renovarSesion();

                            break;
                        case 2:

                            //dialogFinishParken().show();

                            break;
                        case 3:
                            break;

                        default:
                            break;

                    }
                }


                break;

            case NOTIFICATION_FINISH_PS:

                switch (actions){
                    case 1:

                        //finalizarDirectSesionParken();

                        break;

                    default:
                        break;

                }

                break;

            case NOTIFICATION_MOVEMENT:

                switch (actions){
                    case 1:

                        //confirmarFinSesionParken(Integer.valueOf(data));

                        break;

                    case 2:

                        //omitirFinSesionParken(Integer.valueOf(data));

                        break;

                    default:
                        break;

                }

                break;
            case NOTIFICATION_NEW_RECEIPT:

                switch (actions){
                    case 1:

                        //pagarSancion(data);

                        break;

                    default:
                        break;

                }

                break;

            case NOTIFICATION_INFO:
               // startActivity(new Intent(ParkenActivity.this, SancionActivity.class));
                break;


            default:
                break;

        }

    }

    private void connectSocket() {

        mSocket.connect();
        //mSocket.emit("chat message", "Hola");
        //mSocket.

    }



    public AlertDialog dialogErrorGeofence() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        builder.setTitle("Error")
                .setMessage("Ocurrió un error al crear la geocerca")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        return builder.create();
    }

    public AlertDialog dialogReporteExitoso() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        builder.setTitle("Reporte correcto")
                .setMessage("Se cerró el reporte correctamente.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        return builder.create();
    }

    public AlertDialog dialogReporteFail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        builder.setTitle("Error")
                .setMessage("No es posible completar el reporte.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        return builder.create();
    }

    public AlertDialog dialogAlertNoGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Navegador GPS")
                .setMessage("No tienes navegadores GPS instalados en tu dispositivo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        return builder.create();

    }

    public AlertDialog dialogAlertNoLocation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ubicación desabilitada")
                .setMessage("Activa la localización del dispositivo para utilizar la aplicación.")
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
                dialogAlertNoLocation = null;
                finish();
            }
        });
        return builder.create();

    }

    public AlertDialog dialogConfirmLogOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);
        builder.setTitle("Cerrar sesión")
                .setMessage("¿Deseas cerrar la sesión?")
                .setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        return;
                    }
                })
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                cerrarSesion();
                            }
                        });

        return builder.create();
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Method for getting the maximum value
    public static double getMax(double[] inputArray){
        double maxValue = inputArray[0];
        for(int i=1;i < inputArray.length;i++){
            if(inputArray[i] > maxValue){
                maxValue = inputArray[i];
            }
        }
        return maxValue;
    }

    // Method for getting the minimum value
    public static double getMin(double[] inputArray){
        double minValue = inputArray[0];
        for(int i=1;i<inputArray.length;i++){
            if(inputArray[i] < minValue){
                minValue = inputArray[i];
            }
        }
        return minValue;
    }

    public void requestPermissionLocation(){

        contAuxPermission = contAuxPermission + 1;
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

                ActivityCompat.requestPermissions(activityParken, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1);
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





    private AlertDialog dialogPermissionLocationRequired() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

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
                        ActivityCompat.requestPermissions(activityParken, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1);
                        return;
                    }

                });

        return builder.create();
    }

    private AlertDialog cerrarSesionXDuplicado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        builder.setTitle("Cerrar sesión")
                .setMessage("Has iniciado sesión en otro dispositivo.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }})
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        cerrarSesion();
                    }

                });

        return builder.create();
    }

    private AlertDialog dialogPermissionRequired() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

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

    private AlertDialog dialogConfirmCancelOnReport() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        builder.setTitle("Cancelar")
                .setMessage("¿Estás seguro que no atenderás el reporte?")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelarOnReport();
                    }})
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }

                });

        return builder.create();
    }

    private AlertDialog dialogConfirmReportResolve() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        builder.setTitle("¿Resolver reporte?")
                .setMessage("Aún no te encuentras cerca del espacio Parken reportado.")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Resolver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Aqui es donde mandamos llamar para atender el reporte
                        atenderReporte();
                    }})
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });

        return builder.create();
    }


    private void drawRoute(LatLng origen, LatLng destino) {
        // Getting URL to the Google Directions API
        //String url = getDirectionsUrl(origin, destino);
        //Before clear the route
        //Log.d("OnTheWayURL", String.valueOf(session.getLatDestino())+" - "+String.valueOf(session.getLngDestino()));
        //String url = getDirectionsUrl(origin, new LatLng(session.getLatDestino(),session.getLngDestino()));
        //String url = getDirectionsUrl(origin, new LatLng(latitudDestino, longitudDestino));
        Log.d("DrawRoute", "Drawing");
        String url = getDirectionsUrl(origen, destino);
        downloadTask = new ParkenActivity.DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key="+getString(R.string.google_maps_directions_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;


        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }


    /**
     *Geocercas
     *
     */

    private void startGeofence(String geofence) throws JSONException {
        final String TAG = "startGeonfence";
        final String GEOFENCE_REQ_ID;
        final String GEOFENCE_DESTINATION_ID;
        final float GEOFENCE_RADIUS; // in meters

        /* Codigo ejemplo para crear una geocerca
        Geofence geofence = createGeofence( new LatLng(latitudDestino, longitudDestino), GEOFENCE_RADIUS, GEOFENCE_REQ_ID );
        GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
        addGeofence( geofenceRequest );
        */

        Log.d(TAG, "TRUE");

        //Creamos la lista
        List<Geofence> mGeofenceList = new ArrayList<Geofence>();

        //Dependiendo de "geofence" creamos la geocerca con su respectivo centro y radio
        switch (geofence){
            case VIEW_ON_REPORT:

                GEOFENCE_REQ_ID = "onreport";
                GEOFENCE_DESTINATION_ID = "destino";
                GEOFENCE_RADIUS = RADIUS_GEOFENCE_ON_THE_WAY; // in meters

                Log.d(TAG, "GeofenceCentro: " + String.valueOf(latitudDestino)+" - "+ String.valueOf(longitudDestino));
                Log.d(TAG, "DestinoGeofence: " + GEOFENCE_DESTINATION_ID);

                mGeofenceList.add(createGeofence(
                        new LatLng(latitudDestino, longitudDestino),
                        GEOFENCE_RADIUS,
                        GEOFENCE_DESTINATION_ID));

                break;
            default:
                break;
        }

        GeofencingRequest geofenceRequest = createGeofenceRequest(mGeofenceList);
        addGeofence( geofenceRequest, VIEW_ON_REPORT);

    }

    private Geofence createGeofence(LatLng latLng, float radius, String id) {

        final long GEO_DURATION = 60 * 60 * 1000;

        Log.d("CrearGeocerca", "createGeofence");
        return new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(500)
                .build();
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest( List<Geofence> geofences ) {
        //private GeofencingRequest createGeofenceRequest( Geofence geofences ) {
        Log.d("Crear request Geofence", "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofences( geofences )
                .build();
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request, String vista) {
        Log.d("Geofence", "addGeofence");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(dialogPermissionLocationRequired == null && contAuxPermission != 1) {
                dialogPermissionLocationRequired = dialogPermissionLocationRequired();
                dialogPermissionLocationRequired.show();
            }
            return;
        }
        if(vista.equals(VIEW_ON_REPORT)){
            mGeofencingClient.addGeofences(request, createGeofenceReportPendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void aVoid) {
                                                  //drawGeofence(new LatLng(latitudDestino, longitudDestino));
                                              }
                                          }
                    );

        }
    }


    private PendingIntent geoFenceReportPendingIntent;
    private final int GEOFENCE_REPORT_REQ_CODE = 1;
    private PendingIntent createGeofenceReportPendingIntent() {
        Log.d("Geofence", "createGeofenceSessionPendingIntent");
        if ( geoFenceReportPendingIntent != null )
            return geoFenceReportPendingIntent;

        Intent intent = new Intent( this, GeofenceParkenIntentService.class);
        return PendingIntent.getService(
                this, GEOFENCE_REPORT_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );

    }

    // Clear Geofence
    private void clearGeofence(String geofence) {

        final String TAG = "ClearGeofence";
        Log.d(TAG, "clearGeofence()");

        switch (geofence){

            case GEOFENCE_ON_REPORT:

                Log.d(TAG, "ClearOnTheWayGeofence");
                mGeofencingClient.removeGeofences(createGeofenceReportPendingIntent())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                  @Override
                                                  public void onSuccess(Void aVoid) {
                                                      //drawGeofence(new LatLng(latitudDestino, longitudDestino));
                                                  }
                                              }

                        );

                break;

            default:
                break;
        }

    }






    /**
     * Location
     */

    /**
     * LocationListener
     * Métodos que registra que los cambios
     * en el GPS y la uicación del dispositivo
     */
    @Override
    public void onLocationChanged(Location location) {

        //Aqui enviamos la ubicación del supervisor
        lastLocation = location;

        if(vista != null) {

            if(vista.equals(VIEW_PARKEN)){ //Si la vista es onParken, entonces refrescamos la vista onParken
                onParken(REFRESH);
            }

        }else{
            Log.d("LocationChangedVista", "NULL");
        }

    }

    // Start location Updates
    private void startLocationUpdates(){
        Log.d("StartLocation", "startLocationUpdates()");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(dialogPermissionLocationRequired == null && contAuxPermission != 1) {
                dialogPermissionLocationRequired = dialogPermissionLocationRequired();
                dialogPermissionLocationRequired.show();
            }
            return;
        }
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    // Get last known location
    private void getLastKnownLocation() {
        Log.d("GetLastKnownLocation", "getLastKnownLocation()");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(dialogPermissionLocationRequired == null && contAuxPermission != 1) {
                dialogPermissionLocationRequired = dialogPermissionLocationRequired();
                dialogPermissionLocationRequired.show();
            }
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //alertNoGPS().show();
            alertLay.setVisibility(View.VISIBLE);
            txtAlert.setText("Buscando la señal del GPS");
        }else{
            //alertLay.setVisibility(View.GONE);
        }

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if ( lastLocation != null ) {
            Log.d("GetLastKnownLocation", "LasKnown location. " +
                    "Long: " + lastLocation.getLongitude() +
                    " | Lat: " + lastLocation.getLatitude());
            //writeLastLocation();
            startLocationUpdates();
        } else {
            Log.w("GetLastKnownLocation", "No location retrieved yet");
            startLocationUpdates();
        }
    }

    private void obtenerReportes() throws JSONException {

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idSupervisor", session.infoId());

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_REPORTS,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            if(response.getInt("success") == 1){

                                Log.d("ObtenerReportes", response.toString());

                                serverConnected = true;

                                showProgress(false);
                                if(hayReportesPendientes(response.getString("reportes")) != 0){

                                    //Mostrar alerta en el mapa
                                    //Lanzar notificación
                                    Notificacion.lanzar(getApplicationContext(), NOTIFICATION_NEW_REPORT, "MAX", "");
                                    //Abrir ReporteActivity
                                    //startActivity(new Intent(ParkenActivity.this, ReporteActivity.class));
                                    onReport(RELOAD, obtenerReporteAsignado(response.getString("reportes")));

                                    if(mSocket.connected()){
                                        //Desconectamos el socket
                                        mSocket.off(Jeison.SOCKET_ON_CONNECTED, location);
                                        mSocket.disconnect();
                                    }

                                }else {
                                    onParken(LOAD);
                                }

                            }else{

                                showProgress(false);
                                if(response.getInt("success") == 3){
                                    serverConnected = true;
                                    onParken(LOAD);

                                }else {

                                    Log.d("ObtenerReportes", response.toString());
                                    showBarServerNotConnected();
                                    onParken(LOAD);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            showBarServerNotConnected();
                            onParken(LOAD);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("ObtenerReportes", "Error Respuesta en JSON: " + error.getMessage());
                        showBarServerNotConnected();
                        onParken(LOAD);
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }


    private void verificarSupervisor() throws JSONException {

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idSupervisor", session.infoId());

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_VERIFY_SUPER,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            if(response.getInt("success") == 1){

                                Log.d("VerificarSupervisor", response.toString());

                                if(response.getString("token").equals("") || response.getString("token").equals("null") || response.getString("token") == null){
                                    cerrarSesion();
                                }else{

                                    //Si esta veirficado y procedemos a obtener el token
                                    if(!response.getString("token").equals(session.getToken())){
                                        cerrarSesionXDuplicado().show();
                                    }else {
                                        obtenerReportes();
                                    }

                                }

                            }else{
                                Log.d("VerificarSupervisor", response.toString());
                                cerrarSesion();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showBarServerNotConnected();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VerificarSupervisor", "Error Respuesta en JSON: " + error.getMessage());
                        showBarServerNotConnected();

                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }




    private int hayReportesPendientes(String reportes) throws JSONException {
        //En este metodo obtenemos de una consulta el json con la información de los reportes
        //Lo parseamos y creamos el objeto sancion
        JSONArray jsonReportes = new JSONArray(reportes);

        int reportesPendiente = 0;

        for (int i = 0; i < jsonReportes.length(); i++) {

            if (jsonReportes.getJSONObject(i).getString("estatusreporte").equals("ASIGNADO")) {
                reportesPendiente++;
            }
        }

            /*
            JSONArray jsonSancionCentro = new JSONArray(jsonReportes.getJSONObject(i).getString("coordenada"));
            //Los invertí, no se por que
            longitud = jsonSancionCentro.getJSONObject(0).getDouble(    "latitud");
            latitud = jsonSancionCentro.getJSONObject(0).getDouble("longitud");

            report = new Reporte(jsonReportes.getJSONObject(i).getInt("idreporte"),
                    jsonReportes.getJSONObject(i).getString("tiemporeporte"),
                    jsonReportes.getJSONObject(i).getString("observacionreporte"),
                    jsonReportes.getJSONObject(i).getString("estatusreporte"),
                    jsonReportes.getJSONObject(i).getString("tiporeporte"),
                    jsonReportes.getJSONObject(i).getInt("idespacioparken"),
                    jsonReportes.getJSONObject(i).getString("estatusespacioparken"),
                    jsonReportes.getJSONObject(i).getInt("zona"),
                    latitud,
                    longitud,
                    jsonReportes.getJSONObject(i).getString("direccion"),
                    jsonReportes.getJSONObject(i).getInt("idautomovilista"),
                    jsonReportes.getJSONObject(i).getString("nombreautomovilista"),
                    jsonReportes.getJSONObject(i).getString("apellidoautomovilista"),
                    jsonReportes.getJSONObject(i).getString("emailautomovilista"),
                    jsonReportes.getJSONObject(i).getString("celularautomovilista"),
                    jsonReportes.getJSONObject(i).getString("token"),
                    jsonReportes.getJSONObject(i).getDouble("puntosparken"),
                    getStaticMapUrl(String.valueOf(latitud), String.valueOf(longitud)));
            //"https://maps.googleapis.com/maps/api/staticmap?center=19.453094,-99.147437&zoom=15&size=400x200&&markers=color:RED%7Clabel:P%7C19.453094,-99.147437&maptype=roadmap&key=AIzaSyAZNyEJvYRGwgeo0udMCeajMgeZXC1mAwg");

            reporte_list.add(report);

        }

        adapterReporte.notifyDataSetChanged();
        */
            return reportesPendiente;

    }


    private String obtenerReporteAsignado(String reporte) throws JSONException {

        JSONArray jsonReportes = new JSONArray(reporte);

        String reporteAsignado = "";

        for (int i = 0; i < jsonReportes.length(); i++) {

            if (jsonReportes.getJSONObject(i).getString("estatusreporte").equals("ASIGNADO")) {
                reporteAsignado = jsonReportes.getString(i);
                Log.e("Obtenerreportes", reporteAsignado);
            }
        }

        return reporteAsignado;

    }

    public void atenderReporte(){
        //Abrir ReporteActivity con el idEspacioparken fijo
        Intent reportActivity = new Intent(ParkenActivity.this, ModifyParkenSpaceActivity.class);
        reportActivity.putExtra("Activity", "ReportGeofence");
        reportActivity.putExtra("jsonReporte", reportParken);
        startActivity(reportActivity);

    }




    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Parken";
            String description = "Parken channel";
            int importanceMax = NotificationManager.IMPORTANCE_HIGH;
            int importanceHigh = NotificationManager.IMPORTANCE_HIGH;
            int importanceDef = NotificationManager.IMPORTANCE_DEFAULT;
            int importanceMin = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel("HIGH", name, importanceHigh);
            NotificationChannel channel1 = new NotificationChannel("MAX", name, importanceMax);
            NotificationChannel channel2 = new NotificationChannel("DEFAULT", name, importanceDef);
            NotificationChannel channel3 = new NotificationChannel("MIN", name, importanceMin);
            channel.setDescription(description);
            channel.enableVibration(false);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
            notificationManager.createNotificationChannel(channel3);
        }
    }


    /**
     * Navigation Bar
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.parken, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //dialogConfirmLogOut();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.nav_modify_space) {
            startActivity(new Intent(ParkenActivity.this, ModifyParkenSpaceActivity.class)
                    .putExtra("Activity", "ParkenActivity"));

        } else if (id == R.id.nav_session) {
            startActivity(new Intent(ParkenActivity.this,SesionParkenActivity.class));

        } else if (id == R.id.nav_sanction) {
        startActivity(new Intent(ParkenActivity.this, CreateReceiptActivity.class)
        .putExtra("Activity", "ParkenActivity"));

        } else if (id == R.id.nav_reports) {
            startActivity(new Intent(ParkenActivity.this, ReporteActivity.class));

        }else if (id == R.id.nav_pay_session) {
            startActivity(new Intent(ParkenActivity.this, PayReceiptActivity.class).putExtra("Activity", "ParkenActivity"));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    Log.d("PermissionResult", "permission was granted, yay!");
                    Log.d("PermissionResultLogIn", String.valueOf(session.loggedin()));
                    loadMap();


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


    // Create GoogleApiClient instance
    private void createGoogleApi() {
        Log.d("Google Api", "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks(this)
                    .addApi(ActivityRecognition.API)
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }

    /**
     * Google API Client ConnectionCallbacks
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Connected", "onConnected()");

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            Log.d("Connected", "Si hay conexión a Internet en este momento");
            try {
                verificarSupervisor();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // No hay conexión a Internet en este momento
            Log.d("Connected", "No hay conexión a Internet en este momento");
            onParken(LOAD);
            alertLay.setVisibility(View.VISIBLE);
            txtAlertNoInternet.setText("No hay conexión a Internet");
        }

        createNotificationChannel();
        getLastKnownLocation();
        //onParken(LOAD);

    }

    // GoogleApiClient.ConnectionCallbacks suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.w("ConnectionSuspended", "onConnectionSuspended()");
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w("ConnectionFailed", "onConnectionFailed()");
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.d("Tag", "onResult: " + status);
        if ( status.isSuccess() ) {
            //saveGeofence();
            //drawGeofence(new LatLng(session.getLatDestino(),session.getLngDestino()));
            //drawGeofence(new LatLng(latitudDestino, longitudDestino));
        } else {
            // inform about fail
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //Log.d(TAG, "onMapClick("+latLng +")");
        //markerForGeofence(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
        return false;
    }

    @Override
    protected void onDestroy() {

       if(googleApiClient != null){
        googleApiClient.disconnect();
       }

        //Cerrar sockets -------------------------------------------------
        mSocket.disconnect();
        //mSocket.off(Jeison.SOCKET_FIND_PARKEN_SPACE, parkenSpace);
        //mSocket.off("chat message", parkenSpace);
        //----------------------------------------------------------------
        super.onDestroy();
    }

    /**
     * Socket events
     */
    private Emitter.Listener location = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        JSONObject response = new JSONObject((String) args[0]) ;

                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    @Override
    protected void onNewIntent(Intent intent){

        //Recibe un nuevo Intent
        //Unicamente cuando recibe un intent
        //En esta parte se reciben todas las actualizaciones que vienen de un nuevo intent

        Log.e("OnNewIntent", "true");

        googleApiClient.connect();
        //Actualizamos los datos del driver
        String nameHeaderAux = session.infoNombre() + " " + session.infoApellido();
        txtNameDriver.setText(nameHeaderAux);
        txtEmailDriver.setText(session.infoEmail());

            if(intent.getStringExtra("Activity") != null) {

                switch (intent.getStringExtra("Activity")){

                    case ACTIVITY_REPORTE:

                        Log.d("Activity", ACTIVITY_REPORTE);

                        try {
                            onReport(LOAD, intent.getStringExtra("jsonReporte"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;

                    case INTENT_GEOFENCE_ON_REPORT:

                        atenderReporte();

                        break;

                    case REPORT_FINISH:

                        if(intent.getIntExtra("ActivityStatus", 0) == ModifyParkenSpaceActivity.EXITO){
                            dialogReporteExitoso().show();
                        }

                        if(intent.getIntExtra("ActivityStatus", 0) == ModifyParkenSpaceActivity.FAIL){
                            dialogReporteFail().show();
                        }
                        //Obtenemos otra vez los reportes, en caso de que exista otro.
                        try {
                            obtenerReportes();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        break;

                    case NOTIFICATIONS:

                        switchNotifications(intent.getIntExtra("ActivityStatus", 0),
                                intent.getIntExtra("Actions", -1),
                                intent.getStringExtra("data"));

                        break;


                    default:
                        vista = VIEW_PARKEN;
                        break;
                }
            }
        }

    /**
     * AsynkTask
     * Código con las tareas asincronas para mostrar la ruta en la app
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParkenActivity.ParserTask parserTask = new ParkenActivity.ParserTask();
            parserTask.execute(result);

        }

        @Override
        protected void onCancelled(){
            //ParkenActivity.ParserTask parserTask = new ParkenActivity.ParserTask();
            //parserTask.cancel(true);
            super.onCancelled();
        }

    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected void onPreExecute(){
            if(polyline!=null){
                //polyline.get
                //polylineAux = mMap.add
            }
            super.onPreExecute();
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            points = new ArrayList();
            lineOptions = new PolylineOptions();
            if (result == null) {
                cancel(true);
            }else {

                for (int i = 0; i < result.size(); i++) {


                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }
                    lineOptions.addAll(points);
                    lineOptions.width(12);
                    lineOptions.color(Color.RED);
                    lineOptions.geodesic(true);
                }

                // Drawing polyline in the Google Map for the i-th route
                if (points.size() != 0) {
                    if (polyline != null) {
                        polyline.remove();
                    }
                    polyline = mMap.addPolyline(lineOptions);
                }
            }
        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
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
                mParkenFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                mParkenFormView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mParkenFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
                mParkenFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }

}
