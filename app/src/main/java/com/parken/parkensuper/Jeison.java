package com.parken.parkensuper;

import com.android.volley.RequestQueue;

/**
 * Created by marcos on 21/03/18.
 */

public class Jeison {


    public static final String YOUR_API_KEY = "AIzaSyDkmiXSeUvTkbXgV7UYpwmhiysqkrjqcZ0";

    //public static final String IP = "192.168.1.66";
    public static final String IP = "192.168.200.42";
    //public static final String IP = "192.168.43.236";
    //public static final String IP = "192.168.15.33";
    //public static final String IP = "192.168.15.116";
    //public static final String IP = "192.168.1.64";
    //public static final String IP = "10.4.132.31";
    //public static final String IP = "100.87.199.16";
    //public static final String IP = "10.100.126.239";

    //public static final String IP = "3.16.52.71";
    //public static final String IP = "www.parkenapp.com";




    public static final String PORT = ":3001";
    public static final String URL_BASE = "http://" + IP + PORT;


    public static final String URL_SOCKET = URL_BASE;
    public static final String URL_LOGIN = URL_BASE + "/login";
    public static final String URL_DRIVER_SIGIN = URL_BASE + "/automovilista/sigIn";
    public static final String URL_DRIVER_DATA = URL_BASE + "/supervisor/data";
    public static final String URL_DRIVER_ADD_CAR = URL_BASE + "/automovilista/addCar";
    public static final String URL_DRIVER_GETTING_ID_CAR = URL_BASE + "/supervisor/obtenerIdVehiculo";
    public static final String URL_DRIVER_CAR_AVAILABLE = URL_BASE + "/automovilista/verificarEstatusVehiculo";
    public static final String URL_DRIVER_POINTS = URL_BASE + "/automovilista/obtenerPuntosParken";
    public static final String URL_DRIVER_FIND_PARKEN_ZONE = URL_BASE + "/automovilista/buscarZonaParken";
    public static final String URL_DRIVER_FIND_PARKEN_SPACE = URL_BASE + "/automovilista/buscarEspacioParken";
    public static final String URL_DRIVER_PARKEN_SPACE = URL_BASE + "/automovilista/mostrarEspaciosParken";
    public static final String URL_DRIVER_VERYFY_ID = URL_BASE +  "/supervisor/verificarDatos";
    public static final String URL_DRIVER_UPDATE = URL_BASE +  "/supervisor/actualizarDatos";
    public static final String URL_DRIVER_REPORTS = URL_BASE + "/supervisor/obtenerTodosReportes";
    public static final String URL_VERIFY_SUPER = URL_BASE + "/supervisor/verificarSupervisor";
    public static final String URL_DRIVER_PAYING_RECEIPT = URL_BASE + "/automovilista/pagarSancion";
    public static final String URL_DRIVER_GETTING_RECEIPTS_AVAILABLES = URL_BASE + "/supervisor/obtenerEspaciosParkenParaPagarSancion";
    public static final String URL_DRIVER_GETTING_PARKEN_SPACES_AVAILABLES = URL_BASE + "/supervisor/obtenerEspaciosParkenParaSesion";
    public static final String URL_DRIVER_GETTING_PARKEN_SPACES_STATUS = URL_BASE + "/supervisor/estatusEspacioParken";
    public static final String URL_DRIVER_GETTING_PARKEN_ZONE = URL_BASE +  "/obtenerZonasParken";
    public static final String URL_DRIVER_GETTING_ALL_PARKEN_SPACES = URL_BASE +  "/supervisor/obtenerTodosEspaciosParken";
    public static final String URL_DRIVER_MODIFY_PARKEN_SPACE = URL_BASE + "/supervisor/actualizarEspacioParken";
    public static final String URL_DRIVER_MODIFY_REPORT = URL_BASE + "/supervisor/modificarReporte";
    public static final String URL_DRIVER_CREATE_RECEIPT = URL_BASE + "/supervisor/crearSancion";
    public static final String URL_DRIVER_DELETE_CAR = URL_BASE +  "/automovilista/eliminarVehiculo";
    public static final String URL_DRIVER_EDIT_CAR = URL_BASE +  "/automovilista/actualizarVehiculo";
    public static final String URL_DRIVER_PARKEN_SPACE_BOOK = URL_BASE + "/automovilista/apartarEspacioParken";
    public static final String URL_DRIVER_UPDATE_TICKET = URL_BASE + "/automovilista/pagarSancion";
    public static final String URL_DRIVER_PARKEN_SESSION = URL_BASE + "/automovilista/obtenerSesionesParken";
    public static final String URL_DRIVER_ACIVATE_SESSION_PARKEN = URL_BASE + "/automovilista/activarSesionParken";
    public static final String URL_DRIVER_MODIFY_SESSION_PARKEN = URL_BASE + "/automovilista/modificarSesionParken";
    public static final String URL_DRIVER_CREATE_REPORT = URL_BASE + "/automovilista/crearReporte";
    public static final String URL_DRIVER_DELETE_SESSION = URL_BASE + "/automovilista/eliminarSesionParken";
    public static final String URL_DRIVER_GETTING_VIEW = URL_BASE + "/automovilista/obtenerVistaDelServer";
    public static final String URL_DRIVER_GETTING_VALUES= URL_BASE + "/automovilista/obtenerValoresDelServer";
    public static final String URL_DRIVER_REFRESHING_VIEW_PAY = URL_BASE + "/automovilista/establecerVistaPagando";
    public static final String URL_TOKEN = URL_BASE + "/actualizarToken";
    public static String URL_BASE_POST = "http://192.168.1.70:3000/login";
    public static final String URL_GET_DIRECTION = "https://maps.googleapis.com/maps/api/geocode/json?address=~ADDRESS~&key=" + YOUR_API_KEY;
    //public static final String URL_GET_DIRECTION = "https://maps.googleapis.com/maps/api/geocode/json?address=NEPTUNO&key=AIzaSyA15TKW11TMeC60Kmoq4cqgRYryvRsGDQI";
    public static final String URL_GET_LATLNG = "https://maps.googleapis.com/maps/api/geocode/json?latlng=~LNG,~LAT&key="+ YOUR_API_KEY;



    public static final String SOCKET_ON_CONNECTED = "disponibleAReportes";
}