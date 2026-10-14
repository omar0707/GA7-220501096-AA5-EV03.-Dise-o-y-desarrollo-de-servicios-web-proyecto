package com.canstyling.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Mascota {
    private int idMascota;
    private int idCliente;
    private int idRaza;
    private String nombreRaza;
    private String nombre;
    private String genero; // MACHO, HEMBRA
    private String color;
    private Integer edadAproximada;
    private BigDecimal pesoKg;
    private String temperamento;
    private String alergiasCondiciones;
    private boolean vacunasAlDia;
    private String fotoPerfilUrl;
    private String estado;
    private Timestamp creadoEn;

    public Mascota() {}

    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdRaza() { return idRaza; }
    public void setIdRaza(int idRaza) { this.idRaza = idRaza; }
    public String getNombreRaza() { return nombreRaza; }
    public void setNombreRaza(String nombreRaza) { this.nombreRaza = nombreRaza; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Integer getEdadAproximada() { return edadAproximada; }
    public void setEdadAproximada(Integer edadAproximada) { this.edadAproximada = edadAproximada; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }
    public String getTemperamento() { return temperamento; }
    public void setTemperamento(String temperamento) { this.temperamento = temperamento; }
    public String getAlergiasCondiciones() { return alergiasCondiciones; }
    public void setAlergiasCondiciones(String alergiasCondiciones) { this.alergiasCondiciones = alergiasCondiciones; }
    public boolean isVacunasAlDia() { return vacunasAlDia; }
    public void setVacunasAlDia(boolean vacunasAlDia) { this.vacunasAlDia = vacunasAlDia; }
    public String getFotoPerfilUrl() { return fotoPerfilUrl; }
    public void setFotoPerfilUrl(String fotoPerfilUrl) { this.fotoPerfilUrl = fotoPerfilUrl; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Timestamp getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Timestamp creadoEn) { this.creadoEn = creadoEn; }
}
