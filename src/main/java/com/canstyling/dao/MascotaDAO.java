package com.canstyling.dao;

import com.canstyling.model.Mascota;
import java.util.List;
import java.util.Optional;

public interface MascotaDAO {
    List<Mascota> listarPorCliente(int idCliente);
    Optional<Mascota> buscarPorId(int idMascota);
    int registrar(Mascota mascota);
    boolean actualizar(Mascota mascota);
}
