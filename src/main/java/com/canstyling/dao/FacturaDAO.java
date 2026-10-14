package com.canstyling.dao;

import com.canstyling.model.DetalleFactura;
import com.canstyling.model.Factura;

import java.util.List;
import java.util.Optional;

public interface FacturaDAO {
    List<Factura> listarTodas();
    Optional<Factura> buscarPorId(int idFactura);
    Optional<Factura> buscarPorNumero(String numeroFactura);
    int registrar(Factura factura);
    boolean registrarDetalle(DetalleFactura detalle);
    List<DetalleFactura> listarDetallesPorFactura(int idFactura);
    String generarNumeroSiguiente();
}
