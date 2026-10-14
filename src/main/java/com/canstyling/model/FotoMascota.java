package com.canstyling.model;

import java.sql.Timestamp;

public class FotoMascota {
    private int idFoto;
    private int idCita;
    private String tipoFoto; // ANTES, DESPUES
    private String urlFoto;
    private String cloudinaryPublicId;
    private Timestamp subidaEn;

    public FotoMascota() {}

    public FotoMascota(int idFoto, int idCita, String tipoFoto, String urlFoto, String cloudinaryPublicId) {
        this.idFoto = idFoto;
        this.idCita = idCita;
        this.tipoFoto = tipoFoto;
        this.urlFoto = urlFoto;
        this.cloudinaryPublicId = cloudinaryPublicId;
    }

    public int getIdFoto() { return idFoto; }
    public void setIdFoto(int idFoto) { this.idFoto = idFoto; }
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }
    public String getTipoFoto() { return tipoFoto; }
    public void setTipoFoto(String tipoFoto) { this.tipoFoto = tipoFoto; }
    public String getUrlFoto() { return urlFoto; }
    public void setUrlFoto(String urlFoto) { this.urlFoto = urlFoto; }
    public String getCloudinaryPublicId() { return cloudinaryPublicId; }
    public void setCloudinaryPublicId(String cloudinaryPublicId) { this.cloudinaryPublicId = cloudinaryPublicId; }
    public Timestamp getSubidaEn() { return subidaEn; }
    public void setSubidaEn(Timestamp subidaEn) { this.subidaEn = subidaEn; }
}
