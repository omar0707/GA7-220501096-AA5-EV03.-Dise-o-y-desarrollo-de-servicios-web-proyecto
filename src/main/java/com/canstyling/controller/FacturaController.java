package com.canstyling.controller;

import com.canstyling.dto.ApiResponse;
import com.canstyling.dto.CrearFacturaRequest;
import com.canstyling.model.Factura;
import com.canstyling.service.FacturaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    private final FacturaService facturaService = new FacturaService();

    @GetMapping
    public ResponseEntity<ApiResponse<List<Factura>>> listarFacturas() {
        List<Factura> facturas = facturaService.listarFacturas();
        return ResponseEntity.ok(ApiResponse.ok("Listado de facturas obtenido", facturas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Factura>> obtenerPorId(@PathVariable("id") int id) {
        Optional<Factura> opt = facturaService.buscarPorId(id);
        return opt.map(factura -> ResponseEntity.ok(ApiResponse.ok("Factura encontrada", factura)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Factura no encontrada con ID: " + id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> emitirFactura(@RequestBody CrearFacturaRequest req) {
        Map<String, Object> res = facturaService.emitirFactura(req);
        boolean success = (boolean) res.get("success");
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok((String) res.get("message"), res));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error((String) res.get("message")));
    }
}
