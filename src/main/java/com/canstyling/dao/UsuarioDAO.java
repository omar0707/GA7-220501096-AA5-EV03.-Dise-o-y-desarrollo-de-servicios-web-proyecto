package com.canstyling.dao;

import com.canstyling.model.Usuario;
import java.util.Optional;

public interface UsuarioDAO {
    Optional<Usuario> buscarPorCorreo(String correo);
    Optional<Usuario> buscarPorId(int idUsuario);
    int registrar(Usuario usuario);
}
