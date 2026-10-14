package com.canstyling.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CrearFacturaRequest {
    private Integer idCita;
    private int idCliente;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal descuento;
    private BigDecimal total;
    private String metodoPago; // EFECTIVO, TARJETA_DEBITO, TARJETA_CREDITO, TRANSFERENCIA_NEQUI_DAVIPLATA, OTRO
    private List<ItemFacturaDTO> items = new ArrayList<>();

    public static class ItemFacturaDTO {
        private Integer idServicio;
        private Integer idProducto;
        private String descripcion;
        private int cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;

        public ItemFacturaDTO() {}

        public Integer getIdServicio() { return idServicio; }
        public void setIdServicio(Integer idServicio) { this.idServicio = idServicio; }
        public Integer getIdProducto() { return idProducto; }
        public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }

    public CrearFacturaRequest() {}

    public Integer getIdCita() { return idCita; }
    public void setIdCita(Integer idCita) { this.idCita = idCita; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public List<ItemFacturaDTO> getItems() { return items; }
    public void setItems(List<ItemFacturaDTO> items) { this.items = items; }
}
