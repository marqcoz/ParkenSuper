package com.parken.parkensuper;

public class Reporte {

    private int idReporte;
    private String tiempoReporte;
    private String observacionReporte;
    private String estatusReporte;
    private String tipoReporte;
    private int idEspacioParken;
    private String estatusEspacioParken;
    private int idZonaParken;
    private double latitudEspacioParken;
    private double longitudEspacioParken;
    private String direccionEspacioParken;
    private int idAutomovilista;
    private String nombreAutomovilista;
    private String apellidoAutomovilista;
    private String correoAutomovilista;
    private String celAutomovilista;
    private String tokenAutomovilista;
    private double puntosParken;
    private String jsonReporte;
    private String imgMapLink;

    public Reporte( int idReporte,
                    String tiempoReporte,
                    String observacionReporte,
                    String estatusReporte,
                    String tipoReporte,
                    int idEspacioParken,
                    String estatusEspacioParken,
                    int idZonaParken,
                    double latitudEspacioParken,
                    double longitudEspacioParken,
                    String direccionEspacioParken,
                    int idAutomovilista,
                    String nombreAutomovilista,
                    String apellidoAutomovilista,
                    String correoAutomovilista,
                    String celAutomovilista,
                    String tokenAutomovilista,
                    double puntosParken,
                    String jsonReporte,
                    String imgMapLink) {

        this.idReporte = idReporte;
        this.tiempoReporte = tiempoReporte;
        this.observacionReporte = observacionReporte;
        this.estatusReporte = estatusReporte;
        this.tipoReporte = tipoReporte;
        this.idEspacioParken = idEspacioParken;
        this.estatusEspacioParken = estatusEspacioParken;
        this.idZonaParken = idZonaParken;
        this.latitudEspacioParken = latitudEspacioParken;
        this.longitudEspacioParken = longitudEspacioParken;
        this.direccionEspacioParken = direccionEspacioParken;
        this.idAutomovilista = idAutomovilista;
        this.nombreAutomovilista = nombreAutomovilista;
        this.apellidoAutomovilista = apellidoAutomovilista;
        this.correoAutomovilista = correoAutomovilista;
        this.celAutomovilista = celAutomovilista;
        this.tokenAutomovilista = tokenAutomovilista;
        this.puntosParken = puntosParken;
        this.jsonReporte = jsonReporte;
        this.imgMapLink = imgMapLink;
    }

    public String getJsonReporte() {
        return jsonReporte;
    }

    public void setJsonReporte(String jsonReporte) {
        this.jsonReporte = jsonReporte;
    }

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public String getTiempoReporte() {
        return tiempoReporte;
    }

    public void setTiempoReporte(String tiempoReporte) {
        this.tiempoReporte = tiempoReporte;
    }

    public String getObservacionReporte() {
        return observacionReporte;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public void setObservacionReporte(String observacionReporte) {

        this.observacionReporte = observacionReporte;
    }

    public String getEstatusReporte() {
        return estatusReporte;
    }

    public void setEstatusReporte(String estatusReporte) {
        this.estatusReporte = estatusReporte;
    }

    public int getIdEspacioParken() {
        return idEspacioParken;
    }

    public void setIdEspacioParken(int idEspacioParken) {
        this.idEspacioParken = idEspacioParken;
    }

    public String getEstatusEspacioParken() {
        return estatusEspacioParken;
    }

    public void setEstatusEspacioParken(String estatusEspacioParken) {
        this.estatusEspacioParken = estatusEspacioParken;
    }

    public int getIdZonaParken() {
        return idZonaParken;
    }

    public void setIdZonaParken(int idZonaParken) {
        this.idZonaParken = idZonaParken;
    }

    public double getLatitudEspacioParken() {
        return latitudEspacioParken;
    }

    public void setLatitudEspacioParken(double latitudEspacioParken) {
        this.latitudEspacioParken = latitudEspacioParken;
    }

    public double getLongitudEspacioParken() {
        return longitudEspacioParken;
    }

    public void setLongitudEspacioParken(double longitudEspacioParken) {
        this.longitudEspacioParken = longitudEspacioParken;
    }

    public String getDireccionEspacioParken() {
        return direccionEspacioParken;
    }

    public void setDireccionEspacioParken(String direccionEspacioParken) {
        this.direccionEspacioParken = direccionEspacioParken;
    }

    public int getIdAutomovilista() {
        return idAutomovilista;
    }

    public void setIdAutomovilista(int idAutomovilista) {
        this.idAutomovilista = idAutomovilista;
    }

    public String getNombreAutomovilista() {
        return nombreAutomovilista;
    }

    public void setNombreAutomovilista(String nombreAutomovilista) {
        this.nombreAutomovilista = nombreAutomovilista;
    }

    public String getApellidoAutomovilista() {
        return apellidoAutomovilista;
    }

    public void setApellidoAutomovilista(String apellidoAutomovilista) {
        this.apellidoAutomovilista = apellidoAutomovilista;
    }

    public String getCorreoAutomovilista() {
        return correoAutomovilista;
    }

    public void setCorreoAutomovilista(String correoAutomovilista) {
        this.correoAutomovilista = correoAutomovilista;
    }

    public String getCelAutomovilista() {
        return celAutomovilista;
    }

    public void setCelAutomovilista(String celAutomovilista) {
        this.celAutomovilista = celAutomovilista;
    }

    public String getTokenAutomovilista() {
        return tokenAutomovilista;
    }

    public void setTokenAutomovilista(String tokenAutomovilista) {
        this.tokenAutomovilista = tokenAutomovilista;
    }

    public double getPuntosParken() {
        return puntosParken;
    }

    public void setPuntosParken(double puntosParken) {
        this.puntosParken = puntosParken;
    }

    public String getImgMapLink() {
        return imgMapLink;
    }

    public void setImgMapLink(String imgMapLink) {
        this.imgMapLink = imgMapLink;
    }
}
