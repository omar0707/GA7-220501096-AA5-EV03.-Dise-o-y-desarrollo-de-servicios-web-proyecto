package com.canstyling.dto;

import java.math.BigDecimal;

public class RegistroWebClienteRequest {
    // Datos del Propietario
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String correo;
    private String password;
    private String telefonoPrincipal;
    private String telefonoSecundario;
    private String direccion;
    private String barrioCiudad;

    // Datos de la Mascota Inicial
    private String nombreMascota;
    private int idRaza;
    private String generoMascota; // MACHO, HEMBRA
    private String colorMascota;
    private Integer edadMeses;
    private BigDecimal pesoKg;
    private String temperamento;
    private String alergiasCondiciones;

    public RegistroWebClienteRequest() {}

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getTelefonoPrincipal() { return telefonoPrincipal; }
    public void setTelefonoPrincipal(String telefonoPrincipal) { this.telefonoPrincipal = telefonoPrincipal; }
    public String getTelefonoSecundario() { return telefonoSecundario; }
    public void setTelefonoSecundario(String telefonoSecundario) { this.telefonoSecundario = telefonoSecundario; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getBarrioCiudad() { return barrioCiudad; }
    public void setBarrioCiudad(String barrioCiudad) { this.barrioCiudad = barrioCiudad; }
    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }
    public int getIdRaza() { return idRaza; }
    public void setIdRaza(int idRaza) { this.idRaza = idRaza; }
    public String getGeneroMascota() { return generoMascota; }
    public void setGeneroMascota(String generoMascota) { this.generoMascota = generoMascota; }
    public String getColorMascota() { return colorMascota; }
    public void setColorMascota(String colorMascota) { this.colorMascota = colorMascota; }
    public Integer getEdadMeses() { return edadMeses; }
    public void setEdadMeses(Integer edadMeses) { this.edadMeses = edadMeses; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }
    public String getTemperamento() { return temperamento; }
    public void setTemperamento(String temperamento) { this.temperamento = temperamento; }
    public String getAlergiasCondiciones() { return alergiasCondiciones; }
    public void setAlergiasCondiciones(String alergiasCondiciones) { this.alergiasCondiciones = alergiasCondiciones; }
}
