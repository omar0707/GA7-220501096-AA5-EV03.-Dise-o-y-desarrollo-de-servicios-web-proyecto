package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.OtpDAO;
import com.canstyling.model.CodigoOTP;

import java.sql.*;
import java.util.Optional;

public class OtpDAOImpl implements OtpDAO {

    @Override
    public boolean guardarOTP(CodigoOTP otp) {
        String sql = "INSERT INTO codigos_otp (correo, codigo_hash, intentos, usado, expira_en) VALUES (?, ?, 0, FALSE, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, otp.getCorreo());
            stmt.setString(2, otp.getCodigoHash());
            stmt.setTimestamp(3, otp.getExpiraEn());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en guardarOTP: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<CodigoOTP> buscarOTPValido(String correo) {
        String sql = "SELECT * FROM codigos_otp WHERE correo = ? AND usado = FALSE AND expira_en > CURRENT_TIMESTAMP ORDER BY creado_en DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CodigoOTP otp = new CodigoOTP();
                    otp.setIdOtp(rs.getInt("id_otp"));
                    otp.setCorreo(rs.getString("correo"));
                    otp.setCodigoHash(rs.getString("codigo_hash"));
                    otp.setIntentos(rs.getInt("intentos"));
                    otp.setUsado(rs.getBoolean("usado"));
                    otp.setExpiraEn(rs.getTimestamp("expira_en"));
                    otp.setCreadoEn(rs.getTimestamp("creado_en"));
                    return Optional.of(otp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarOTPValido: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean marcarComoUsado(int idOtp) {
        String sql = "UPDATE codigos_otp SET usado = TRUE WHERE id_otp = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idOtp);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en marcarComoUsado OTP: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean incrementarIntentos(int idOtp) {
        String sql = "UPDATE codigos_otp SET intentos = intentos + 1 WHERE id_otp = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idOtp);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en incrementarIntentos OTP: " + e.getMessage());
            return false;
        }
    }
}
