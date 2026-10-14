package com.canstyling.dto;

public class AgendarCitaRequest {
    private int idCliente;
    private int idMascota;
    private int idServicio;
    private Integer idEstilista;
    private String fechaCita; // YYYY-MM-DD
    private String horaCita;  // HH:MM:SS
    private String origenReserva; // WEB, WHATSAPP, PRESENCIAL
    private String observaciones;
    private Integer duracionMinutos;

    public AgendarCitaRequest() {}

    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }


    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }
    public Integer getIdEstilista() { return idEstilista; }
    public void setIdEstilista(Integer idEstilista) { this.idEstilista = idEstilista; }
    public String getFechaCita() { return fechaCita; }
    public void setFechaCita(String fechaCita) { this.fechaCita = fechaCita; }
    public String getHoraCita() { return horaCita; }
    public void setHoraCita(String horaCita) { this.horaCita = horaCita; }
    public String getOrigenReserva() { return origenReserva; }
    public void setOrigenReserva(String origenReserva) { this.origenReserva = origenReserva; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
