package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.FacturaDAO;
import com.canstyling.model.DetalleFactura;
import com.canstyling.model.Factura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FacturaDAOImpl implements FacturaDAO {

    @Override
    public List<Factura> listarTodas() {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT f.*, CONCAT(c.nombres, ' ', c.apellidos) AS cliente_nombre FROM facturas f INNER JOIN clientes c ON f.id_cliente = c.id_cliente ORDER BY f.fecha_emision DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Factura f = mapearFactura(rs);
                f.setDetalles(listarDetallesPorFactura(f.getIdFactura()));
                facturas.add(f);
            }
        } catch (SQLException e) {
            System.err.println("Error en listarTodas Facturas: " + e.getMessage());
        }
        return facturas;
    }

    @Override
    public Optional<Factura> buscarPorId(int idFactura) {
        String sql = "SELECT f.*, CONCAT(c.nombres, ' ', c.apellidos) AS cliente_nombre FROM facturas f INNER JOIN clientes c ON f.id_cliente = c.id_cliente WHERE f.id_factura = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFactura);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Factura f = mapearFactura(rs);
                    f.setDetalles(listarDetallesPorFactura(f.getIdFactura()));
                    return Optional.of(f);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorId Factura: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Factura> buscarPorNumero(String numeroFactura) {
        String sql = "SELECT f.*, CONCAT(c.nombres, ' ', c.apellidos) AS cliente_nombre FROM facturas f INNER JOIN clientes c ON f.id_cliente = c.id_cliente WHERE f.numero_factura = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroFactura);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Factura f = mapearFactura(rs);
                    f.setDetalles(listarDetallesPorFactura(f.getIdFactura()));
                    return Optional.of(f);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorNumero Factura: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public int registrar(Factura factura) {
        String sql = "INSERT INTO facturas (id_cita, id_cliente, numero_factura, subtotal, iva, descuento, total, metodo_pago, estado_pago) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setObject(1, factura.getIdCita());
            stmt.setInt(2, factura.getIdCliente());
            stmt.setString(3, factura.getNumeroFactura());
            stmt.setBigDecimal(4, factura.getSubtotal());
            stmt.setBigDecimal(5, factura.getIva());
            stmt.setBigDecimal(6, factura.getDescuento());
            stmt.setBigDecimal(7, factura.getTotal());
            stmt.setString(8, factura.getMetodoPago() != null ? factura.getMetodoPago() : "EFECTIVO");
            stmt.setString(9, "PAGADA");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        int idFactura = keys.getInt(1);
                        // Registrar items del detalle
                        for (DetalleFactura d : factura.getDetalles()) {
                            d.setIdFactura(idFactura);
                            registrarDetalle(d);
                        }
                        return idFactura;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en registrar Factura: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean registrarDetalle(DetalleFactura detalle) {
        String sql = "INSERT INTO detalle_facturas (id_factura, id_servicio, id_producto, descripcion, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detalle.getIdFactura());
            stmt.setObject(2, detalle.getIdServicio());
            stmt.setObject(3, detalle.getIdProducto());
            stmt.setString(4, detalle.getDescripcion());
            stmt.setInt(5, detalle.getCantidad());
            stmt.setBigDecimal(6, detalle.getPrecioUnitario());
            stmt.setBigDecimal(7, detalle.getSubtotal());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en registrarDetalle: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<DetalleFactura> listarDetallesPorFactura(int idFactura) {
        List<DetalleFactura> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_facturas WHERE id_factura = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFactura);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new DetalleFactura(
                        rs.getInt("id_detalle"),
                        rs.getInt("id_factura"),
                        (Integer) rs.getObject("id_servicio"),
                        (Integer) rs.getObject("id_producto"),
                        rs.getString("descripcion"),
                        rs.getInt("cantidad"),
                        rs.getBigDecimal("precio_unitario"),
                        rs.getBigDecimal("subtotal")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarDetallesPorFactura: " + e.getMessage());
        }
        return detalles;
    }

    @Override
    public String generarNumeroSiguiente() {
        String sql = "SELECT COUNT(*) FROM facturas";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1) + 1;
                return String.format("FAC-2026-%04d", count);
            }
        } catch (SQLException e) {
            System.err.println("Error en generarNumeroSiguiente: " + e.getMessage());
        }
        return "FAC-2026-0001";
    }

    private Factura mapearFactura(ResultSet rs) throws SQLException {
        Factura f = new Factura();
        f.setIdFactura(rs.getInt("id_factura"));
        f.setIdCita((Integer) rs.getObject("id_cita"));
        f.setIdCliente(rs.getInt("id_cliente"));
        f.setNumeroFactura(rs.getString("numero_factura"));
        f.setFechaEmision(rs.getTimestamp("fecha_emision"));
        f.setSubtotal(rs.getBigDecimal("subtotal"));
        f.setIva(rs.getBigDecimal("iva"));
        f.setDescuento(rs.getBigDecimal("descuento"));
        f.setTotal(rs.getBigDecimal("total"));
        f.setMetodoPago(rs.getString("metodo_pago"));
        f.setEstadoPago(rs.getString("estado_pago"));
        f.setClienteNombre(rs.getString("cliente_nombre"));
        return f;
    }
}
