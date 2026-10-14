package com.canstyling.service;

import com.canstyling.dao.FacturaDAO;
import com.canstyling.dao.InventarioDAO;
import com.canstyling.dao.impl.FacturaDAOImpl;
import com.canstyling.dao.impl.InventarioDAOImpl;
import com.canstyling.dto.CrearFacturaRequest;
import com.canstyling.model.DetalleFactura;
import com.canstyling.model.Factura;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FacturaService {

    private final FacturaDAO facturaDAO = new FacturaDAOImpl();
    private final InventarioDAO inventarioDAO = new InventarioDAOImpl();

    public List<Factura> listarFacturas() {
        return facturaDAO.listarTodas();
    }

    public Optional<Factura> buscarPorId(int idFactura) {
        return facturaDAO.buscarPorId(idFactura);
    }

    public Map<String, Object> emitirFactura(CrearFacturaRequest req) {
        Map<String, Object> res = new HashMap<>();

        if (req.getIdCliente() <= 0 || req.getItems() == null || req.getItems().isEmpty()) {
            res.put("success", false);
            res.put("message", "Cliente e items de la factura son requeridos.");
            return res;
        }

        String numeroFactura = facturaDAO.generarNumeroSiguiente();

        Factura f = new Factura();
        f.setIdCita(req.getIdCita());
        f.setIdCliente(req.getIdCliente());
        f.setNumeroFactura(numeroFactura);
        f.setSubtotal(req.getSubtotal() != null ? req.getSubtotal() : BigDecimal.ZERO);
        f.setIva(req.getIva() != null ? req.getIva() : BigDecimal.ZERO);
        f.setDescuento(req.getDescuento() != null ? req.getDescuento() : BigDecimal.ZERO);
        f.setTotal(req.getTotal() != null ? req.getTotal() : BigDecimal.ZERO);
        f.setMetodoPago(req.getMetodoPago() != null ? req.getMetodoPago() : "EFECTIVO");

        // Convertir DTOs a entidad
        for (CrearFacturaRequest.ItemFacturaDTO item : req.getItems()) {
            DetalleFactura d = new DetalleFactura(
                0,
                0,
                item.getIdServicio(),
                item.getIdProducto(),
                item.getDescripcion(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal()
            );
            f.getDetalles().add(d);

            // Si es producto físico de inventario, descontar stock automáticamente
            if (item.getIdProducto() != null && item.getIdProducto() > 0) {
                inventarioDAO.actualizarStock(item.getIdProducto(), -item.getCantidad());
            }
        }

        int idFactura = facturaDAO.registrar(f);
        if (idFactura > 0) {
            f.setIdFactura(idFactura);
            res.put("success", true);
            res.put("message", "Factura emitida exitosamente con número: " + numeroFactura);
            res.put("factura", f);
        } else {
            res.put("success", false);
            res.put("message", "Error al registrar la factura en la base de datos.");
        }
        return res;
    }
}
