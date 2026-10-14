package com.canstyling.service;

import com.canstyling.dao.ConfiguracionDAO;
import com.canstyling.dao.impl.ConfiguracionDAOImpl;

import java.util.Map;

public class ConfiguracionService {

    private final ConfiguracionDAO configuracionDAO = new ConfiguracionDAOImpl();

    public Map<String, String> obtenerConfiguracion() {
        Map<String, String> cfg = configuracionDAO.obtenerTodas();
        // Valores por defecto seguros si la tabla está vacía
        cfg.putIfAbsent("horario_apertura", "08:00");
        cfg.putIfAbsent("horario_cierre", "17:00");
        cfg.putIfAbsent("dias_cerrados", "0"); // 0 = Domingo
        cfg.putIfAbsent("capacidad_estilistas", "3");
        cfg.putIfAbsent("politica_cancelacion_horas", "24");
        cfg.putIfAbsent("duraciones_razas", "{\"Criollo / Mestizo\":120,\"Samoyedo\":180,\"Gato / Felino\":60,\"Poodle\":120,\"Golden Retriever\":150,\"Bulldog Frances\":90,\"Schnauzer Miniatura\":120}");
        return cfg;
    }

    public boolean guardarConfiguracion(Map<String, String> parametros) {
        boolean exito = true;
        for (Map.Entry<String, String> entry : parametros.entrySet()) {
            boolean ok = configuracionDAO.guardarOActualizar(entry.getKey(), entry.getValue(), null);
            if (!ok) exito = false;
        }
        return exito;
    }
}
