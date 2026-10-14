package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.MascotaDAO;
import com.canstyling.model.Mascota;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MascotaDAOImpl implements MascotaDAO {

    @Override
    public List<Mascota> listarPorCliente(int idCliente) {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = "SELECT m.*, r.nombre_raza FROM mascotas m INNER JOIN razas r ON m.id_raza = r.id_raza WHERE m.id_cliente = ? AND m.estado = 'ACTIVO'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mascotas.add(mapearMascota(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarPorCliente Mascotas: " + e.getMessage());
        }
        return mascotas;
    }

    @Override
    public Optional<Mascota> buscarPorId(int idMascota) {
        String sql = "SELECT m.*, r.nombre_raza FROM mascotas m INNER JOIN razas r ON m.id_raza = r.id_raza WHERE m.id_mascota = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMascota);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearMascota(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorId Mascota: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public int registrar(Mascota mascota) {
        String sql = "INSERT INTO mascotas (id_cliente, id_raza, nombre, genero, color, edad_aproximada, peso_kg, temperamento, alergias_condiciones, vacunas_al_dia, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, mascota.getIdCliente());
            stmt.setInt(2, mascota.getIdRaza() > 0 ? mascota.getIdRaza() : 5); // 5 = Mestizo por defecto
            stmt.setString(3, mascota.getNombre());
            stmt.setString(4, mascota.getGenero() != null ? mascota.getGenero() : "MACHO");
            stmt.setString(5, mascota.getColor());
            stmt.setObject(6, mascota.getEdadAproximada());
            stmt.setBigDecimal(7, mascota.getPesoKg());
            stmt.setString(8, mascota.getTemperamento() != null ? mascota.getTemperamento() : "DOCIL");
            stmt.setString(9, mascota.getAlergiasCondiciones());
            stmt.setBoolean(10, mascota.isVacunasAlDia());
            stmt.setString(11, "ACTIVO");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en registrar Mascota: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean actualizar(Mascota mascota) {
        StringBuilder sql = new StringBuilder("UPDATE mascotas SET ");
        List<Object> params = new java.util.ArrayList<>();

        if (mascota.getIdRaza() > 0) {
            sql.append("id_raza = ?, ");
            params.add(mascota.getIdRaza());
        }
        if (mascota.getNombre() != null && !mascota.getNombre().trim().isEmpty()) {
            sql.append("nombre = ?, ");
            params.add(mascota.getNombre());
        }
        if (mascota.getGenero() != null) {
            sql.append("genero = ?, ");
            params.add(mascota.getGenero());
        }
        if (mascota.getColor() != null) {
            sql.append("color = ?, ");
            params.add(mascota.getColor());
        }
        if (mascota.getEdadAproximada() != null) {
            sql.append("edad_aproximada = ?, ");
            params.add(mascota.getEdadAproximada());
        }
        if (mascota.getPesoKg() != null) {
            sql.append("peso_kg = ?, ");
            params.add(mascota.getPesoKg());
        }
        if (mascota.getTemperamento() != null) {
            sql.append("temperamento = ?, ");
            params.add(mascota.getTemperamento());
        }
        if (mascota.getAlergiasCondiciones() != null) {
            sql.append("alergias_condiciones = ?, ");
            params.add(mascota.getAlergiasCondiciones());
        }
        sql.append("vacunas_al_dia = ? WHERE id_mascota = ?");
        params.add(mascota.isVacunasAlDia());
        params.add(mascota.getIdMascota());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizar Mascota: " + e.getMessage());
            return false;
        }
    }


    private Mascota mapearMascota(ResultSet rs) throws SQLException {
        Mascota m = new Mascota();
        m.setIdMascota(rs.getInt("id_mascota"));
        m.setIdCliente(rs.getInt("id_cliente"));
        m.setIdRaza(rs.getInt("id_raza"));
        m.setNombreRaza(rs.getString("nombre_raza"));
        m.setNombre(rs.getString("nombre"));
        m.setGenero(rs.getString("genero"));
        m.setColor(rs.getString("color"));
        m.setEdadAproximada((Integer) rs.getObject("edad_aproximada"));
        m.setPesoKg(rs.getBigDecimal("peso_kg"));
        m.setTemperamento(rs.getString("temperamento"));
        m.setAlergiasCondiciones(rs.getString("alergias_condiciones"));
        m.setVacunasAlDia(rs.getBoolean("vacunas_al_dia"));
        m.setFotoPerfilUrl(rs.getString("foto_perfil_url"));
        m.setEstado(rs.getString("estado"));
        m.setCreadoEn(rs.getTimestamp("creado_en"));
        return m;
    }
}
