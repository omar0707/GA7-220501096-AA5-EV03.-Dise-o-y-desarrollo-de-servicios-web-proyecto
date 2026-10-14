package com.canstyling.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Cita {
    private int idCita;
    private String tokenAcceso; // UUID v4 Anti-IDOR
    private int idCliente;
    private int idMascota;
    private int idServicio;
    private Integer idEstilista;
    private Date fechaCita;
    private Time horaCita;
    private int duracionMinutos;
    private String estadoCita; // PENDIENTE, CONFIRMADA, EN_PROCESO, COMPLETADA, CANCELADA, NO_ASISTIO
    private String origenReserva; // WEB, WHATSAPP, PRESENCIAL
    private String observacionesCliente;
    private Timestamp creadoEn;

    // Campos enriquecidos para vistas y DTOs
    private String clienteNombre;
    private String clienteCorreo;
    private String clienteTelefono;
    private String mascotaNombre;
    private String nombreRaza;
    private String nombreServicio;
    private BigDecimal precioBase;
    private String estilistaNombre;
    private List<FotoMascota> fotos = new ArrayList<>();

    public Cita() {}

    public String getClienteCorreo() { return clienteCorreo; }
    public void setClienteCorreo(String clienteCorreo) { this.clienteCorreo = clienteCorreo; }


    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }
    public String getTokenAcceso() { return tokenAcceso; }
    public void setTokenAcceso(String tokenAcceso) { this.tokenAcceso = tokenAcceso; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }
    public Integer getIdEstilista() { return idEstilista; }
    public void setIdEstilista(Integer idEstilista) { this.idEstilista = idEstilista; }
    public Date getFechaCita() { return fechaCita; }
    public void setFechaCita(Date fechaCita) { this.fechaCita = fechaCita; }
    public Time getHoraCita() { return horaCita; }
    public void setHoraCita(Time horaCita) { this.horaCita = horaCita; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public String getEstadoCita() { return estadoCita; }
    public void setEstadoCita(String estadoCita) { this.estadoCita = estadoCita; }
    public String getOrigenReserva() { return origenReserva; }
    public void setOrigenReserva(String origenReserva) { this.origenReserva = origenReserva; }
    public String getObservacionesCliente() { return observacionesCliente; }
    public void setObservacionesCliente(String observacionesCliente) { this.observacionesCliente = observacionesCliente; }
    public Timestamp getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Timestamp creadoEn) { this.creadoEn = creadoEn; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getClienteTelefono() { return clienteTelefono; }
    public void setClienteTelefono(String clienteTelefono) { this.clienteTelefono = clienteTelefono; }
    public String getMascotaNombre() { return mascotaNombre; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }
    public String getNombreRaza() { return nombreRaza; }
    public void setNombreRaza(String nombreRaza) { this.nombreRaza = nombreRaza; }
    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }
    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }
    public String getEstilistaNombre() { return estilistaNombre; }
    public void setEstilistaNombre(String estilistaNombre) { this.estilistaNombre = estilistaNombre; }
    public List<FotoMascota> getFotos() { return fotos; }
    public void setFotos(List<FotoMascota> fotos) { this.fotos = fotos; }
}
