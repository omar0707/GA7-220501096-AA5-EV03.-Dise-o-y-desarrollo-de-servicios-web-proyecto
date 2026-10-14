package com.canstyling.dto;

public class RegistroWhatsAppRequest {
    private String numeroWhatsApp;
    private String nombres;
    private String apellidos;
    private String password;
    private String nombreMascota;
    private String razaMascota;

    public RegistroWhatsAppRequest() {}

    public RegistroWhatsAppRequest(String numeroWhatsApp, String nombres, String apellidos, String nombreMascota, String razaMascota) {
        this.numeroWhatsApp = numeroWhatsApp;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.nombreMascota = nombreMascota;
        this.razaMascota = razaMascota;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNumeroWhatsApp() { return numeroWhatsApp; }
    public void setNumeroWhatsApp(String numeroWhatsApp) { this.numeroWhatsApp = numeroWhatsApp; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }
    public String getRazaMascota() { return razaMascota; }
    public void setRazaMascota(String razaMascota) { this.razaMascota = razaMascota; }
}
