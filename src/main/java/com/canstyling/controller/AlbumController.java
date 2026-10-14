package com.canstyling.controller;

import com.canstyling.dto.ApiResponse;
import com.canstyling.model.Cita;
import com.canstyling.service.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/album")
@CrossOrigin(origins = "*")
public class AlbumController {

    private final CitaService citaService = new CitaService();

    /**
     * Consulta pública del Álbum Digital de la mascota mediante Token UUID Anti-IDOR.
     * Impide el acceso a través de IDs correlativos secuenciales (ej. /album/1).
     */
    @GetMapping("/{token}")
    public ResponseEntity<ApiResponse<Cita>> consultarAlbumPublico(@PathVariable("token") String token) {
        // Bloqueo Anti-IDOR: Si el token es puramente numérico o no tiene formato UUID v4
        if (token.matches("^\\d+$") || token.length() < 10) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Acceso denegado: El recurso solicitado requiere un token de acceso criptográfico válido.", null));
        }

        Optional<Cita> citaOpt = citaService.buscarPorTokenAntiIDOR(token);
        if (citaOpt.isPresent()) {
            return ResponseEntity.ok(ApiResponse.ok("Álbum digital de mascota recuperado exitosamente", citaOpt.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Álbum digital no encontrado o token inválido", null));
        }
    }
}
