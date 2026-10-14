package com.canstyling.dao;

import com.canstyling.model.Cliente;
import com.canstyling.model.ClienteTelefono;
import java.util.List;
import java.util.Optional;

public interface ClienteDAO {
    List<Cliente> listarTodos();
    Optional<Cliente> buscarPorId(int idCliente);
    Optional<Cliente> buscarPorDocumento(String numeroDocumento);
    Optional<Cliente> buscarPorTelefono(String numeroTelefono);
    int registrar(Cliente cliente);
    boolean actualizar(Cliente cliente);
    boolean registrarTelefono(ClienteTelefono telefono);
    List<ClienteTelefono> listarTelefonosPorCliente(int idCliente);
}
