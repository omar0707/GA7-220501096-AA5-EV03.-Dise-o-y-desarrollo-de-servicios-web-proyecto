package com.canstyling.controller;

import com.canstyling.dto.ApiResponse;
import com.canstyling.service.ConfiguracionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/configuracion")
@CrossOrigin(origins = "*")
public class ConfiguracionController {

    private final ConfiguracionService configuracionService = new ConfiguracionService();

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> obtenerConfiguracion() {
        Map<String, String> cfg = configuracionService.obtenerConfiguracion();
        return ResponseEntity.ok(ApiResponse.ok("Configuración del negocio obtenida exitosamente", cfg));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> guardarConfiguracion(@RequestBody Map<String, String> parametros) {
        boolean ok = configuracionService.guardarConfiguracion(parametros);
        if (ok) {
            Map<String, String> actualizada = configuracionService.obtenerConfiguracion();
            return ResponseEntity.ok(ApiResponse.ok("Configuración actualizada correctamente en MySQL", actualizada));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("Error al actualizar la configuración en MySQL"));
    }
}
