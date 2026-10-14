package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.InventarioDAO;
import com.canstyling.model.InventarioInsumo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventarioDAOImpl implements InventarioDAO {

    @Override
    public List<InventarioInsumo> listarTodos() {
        List<InventarioInsumo> insumos = new ArrayList<>();
        String sql = "SELECT * FROM inventario_insumos ORDER BY categoria ASC, nombre_producto ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                insumos.add(mapearInsumo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en listarTodos Inventario: " + e.getMessage());
        }
        return insumos;
    }

    @Override
    public Optional<InventarioInsumo> buscarPorId(int idProducto) {
        String sql = "SELECT * FROM inventario_insumos WHERE id_producto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearInsumo(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorId Inventario: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<InventarioInsumo> listarAlertasStockBajo() {
        List<InventarioInsumo> alertas = new ArrayList<>();
        String sql = "SELECT * FROM inventario_insumos WHERE stock_actual <= stock_minimo ORDER BY stock_actual ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                alertas.add(mapearInsumo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en listarAlertasStockBajo: " + e.getMessage());
        }
        return alertas;
    }

    @Override
    public int registrar(InventarioInsumo insumo) {
        String sql = "INSERT INTO inventario_insumos (codigo_barras, nombre_producto, categoria, stock_actual, stock_minimo, unidad_medida, precio_compra, precio_venta) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, insumo.getCodigoBarras());
            stmt.setString(2, insumo.getNombreProducto());
            stmt.setString(3, insumo.getCategoria());
            stmt.setInt(4, insumo.getStockActual());
            stmt.setInt(5, insumo.getStockMinimo());
            stmt.setString(6, insumo.getUnidadMedida() != null ? insumo.getUnidadMedida() : "Unidad");
            stmt.setBigDecimal(7, insumo.getPrecioCompra());
            stmt.setBigDecimal(8, insumo.getPrecioVenta());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en registrar Insumo: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean actualizarStock(int idProducto, int cantidadAjuste) {
        String sql = "UPDATE inventario_insumos SET stock_actual = stock_actual + ? WHERE id_producto = ? AND (stock_actual + ?) >= 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cantidadAjuste);
            stmt.setInt(2, idProducto);
            stmt.setInt(3, cantidadAjuste);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizarStock: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(InventarioInsumo insumo) {
        String sql = "UPDATE inventario_insumos SET nombre_producto = ?, categoria = ?, stock_minimo = ?, precio_compra = ?, precio_venta = ? WHERE id_producto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, insumo.getNombreProducto());
            stmt.setString(2, insumo.getCategoria());
            stmt.setInt(3, insumo.getStockMinimo());
            stmt.setBigDecimal(4, insumo.getPrecioCompra());
            stmt.setBigDecimal(5, insumo.getPrecioVenta());
            stmt.setInt(6, insumo.getIdProducto());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizar Insumo: " + e.getMessage());
            return false;
        }
    }

    private InventarioInsumo mapearInsumo(ResultSet rs) throws SQLException {
        InventarioInsumo i = new InventarioInsumo();
        i.setIdProducto(rs.getInt("id_producto"));
        i.setCodigoBarras(rs.getString("codigo_barras"));
        i.setNombreProducto(rs.getString("nombre_producto"));
        i.setCategoria(rs.getString("categoria"));
        i.setStockActual(rs.getInt("stock_actual"));
        i.setStockMinimo(rs.getInt("stock_minimo"));
        i.setUnidadMedida(rs.getString("unidad_medida"));
        i.setPrecioCompra(rs.getBigDecimal("precio_compra"));
        i.setPrecioVenta(rs.getBigDecimal("precio_venta"));
        i.setActualizadoEn(rs.getTimestamp("actualizado_en"));
        return i;
    }
}
