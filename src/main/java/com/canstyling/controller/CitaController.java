package com.canstyling.controller;

import com.canstyling.dto.AgendarCitaRequest;
import com.canstyling.dto.ApiResponse;
import com.canstyling.model.Cita;
import com.canstyling.service.CitaService;
import com.canstyling.service.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    private final CitaService citaService = new CitaService();
    private final CloudinaryService cloudinaryService = new CloudinaryService();

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cita>>> listarCitas(@RequestParam(name = "estado", required = false) String estado) {
        List<Cita> citas = citaService.listarCitas(estado);
        return ResponseEntity.ok(ApiResponse.ok("Listado de citas obtenido", citas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cita>> obtenerCitaPorId(@PathVariable("id") int id) {
        Optional<Cita> opt = citaService.buscarPorId(id);
        return opt.map(cita -> ResponseEntity.ok(ApiResponse.ok("Cita encontrada", cita)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Cita no encontrada con ID: " + id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> agendarCita(@RequestBody AgendarCitaRequest req) {
        Map<String, Object> res = citaService.agendarCita(req);
        boolean success = (boolean) res.get("success");
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok((String) res.get("message"), res));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error((String) res.get("message")));
    }

    @PutMapping("/{id}/completar")
    public ResponseEntity<ApiResponse<Void>> completarCita(@PathVariable("id") int id) {
        boolean ok = citaService.completarCita(id);
        if (ok) {
            return ResponseEntity.ok(ApiResponse.ok("Cita marcada como COMPLETADA. Notificación saliente a WhatsApp disparada."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("No se pudo completar la cita"));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(
            @PathVariable("id") int id,
            @RequestBody Map<String, String> body) {
        String nuevoEstado = body.getOrDefault("estado", "PENDIENTE");
        String notas = body.get("notas");
        boolean ok = citaService.actualizarEstadoYNotas(id, nuevoEstado, notas);
        if (ok) {
            return ResponseEntity.ok(ApiResponse.ok("Estado de la cita #" + id + " actualizado a " + nuevoEstado + " en MySQL."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("No se pudo actualizar el estado de la cita en MySQL"));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<Void>> cancelarCita(
            @PathVariable("id") int id,
            @RequestParam(name = "esAdmin", defaultValue = "false") boolean esAdmin) {
        Map<String, Object> res = citaService.cancelarCitaConPolitica(id, esAdmin);
        boolean success = (boolean) res.get("success");
        if (success) {
            return ResponseEntity.ok(ApiResponse.ok((String) res.get("message")));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error((String) res.get("message")));
    }


    @PostMapping("/{id}/fotos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> subirFoto(
            @PathVariable("id") int idCita,
            @RequestBody Map<String, Object> body) {
        String tipoFoto = (String) body.getOrDefault("tipoFoto", "ANTES");
        String mimeType = (String) body.getOrDefault("mimeType", "image/jpeg");
        long fileSize = Long.parseLong(String.valueOf(body.getOrDefault("fileSize", "1048576")));
        String urlFoto = (String) body.getOrDefault("urlFoto", "https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=600");

        Map<String, Object> res = cloudinaryService.subirFotoTransformacion(idCita, tipoFoto, mimeType, fileSize, urlFoto);
        boolean success = (boolean) res.get("success");
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok((String) res.get("message"), res));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error((String) res.get("message")));
    }

    @DeleteMapping("/{id}/fotos/{idFoto}")
    public ResponseEntity<ApiResponse<Void>> eliminarFoto(
            @PathVariable("id") int idCita,
            @PathVariable("idFoto") int idFoto) {
        boolean ok = citaService.eliminarFoto(idFoto);
        if (ok) {
            return ResponseEntity.ok(ApiResponse.ok("Fotografía eliminada correctamente"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("No se pudo eliminar la fotografía"));
    }

    @DeleteMapping("/{id}/fotos")
    public ResponseEntity<ApiResponse<Void>> eliminarTodasLasFotos(@PathVariable("id") int idCita) {
        boolean ok = citaService.eliminarTodasLasFotos(idCita);
        if (ok) {
            return ResponseEntity.ok(ApiResponse.ok("Todas las fotografías del turno han sido eliminadas"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("No se pudieron eliminar las fotografías"));
    }
}
