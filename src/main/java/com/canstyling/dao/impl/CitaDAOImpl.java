package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.CitaDAO;
import com.canstyling.model.Cita;
import com.canstyling.model.FotoMascota;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CitaDAOImpl implements CitaDAO {

    @Override
    public List<Cita> listarTodas(String estado) {
        List<Cita> citas = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM vw_citas_resumen");
        if (estado != null && !estado.isEmpty() && !estado.equalsIgnoreCase("TODAS")) {
            sql.append(" WHERE estado_cita = ?");
        }
        sql.append(" ORDER BY fecha_cita DESC, hora_cita DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (estado != null && !estado.isEmpty() && !estado.equalsIgnoreCase("TODAS")) {
                stmt.setString(1, estado);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Cita c = mapearCitaVista(rs);
                    c.setFotos(listarFotosPorCita(c.getIdCita()));
                    citas.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarTodas Citas: " + e.getMessage());
        }
        return citas;
    }

    @Override
    public Optional<Cita> buscarPorId(int idCita) {
        String sql = "SELECT * FROM vw_citas_resumen WHERE id_cita = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCita);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cita c = mapearCitaVista(rs);
                    c.setFotos(listarFotosPorCita(c.getIdCita()));
                    return Optional.of(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorId Cita: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cita> buscarPorToken(String tokenAcceso) {
        String sql = "SELECT * FROM vw_citas_resumen WHERE token_acceso = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tokenAcceso);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cita c = mapearCitaVista(rs);
                    c.setFotos(listarFotosPorCita(c.getIdCita()));
                    return Optional.of(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorToken Anti-IDOR: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public int agendar(Cita cita) {
        String sql = "INSERT INTO citas (token_acceso, id_cliente, id_mascota, id_servicio, id_estilista, fecha_cita, hora_cita, duracion_minutos, estado_cita, origen_reserva, observaciones_cliente) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cita.getTokenAcceso());
            stmt.setInt(2, cita.getIdCliente());
            stmt.setInt(3, cita.getIdMascota());
            stmt.setInt(4, cita.getIdServicio());
            stmt.setObject(5, cita.getIdEstilista());
            stmt.setDate(6, cita.getFechaCita());
            stmt.setTime(7, cita.getHoraCita());
            stmt.setInt(8, cita.getDuracionMinutos() > 0 ? cita.getDuracionMinutos() : 60);
            stmt.setString(9, cita.getEstadoCita() != null ? cita.getEstadoCita() : "PENDIENTE");
            stmt.setString(10, cita.getOrigenReserva() != null ? cita.getOrigenReserva() : "WEB");
            stmt.setString(11, cita.getObservacionesCliente());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en agendar Cita: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean cambiarEstado(int idCita, String nuevoEstado) {
        String sql = "UPDATE citas SET estado_cita = ? WHERE id_cita = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idCita);
            int updated = stmt.executeUpdate();
            if (updated > 0 && "COMPLETADA".equalsIgnoreCase(nuevoEstado)) {
                String sqlHistorial = "INSERT INTO historial_atenciones (id_cita, id_mascota, id_estilista, procedimiento_realizado, productos_aplicados, novedades_salud, recomendaciones_tutor) " +
                        "SELECT c.id_cita, c.id_mascota, COALESCE(c.id_estilista, 1), CONCAT('Atención completada: ', s.nombre_servicio), " +
                        "'Shampoo hipoalergénico con extracto de avena, acondicionador desenredante y perfume floral', " +
                        "'Piel sana, pelaje hidratado, uñas recortadas y sin ectoparásitos', " +
                        "'Cepillado frecuente recomendado y agendar mantenimiento en 3 semanas' " +
                        "FROM citas c JOIN servicios s ON c.id_servicio = s.id_servicio WHERE c.id_cita = ? " +
                        "ON DUPLICATE KEY UPDATE procedimiento_realizado = VALUES(procedimiento_realizado)";
                try (PreparedStatement stmtHist = conn.prepareStatement(sqlHistorial)) {
                    stmtHist.setInt(1, idCita);
                    stmtHist.executeUpdate();
                } catch (SQLException eHist) {
                    System.err.println("Advertencia insertando historial_atenciones: " + eHist.getMessage());
                }
            }
            return updated > 0;
        } catch (SQLException e) {
            System.err.println("Error en cambiarEstado Cita: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarEstadoYNotas(int idCita, String nuevoEstado, String notas) {
        StringBuilder sql = new StringBuilder("UPDATE citas SET ");
        List<Object> params = new ArrayList<>();
        if (nuevoEstado != null && !nuevoEstado.trim().isEmpty()) {
            sql.append("estado_cita = ?");
            params.add(nuevoEstado.trim().toUpperCase());
        }
        if (notas != null) {
            if (!params.isEmpty()) sql.append(", ");
            sql.append("observaciones_cliente = ?");
            params.add(notas);
        }
        if (params.isEmpty()) return true;
        sql.append(" WHERE id_cita = ?");
        params.add(idCita);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            int updated = stmt.executeUpdate();
            if (updated > 0 && "COMPLETADA".equalsIgnoreCase(nuevoEstado)) {
                String sqlHistorial = "INSERT INTO historial_atenciones (id_cita, id_mascota, id_estilista, procedimiento_realizado, productos_aplicados, novedades_salud, recomendaciones_tutor) " +
                        "SELECT c.id_cita, c.id_mascota, COALESCE(c.id_estilista, 1), CONCAT('Atención completada: ', s.nombre_servicio), " +
                        "'Shampoo hipoalergénico con extracto de avena, acondicionador desenredante y perfume floral', " +
                        "'Piel sana, pelaje hidratado, uñas recortadas y sin ectoparásitos', " +
                        "'Cepillado frecuente recomendado y agendar mantenimiento en 3 semanas' " +
                        "FROM citas c JOIN servicios s ON c.id_servicio = s.id_servicio WHERE c.id_cita = ? " +
                        "ON DUPLICATE KEY UPDATE procedimiento_realizado = VALUES(procedimiento_realizado)";
                try (PreparedStatement stmtHist = conn.prepareStatement(sqlHistorial)) {
                    stmtHist.setInt(1, idCita);
                    stmtHist.executeUpdate();
                } catch (SQLException eHist) {
                    System.err.println("Advertencia insertando historial_atenciones: " + eHist.getMessage());
                }
            }
            return updated > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizarEstadoYNotas Cita: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean registrarFoto(FotoMascota foto) {
        String sql = "INSERT INTO fotos_mascotas (id_cita, tipo_foto, url_foto, cloudinary_public_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, foto.getIdCita());
            stmt.setString(2, foto.getTipoFoto());
            stmt.setString(3, foto.getUrlFoto());
            stmt.setString(4, foto.getCloudinaryPublicId());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        foto.setIdFoto(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error en registrarFoto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<FotoMascota> listarFotosPorCita(int idCita) {
        List<FotoMascota> fotos = new ArrayList<>();
        String sql = "SELECT * FROM fotos_mascotas WHERE id_cita = ? ORDER BY id_foto ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCita);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fotos.add(new FotoMascota(
                        rs.getInt("id_foto"),
                        rs.getInt("id_cita"),
                        rs.getString("tipo_foto"),
                        rs.getString("url_foto"),
                        rs.getString("cloudinary_public_id")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarFotosPorCita: " + e.getMessage());
        }
        return fotos;
    }

    @Override
    public boolean eliminarFoto(int idFoto) {
        String sql = "DELETE FROM fotos_mascotas WHERE id_foto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFoto);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en eliminarFoto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarFotosPorCita(int idCita) {
        String sql = "DELETE FROM fotos_mascotas WHERE id_cita = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCita);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("Error en eliminarFotosPorCita: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int contarCitasActivasPorCliente(int idCliente) {
        String sql = "SELECT COUNT(*) FROM citas WHERE id_cliente = ? AND estado_cita IN ('PENDIENTE', 'CONFIRMADA', 'EN_PROCESO')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en contarCitasActivasPorCliente: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int contarCitasActivasPorMascota(int idMascota) {
        String sql = "SELECT COUNT(*) FROM citas WHERE id_mascota = ? AND estado_cita IN ('PENDIENTE', 'CONFIRMADA', 'EN_PROCESO')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMascota);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en contarCitasActivasPorMascota: " + e.getMessage());
        }
        return 0;
    }

    private Cita mapearCitaVista(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setIdCita(rs.getInt("id_cita"));
        c.setTokenAcceso(rs.getString("token_acceso"));
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setIdMascota(rs.getInt("id_mascota"));
        c.setIdServicio(rs.getInt("id_servicio"));
        c.setIdEstilista((Integer) rs.getObject("id_estilista"));
        c.setDuracionMinutos(rs.getInt("duracion_minutos"));
        c.setObservacionesCliente(rs.getString("observaciones_cliente"));
        c.setFechaCita(rs.getDate("fecha_cita"));
        c.setHoraCita(rs.getTime("hora_cita"));
        c.setEstadoCita(rs.getString("estado_cita"));
        c.setOrigenReserva(rs.getString("origen_reserva"));
        c.setClienteNombre(rs.getString("cliente_nombre"));
        c.setClienteCorreo(rs.getString("cliente_correo"));
        try {
            c.setClienteTelefono(rs.getString("cliente_telefono"));
        } catch (SQLException ignored) {}
        c.setMascotaNombre(rs.getString("mascota_nombre"));
        c.setNombreRaza(rs.getString("nombre_raza"));
        c.setNombreServicio(rs.getString("nombre_servicio"));
        c.setPrecioBase(rs.getBigDecimal("precio_base"));
        c.setEstilistaNombre(rs.getString("estilista_nombre"));
        return c;
    }
}

