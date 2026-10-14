package com.canstyling.dto;

public class ActualizarStockRequest {
    private int cantidadAjuste; // Positivo (ingreso) o negativo (salida)
    private String motivo;

    public ActualizarStockRequest() {}

    public ActualizarStockRequest(int cantidadAjuste, String motivo) {
        this.cantidadAjuste = cantidadAjuste;
        this.motivo = motivo;
    }

    public int getCantidadAjuste() { return cantidadAjuste; }
    public void setCantidadAjuste(int cantidadAjuste) { this.cantidadAjuste = cantidadAjuste; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
