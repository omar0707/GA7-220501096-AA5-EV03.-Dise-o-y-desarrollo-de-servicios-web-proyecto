    package com.canstyling.controller;

import com.canstyling.dto.ApiResponse;
import com.canstyling.dto.RegistroWebClienteRequest;
import com.canstyling.dto.RegistroWhatsAppRequest;
import com.canstyling.model.Cliente;
import com.canstyling.model.Mascota;
import com.canstyling.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService = new ClienteService();

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cliente>>> listarClientes() {
        List<Cliente> clientes = clienteService.listarClientesConMascotas();
        return ResponseEntity.ok(ApiResponse.ok("Listado de clientes obtenido exitosamente", clientes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> obtenerPorId(@PathVariable("id") int id) {
        Optional<Cliente> opt = clienteService.obtenerClientePorId(id);
        return opt.map(cliente -> ResponseEntity.ok(ApiResponse.ok("Cliente encontrado", cliente)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Cliente no encontrado con ID: " + id)));
    }

    /**
     * Canal 1: Registro de cliente y mascota desde el Front-End / Web.
     */
    @PostMapping("/registro-web")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registroWeb(@RequestBody RegistroWebClienteRequest req) {
        Map<String, Object> res = clienteService.registrarDesdeWeb(req);
        boolean success = (boolean) res.get("success");
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok((String) res.get("message"), res));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error((String) res.get("message")));
    }

    /**
     * Canal 2: Registro de cliente vía WhatsApp (Chatbot / Webhook).
     */
    @PostMapping("/registro-whatsapp")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registroWhatsApp(@RequestBody RegistroWhatsAppRequest req) {
        Map<String, Object> res = clienteService.registrarDesdeWhatsApp(req);
        boolean success = (boolean) res.get("success");
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok((String) res.get("message"), res));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error((String) res.get("message")));
    }

    @PostMapping("/{id}/mascotas")
    public ResponseEntity<ApiResponse<Mascota>> agregarMascota(@PathVariable("id") int idCliente, @RequestBody Mascota mascota) {
        mascota.setIdCliente(idCliente);
        int idMascota = clienteService.registrarMascota(mascota);
        if (idMascota > 0) {
            mascota.setIdMascota(idMascota);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Mascota registrada y asociada al cliente exitosamente", mascota));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Error al registrar mascota"));
    }

    @PutMapping("/{id}/mascotas/{idMascota}")
    public ResponseEntity<ApiResponse<Mascota>> actualizarMascota(
            @PathVariable("id") int idCliente,
            @PathVariable("idMascota") int idMascota,
            @RequestBody Mascota mascota) {
        mascota.setIdCliente(idCliente);
        mascota.setIdMascota(idMascota);
        boolean actualizado = clienteService.actualizarMascota(mascota);
        if (actualizado) {
            return ResponseEntity.ok(ApiResponse.ok("Ficha de características de la mascota actualizada en MySQL", mascota));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("No se pudo actualizar la mascota"));
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> eliminarCliente(@PathVariable("id") int id) {
        try {
            boolean eliminado = clienteService.eliminarCliente(id);
            if (eliminado) {
                return ResponseEntity.ok(ApiResponse.ok("Cliente eliminado exitosamente", "ID: " + id));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("No se encontró el cliente con ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al eliminar cliente: " + e.getMessage()));
        }
    }
    
    
}
