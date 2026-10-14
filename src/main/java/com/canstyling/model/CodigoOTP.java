package com.canstyling.model;

import java.sql.Timestamp;

public class CodigoOTP {
    private int idOtp;
    private String correo;
    private String codigoHash;
    private int intentos;
    private boolean usado;
    private Timestamp expiraEn;
    private Timestamp creadoEn;

    public CodigoOTP() {}

    public CodigoOTP(String correo, String codigoHash, Timestamp expiraEn) {
        this.correo = correo;
        this.codigoHash = codigoHash;
        this.expiraEn = expiraEn;
        this.intentos = 0;
        this.usado = false;
    }

    public int getIdOtp() { return idOtp; }
    public void setIdOtp(int idOtp) { this.idOtp = idOtp; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getCodigoHash() { return codigoHash; }
    public void setCodigoHash(String codigoHash) { this.codigoHash = codigoHash; }
    public int getIntentos() { return intentos; }
    public void setIntentos(int intentos) { this.intentos = intentos; }
    public boolean isUsado() { return usado; }
    public void setUsado(boolean usado) { this.usado = usado; }
    public Timestamp getExpiraEn() { return expiraEn; }
    public void setExpiraEn(Timestamp expiraEn) { this.expiraEn = expiraEn; }
    public Timestamp getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Timestamp creadoEn) { this.creadoEn = creadoEn; }
}
