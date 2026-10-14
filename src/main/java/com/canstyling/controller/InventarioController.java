package com.canstyling.controller;

import com.canstyling.dto.ActualizarStockRequest;
import com.canstyling.dto.ApiResponse;
import com.canstyling.model.InventarioInsumo;
import com.canstyling.service.InventarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/inventario")
@CrossOrigin(origins = "*")
public class InventarioController {

    private final InventarioService inventarioService = new InventarioService();

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventarioInsumo>>> listarInventario() {
        List<InventarioInsumo> lista = inventarioService.listarInventario();
        return ResponseEntity.ok(ApiResponse.ok("Inventario obtenido exitosamente", lista));
    }

    @GetMapping("/alertas")
    public ResponseEntity<ApiResponse<List<InventarioInsumo>>> listarAlertasStockBajo() {
        List<InventarioInsumo> alertas = inventarioService.listarAlertasStock();
        return ResponseEntity.ok(ApiResponse.ok("Alertas de stock bajo obtenidas", alertas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventarioInsumo>> obtenerPorId(@PathVariable("id") int id) {
        Optional<InventarioInsumo> opt = inventarioService.buscarPorId(id);
        return opt.map(insumo -> ResponseEntity.ok(ApiResponse.ok("Producto encontrado", insumo)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Producto no encontrado con ID: " + id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> agregarProducto(@RequestBody InventarioInsumo insumo) {
        Map<String, Object> res = inventarioService.registrarProducto(insumo);
        boolean success = (boolean) res.get("success");
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok((String) res.get("message"), res));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error((String) res.get("message")));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ajustarStock(
            @PathVariable("id") int id,
            @RequestBody ActualizarStockRequest req) {
        String motivo = req.getMotivo() != null ? req.getMotivo() : "Ajuste manual";
        Map<String, Object> res = inventarioService.ajustarStock(id, req.getCantidadAjuste(), motivo);
        boolean success = (boolean) res.get("success");
        if (success) {
            return ResponseEntity.ok(ApiResponse.ok((String) res.get("message"), res));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error((String) res.get("message")));
    }
}
