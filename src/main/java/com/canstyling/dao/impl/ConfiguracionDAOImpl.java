package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.ConfiguracionDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConfiguracionDAOImpl implements ConfiguracionDAO {

    @Override
    public Map<String, String> obtenerTodas() {
        Map<String, String> config = new HashMap<>();
        String sql = "SELECT clave, valor FROM configuracion_negocio";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                config.put(rs.getString("clave"), rs.getString("valor"));
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerTodas Configuracion: " + e.getMessage());
        }
        return config;
    }

    @Override
    public String obtenerPorClave(String clave) {
        String sql = "SELECT valor FROM configuracion_negocio WHERE clave = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clave);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("valor");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerPorClave Configuracion: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean guardarOActualizar(String clave, String valor, String descripcion) {
        String sql = "INSERT INTO configuracion_negocio (clave, valor, descripcion) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE valor = VALUES(valor), descripcion = COALESCE(VALUES(descripcion), descripcion)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clave);
            stmt.setString(2, valor);
            stmt.setString(3, descripcion);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en guardarOActualizar Configuracion: " + e.getMessage());
            return false;
        }
    }
}
