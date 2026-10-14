package com.canstyling.model;

import java.sql.Timestamp;

public class ClienteTelefono {
    private int idTelefono;
    private int idCliente;
    private String numeroTelefono;
    private String tipoTelefono; // WHATSAPP, MOVIL, FIJO, TRABAJO
    private boolean esPrincipal;
    private boolean recibeNotificaciones;
    private Timestamp creadoEn;

    public ClienteTelefono() {}

    public ClienteTelefono(int idTelefono, int idCliente, String numeroTelefono, String tipoTelefono, boolean esPrincipal, boolean recibeNotificaciones) {
        this.idTelefono = idTelefono;
        this.idCliente = idCliente;
        this.numeroTelefono = numeroTelefono;
        this.tipoTelefono = tipoTelefono;
        this.esPrincipal = esPrincipal;
        this.recibeNotificaciones = recibeNotificaciones;
    }

    public int getIdTelefono() { return idTelefono; }
    public void setIdTelefono(int idTelefono) { this.idTelefono = idTelefono; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getNumeroTelefono() { return numeroTelefono; }
    public void setNumeroTelefono(String numeroTelefono) { this.numeroTelefono = numeroTelefono; }
    public String getTipoTelefono() { return tipoTelefono; }
    public void setTipoTelefono(String tipoTelefono) { this.tipoTelefono = tipoTelefono; }
    public boolean isEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(boolean esPrincipal) { this.esPrincipal = esPrincipal; }
    public boolean isRecibeNotificaciones() { return recibeNotificaciones; }
    public void setRecibeNotificaciones(boolean recibeNotificaciones) { this.recibeNotificaciones = recibeNotificaciones; }
    public Timestamp getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Timestamp creadoEn) { this.creadoEn = creadoEn; }
}
