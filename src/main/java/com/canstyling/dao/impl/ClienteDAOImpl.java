package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.ClienteDAO;
import com.canstyling.model.Cliente;
import com.canstyling.model.ClienteTelefono;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nombres ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cliente c = mapearCliente(rs);
                c.setTelefonos(listarTelefonosPorCliente(c.getIdCliente()));
                clientes.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error en listarTodos Clientes: " + e.getMessage());
        }
        return clientes;
    }

    @Override
    public Optional<Cliente> buscarPorId(int idCliente) {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente c = mapearCliente(rs);
                    c.setTelefonos(listarTelefonosPorCliente(c.getIdCliente()));
                    return Optional.of(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorId Cliente: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(String correo) {
        String sql = "SELECT * FROM clientes WHERE correo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente c = mapearCliente(rs);
                    c.setTelefonos(listarTelefonosPorCliente(c.getIdCliente()));
                    return Optional.of(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorCorreo Cliente: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cliente> buscarPorTelefono(String numeroTelefono) {
        String sql = "SELECT c.* FROM clientes c INNER JOIN cliente_telefonos t ON c.id_cliente = t.id_cliente WHERE t.numero_telefono = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroTelefono);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente c = mapearCliente(rs);
                    c.setTelefonos(listarTelefonosPorCliente(c.getIdCliente()));
                    return Optional.of(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en buscarPorTelefono Cliente: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public int registrar(Cliente cliente) {
        String sql = "INSERT INTO clientes (id_usuario, correo, password_hash, nombres, apellidos, direccion, barrio_ciudad, canal_origen, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (cliente.getIdUsuario() != null && cliente.getIdUsuario() > 0) {
                stmt.setInt(1, cliente.getIdUsuario());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, cliente.getCorreo());
            stmt.setString(3, cliente.getPasswordHash());
            stmt.setString(4, cliente.getNombres());
            stmt.setString(5, cliente.getApellidos());
            stmt.setString(6, cliente.getDireccion());
            stmt.setString(7, cliente.getBarrioCiudad());
            stmt.setString(8, cliente.getCanalOrigen() != null ? cliente.getCanalOrigen() : "WEB");
            stmt.setString(9, cliente.getObservaciones());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en registrar Cliente: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombres = ?, apellidos = ?, direccion = ?, barrio_ciudad = ?, correo = ?, observaciones = ? WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNombres());
            stmt.setString(2, cliente.getApellidos());
            stmt.setString(3, cliente.getDireccion());
            stmt.setString(4, cliente.getBarrioCiudad());
            stmt.setString(5, cliente.getCorreo());
            stmt.setString(6, cliente.getObservaciones());
            stmt.setInt(7, cliente.getIdCliente());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizar Cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean registrarTelefono(ClienteTelefono telefono) {
        String sql = "INSERT INTO cliente_telefonos (id_cliente, numero_telefono, tipo_telefono, es_principal, recibe_notificaciones) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, telefono.getIdCliente());
            stmt.setString(2, telefono.getNumeroTelefono());
            stmt.setString(3, telefono.getTipoTelefono() != null ? telefono.getTipoTelefono() : "WHATSAPP");
            stmt.setBoolean(4, telefono.isEsPrincipal());
            stmt.setBoolean(5, telefono.isRecibeNotificaciones());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en registrarTelefono: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<ClienteTelefono> listarTelefonosPorCliente(int idCliente) {
        List<ClienteTelefono> telefonos = new ArrayList<>();
        String sql = "SELECT * FROM cliente_telefonos WHERE id_cliente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    telefonos.add(new ClienteTelefono(
                        rs.getInt("id_telefono"),
                        rs.getInt("id_cliente"),
                        rs.getString("numero_telefono"),
                        rs.getString("tipo_telefono"),
                        rs.getBoolean("es_principal"),
                        rs.getBoolean("recibe_notificaciones")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarTelefonosPorCliente: " + e.getMessage());
        }
        return telefonos;
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setIdUsuario((Integer) rs.getObject("id_usuario"));
        c.setNombres(rs.getString("nombres"));
        c.setApellidos(rs.getString("apellidos"));
        c.setDireccion(rs.getString("direccion"));
        c.setBarrioCiudad(rs.getString("barrio_ciudad"));
        c.setCorreo(rs.getString("correo"));
        try {
            c.setPasswordHash(rs.getString("password_hash"));
        } catch (SQLException ignored) {}
        c.setCanalOrigen(rs.getString("canal_origen"));
        c.setObservaciones(rs.getString("observaciones"));
        c.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return c;
    }
}
