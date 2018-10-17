package com.parken.parkensuper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReporteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;

    private RecyclerView.LayoutManager layoutManager;
    public ReporteAdapter adapterReporte;
    private List<Reporte> reporte_list;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    public JsonObjectRequest jsArrayRequest;

    private View mProgressView;
    private View mReporteFormView;

    private ImageView imgInfo;
    private TextView txtViewMessageReporte;
    private TextView aux;

    private TextView txtError;

    private ShPref session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        mReporteFormView = findViewById(R.id.sancion_form);
        mProgressView = findViewById(R.id.sancion_progress);

        imgInfo = findViewById(R.id.imageViewMessageSanciones);
        txtViewMessageReporte = findViewById(R.id.textViewMessageSanciones);
        aux = findViewById(R.id.textViewAux);

        txtError = findViewById(R.id.textViewMessageSanciones);

        session = new ShPref(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        reporte_list  = new ArrayList<>();
        recyclerView.setHasFixedSize(true);

        try {
            showProgress(true);
            obtenerReportes();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //load_data_from_server(0);

        //gridLayoutManager = new GridLayoutManager(this,1);
        //recyclerView.setLayoutManager(gridLayoutManager);r
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVerticalFadingEdgeEnabled(true);

        //recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false, 3));

        adapterReporte = new ReporteAdapter(this, reporte_list, new ReporteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Reporte item) {

                Intent atenderReporte = new Intent(ReporteActivity.this, ParkenActivity.class);
                atenderReporte.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                atenderReporte.putExtra("Activity", ParkenActivity.ACTIVITY_REPORTE);
                atenderReporte.putExtra("jsonReporte", item.getJsonReporte());
                startActivity(atenderReporte);

            }
        });
        recyclerView.setAdapter(adapterReporte);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

               // if(gridLayoutManager.findLastCompletelyVisibleItemPosition() == sancion_list.size()-1){
                    //try {
                        //obtenerSanciones(sancion_list.get(sancion_list.size()-1).getIdSancion());
                    //} catch (JSONException e) {
                      //  e.printStackTrace();
                    //}
                    //obtenerSanciones(sancion_list.get(sancion_list.size()-1).getIdSancion());
                    //load_data_from_server(data_list.get(data_list.size()-1).getId());
               // }

            }
        });


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

                                Log.d("ObtenerSanciones", response.toString());
                                showProgress(false);

                                parseReportesJSON(response.getString("reportes"));

                            }else{
                                showProgress(false);
                                Log.d("ObtenerSanciones", response.toString());
                                //Mostrar dialog
                                imgInfo.setVisibility(View.VISIBLE);
                                txtViewMessageReporte.setVisibility(View.VISIBLE);
                                aux.setVisibility(View.VISIBLE);
                                imgInfo.setImageResource(R.drawable.ic_no_receipt);
                                txtViewMessageReporte.setText("¡Felicidades! \nNo tienes sanciones.");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            //Mostrar snackbar
                            //Mostrar snackbar
                            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Error", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                            snackbar.show();

                            imgInfo.setVisibility(View.VISIBLE);
                            imgInfo.setImageResource(R.drawable.ic_no_connection);
                            txtViewMessageReporte.setVisibility(View.VISIBLE);
                            aux.setVisibility(View.VISIBLE);
                            txtViewMessageReporte.setText("Error al cargar las sanciones.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("ObtenerSanciones", "Error Respuesta en JSON: " + error.getMessage());

                        //Mostrar snackbar
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Error de conexión.", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();

                        imgInfo.setVisibility(View.VISIBLE);
                        imgInfo.setImageResource(R.drawable.ic_no_connection);
                        txtViewMessageReporte.setVisibility(View.VISIBLE);
                        aux.setVisibility(View.VISIBLE);
                        txtViewMessageReporte.setText("Error al cargar las sanciones.");
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }

    private void parseReportesJSON(String reportes) throws JSONException {
        //En este metodo obtenemos de una consulta el json con la información de las sanciones
        //Lo parseamos y creamos el objeto sancion
        JSONArray jsonReportes = new JSONArray(reportes);
        Reporte report;
        Double latitud;
        Double longitud;

        for (int i = 0; i < jsonReportes.length(); i++) {

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
                    reportes,
                    getStaticMapUrl(String.valueOf(latitud), String.valueOf(longitud)));
                    //"https://maps.googleapis.com/maps/api/staticmap?center=19.453094,-99.147437&zoom=15&size=400x200&&markers=color:RED%7Clabel:P%7C19.453094,-99.147437&maptype=roadmap&key=AIzaSyAZNyEJvYRGwgeo0udMCeajMgeZXC1mAwg");

            reporte_list.add(report);

        }

        adapterReporte.notifyDataSetChanged();

    }



    private String getStaticMapUrl(String latPoint, String lngPoint) {

        String coordenadas =  latPoint +","+lngPoint;
        String center = "center=" + coordenadas;
        String zoom = "zoom=18";
        String size = "size=600x300";
        String markers = "markers=color:RED%7Clabel:R%7C" + coordenadas;
        String mapType = "maptype=roadmap";
        String key = "key="+getString(R.string.google_maps_directions_key);

        String parametros = center + "&" + zoom + "&"+ size +"&&"+ markers + "&" + mapType + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        //String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        String url = "https://maps.googleapis.com/maps/api/staticmap?"+parametros;
        Log.d("URL GoogleMapsStatic", url);

        return url;
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

            mReporteFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mReporteFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mReporteFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mReporteFormView.setVisibility(show ? View.VISIBLE : View.GONE);
            mReporteFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
