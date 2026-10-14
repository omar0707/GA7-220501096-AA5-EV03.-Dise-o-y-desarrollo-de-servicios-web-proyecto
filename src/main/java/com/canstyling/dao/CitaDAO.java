package com.canstyling.dao;

import com.canstyling.model.Cita;
import com.canstyling.model.FotoMascota;

import java.util.List;
import java.util.Optional;

public interface CitaDAO {
    List<Cita> listarTodas(String estado);
    Optional<Cita> buscarPorId(int idCita);
    Optional<Cita> buscarPorToken(String tokenAcceso);
    int agendar(Cita cita);
    boolean cambiarEstado(int idCita, String nuevoEstado);
    boolean actualizarEstadoYNotas(int idCita, String nuevoEstado, String notas);
    boolean registrarFoto(FotoMascota foto);
    List<FotoMascota> listarFotosPorCita(int idCita);
    boolean eliminarFoto(int idFoto);
    boolean eliminarFotosPorCita(int idCita);
    int contarCitasActivasPorCliente(int idCliente);
    int contarCitasActivasPorMascota(int idMascota);
}

