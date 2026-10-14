package com.canstyling.service;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.UsuarioDAO;
import com.canstyling.dao.impl.UsuarioDAOImpl;
import com.canstyling.model.Usuario;
import com.canstyling.security.SecurityUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public Map<String, Object> autenticar(String correo, String password) {
        Map<String, Object> resultado = new HashMap<>();

        if (correo == null || correo.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            resultado.put("success", false);
            resultado.put("message", "Error en la autenticación: Correo y contraseña requeridos.");
            return resultado;
        }

        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorCorreo(correo.trim());
        if (usuarioOpt.isEmpty()) {
            resultado.put("success", false);
            resultado.put("message", "Error en la autenticación");
            return resultado;
        }

        Usuario u = usuarioOpt.get();
        if (!SecurityUtils.verificarPassword(password, u.getPasswordHash())) {
            resultado.put("success", false);
            resultado.put("message", "Error en la autenticación");
            return resultado;
        }

        resultado.put("success", true);
        resultado.put("message", "Autenticación satisfactoria");
        resultado.put("usuario", u);
        resultado.put("token", "JWT_CANSTYLING_" + SecurityUtils.generarTokenAntiIDOR());
        return resultado;
    }

    public boolean registrarUsuario(Usuario usuario) {
        if (usuarioDAO.buscarPorCorreo(usuario.getCorreo()).isPresent()) {
            return false;
        }
        usuario.setPasswordHash(SecurityUtils.hashPassword(usuario.getPasswordHash()));
        return usuarioDAO.registrar(usuario) > 0;
    }

    public boolean actualizarPassword(String correo, String nuevaPassword) {
        if (correo == null || nuevaPassword == null || nuevaPassword.trim().length() < 6) {
            return false;
        }
        String hash = SecurityUtils.hashPassword(nuevaPassword.trim());
        String sqlUsuario = "UPDATE usuarios SET password_hash = ? WHERE LOWER(correo) = LOWER(?)";
        String sqlCliente = "UPDATE clientes SET password_hash = ? WHERE LOWER(correo) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sqlUsuario)) {
                stmt.setString(1, hash);
                stmt.setString(2, correo.trim());
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlCliente)) {
                stmt.setString(1, hash);
                stmt.setString(2, correo.trim());
                stmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error en actualizarPassword: " + e.getMessage());
            return false;
        }
    }
}
