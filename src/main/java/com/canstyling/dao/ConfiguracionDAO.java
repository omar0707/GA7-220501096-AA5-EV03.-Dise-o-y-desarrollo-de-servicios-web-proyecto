package com.canstyling.dao;

import java.util.Map;

public interface ConfiguracionDAO {
    Map<String, String> obtenerTodas();
    String obtenerPorClave(String clave);
    boolean guardarOActualizar(String clave, String valor, String descripcion);
}
