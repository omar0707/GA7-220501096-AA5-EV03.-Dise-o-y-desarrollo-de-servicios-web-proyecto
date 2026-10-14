package com.canstyling.model;

import java.sql.Timestamp;

public class Usuario {
    private int idUsuario;
    private int idRol;
    private String nombres;
    private String apellidos;
    private String correo;
    private String passwordHash;
    private String telefono;
    private String canalRegistro;
    private String estado;
    private Timestamp creadoEn;

    public Usuario() {}

    public Usuario(int idUsuario, int idRol, String nombres, String apellidos, String correo, String passwordHash, String telefono, String canalRegistro, String estado) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.telefono = telefono;
        this.canalRegistro = canalRegistro;
        this.estado = estado;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCanalRegistro() { return canalRegistro; }
    public void setCanalRegistro(String canalRegistro) { this.canalRegistro = canalRegistro; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Timestamp getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Timestamp creadoEn) { this.creadoEn = creadoEn; }
}
