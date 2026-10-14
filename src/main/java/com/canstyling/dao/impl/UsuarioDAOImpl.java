package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.UsuarioDAO;
import com.canstyling.model.Usuario;

import java.sql.*;
import java.util.Optional;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND estado = 'ACTIVO'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorCorreo Usuario: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorId(int idUsuario) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorId Usuario: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public int registrar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (id_rol, nombres, apellidos, correo, password_hash, telefono, canal_registro, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, usuario.getIdRol() > 0 ? usuario.getIdRol() : 4); // 4 = Cliente por defecto
            stmt.setString(2, usuario.getNombres());
            stmt.setString(3, usuario.getApellidos());
            stmt.setString(4, usuario.getCorreo());
            stmt.setString(5, usuario.getPasswordHash());
            stmt.setString(6, usuario.getTelefono());
            stmt.setString(7, usuario.getCanalRegistro() != null ? usuario.getCanalRegistro() : "WEB");
            stmt.setString(8, "ACTIVO");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en registrar Usuario: " + e.getMessage());
        }
        return -1;
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setIdRol(rs.getInt("id_rol"));
        u.setNombres(rs.getString("nombres"));
        u.setApellidos(rs.getString("apellidos"));
        u.setCorreo(rs.getString("correo"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setTelefono(rs.getString("telefono"));
        u.setCanalRegistro(rs.getString("canal_registro"));
        u.setEstado(rs.getString("estado"));
        u.setCreadoEn(rs.getTimestamp("creado_en"));
        return u;
    }
}
