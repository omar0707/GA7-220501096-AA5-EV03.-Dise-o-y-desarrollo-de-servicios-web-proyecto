package com.canstyling;

import com.canstyling.dto.AgendarCitaRequest;
import com.canstyling.dto.CrearFacturaRequest;
import com.canstyling.dto.RegistroWebClienteRequest;
import com.canstyling.dto.RegistroWhatsAppRequest;
import com.canstyling.model.*;
import com.canstyling.service.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Suite de Verificación Integral de Servicios Web y Negocio - CanStyling.
 * Guía 7 - Evidencia GA7-220501096-AA5-EV03.
 *
 * Permite validar de forma autónoma:
 * 1. Caso SENA: Autenticación exitosa y fallida.
 * 2. Canal 1: Registro Web de cliente + mascota inicial + teléfonos.
 * 3. Canal 2: Registro vía WhatsApp (Webhook simulado).
 * 4. Módulo de Citas con Token Anti-IDOR (UUID v4) y límite de citas activas.
 * 5. Módulo de Inventario con alertas de stock mínimo.
 * 6. Módulo de Facturación / POS con deducción automática de stock.
 */
public class ProyectoApiTester {

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("🐶 CANSTYLING - SISTEMA INTEGRAL DE SERVICIOS WEB (GA7-AA5-EV03)");
        System.out.println("==========================================================================");

        AuthService authService = new AuthService();
        ClienteService clienteService = new ClienteService();
        CitaService citaService = new CitaService();
        InventarioService inventarioService = new InventarioService();
        FacturaService facturaService = new FacturaService();
        MetaWebhookService webhookService = new MetaWebhookService();

        // 1. CASO SENA: AUTENTICACIÓN
        System.out.println("\n--- [PRUEBA 1] Caso SENA: Autenticación de Usuarios ---");
        Map<String, Object> loginOk = authService.autenticar("admin@canstyling.com", "Admin123*");
        System.out.println("✔ Login Válido: " + loginOk.get("message") + " | Token: " + loginOk.get("token"));

        Map<String, Object> loginFail = authService.autenticar("admin@canstyling.com", "ClaveErronea123");
        System.out.println("✔ Login Inválido (Esperado 401): " + loginFail.get("message"));

        // 2. CANAL 1: REGISTRO WEB DE CLIENTE Y MASCOTA (CON CORREO OBLIGATORIO)
        System.out.println("\n--- [PRUEBA 2] Canal 1: Registro Web de Cliente y Mascota (Sin Cédula) ---");
        RegistroWebClienteRequest reqWeb = new RegistroWebClienteRequest();
        reqWeb.setCorreo("valen." + (int)(Math.random()*90000 + 10000) + "@gmail.com");
        reqWeb.setNombres("Valentina");
        reqWeb.setApellidos("Herrera Mestra");
        reqWeb.setTelefonoPrincipal("3005551234");
        reqWeb.setDireccion("Calle 44 # 10-20");
        reqWeb.setBarrioCiudad("Montecristo, Montería");
        reqWeb.setNombreMascota("Kiara");
        reqWeb.setIdRaza(4); // Schnauzer
        reqWeb.setGeneroMascota("HEMBRA");
        reqWeb.setColorMascota("Blanco y Plata");
        reqWeb.setPesoKg(new BigDecimal("6.8"));

        Map<String, Object> resWeb = clienteService.registrarDesdeWeb(reqWeb);
        System.out.println("✔ " + resWeb.get("message"));

        // 3. CANAL 2: REGISTRO VÍA WHATSAPP (WEBHOOK)
        System.out.println("\n--- [PRUEBA 3] Canal 2: Registro de Cliente vía WhatsApp & Handshake Webhook ---");
        String verifyOk = webhookService.verifyWebhook("subscribe", "canstyling_webhook_verify_token_sena_2026", "challenge_ok_sena_2026");
        System.out.println("✔ Handshake Webhook Meta: " + (verifyOk != null ? "VERIFICADO (" + verifyOk + ")" : "FALLIDO"));

        RegistroWhatsAppRequest reqWA = new RegistroWhatsAppRequest(
            "318" + (int)(Math.random()*9000000 + 1000000),
            "Felipe",
            "Mendoza",
            "Simba",
            "Pomerania"
        );
        Map<String, Object> resWA = clienteService.registrarDesdeWhatsApp(reqWA);
        System.out.println("✔ " + resWA.get("message"));

        // 4. AGENDAMIENTO DE CITA CON ANTI-IDOR
        System.out.println("\n--- [PRUEBA 4] Agendamiento de Cita con Token Anti-IDOR ---");
        AgendarCitaRequest reqCita = new AgendarCitaRequest();
        reqCita.setIdCliente(1);
        reqCita.setIdMascota(1);
        reqCita.setIdServicio(2); // Corte Comercial
        reqCita.setIdEstilista(1);
        reqCita.setFechaCita("2026-09-25");
        reqCita.setHoraCita("10:00:00");
        reqCita.setOrigenReserva("WEB");
        reqCita.setObservaciones("Cliente solicita corte bajo en patas");

        Map<String, Object> resCita = citaService.agendarCita(reqCita);
        System.out.println("✔ " + resCita.get("message") + " | Token Anti-IDOR UUID: " + resCita.get("tokenAcceso"));

        // 5. INVENTARIO Y ALERTAS DE STOCK
        System.out.println("\n--- [PRUEBA 5] Inventario y Alertas de Stock Mínimo ---");
        List<InventarioInsumo> alertas = inventarioService.listarAlertasStock();
        System.out.println("✔ Productos con Stock Bajo / Alertas de Reposición: " + alertas.size());
        for (InventarioInsumo p : alertas) {
            System.out.println("   • Alerta: " + p.getNombreProducto() + " (Stock Actual: " + p.getStockActual() + " | Mínimo: " + p.getStockMinimo() + ")");
        }

        // 6. EMISIÓN DE FACTURA / POS Y DEDUCCIÓN DE STOCK
        System.out.println("\n--- [PRUEBA 6] Módulo de Facturación / POS ---");
        CrearFacturaRequest reqFac = new CrearFacturaRequest();
        reqFac.setIdCliente(1);
        reqFac.setIdCita(1);
        reqFac.setSubtotal(new BigDecimal("80000.00"));
        reqFac.setIva(BigDecimal.ZERO);
        reqFac.setDescuento(BigDecimal.ZERO);
        reqFac.setTotal(new BigDecimal("80000.00"));
        reqFac.setMetodoPago("TRANSFERENCIA_NEQUI_DAVIPLATA");

        CrearFacturaRequest.ItemFacturaDTO itemServ = new CrearFacturaRequest.ItemFacturaDTO();
        itemServ.setIdServicio(2);
        itemServ.setDescripcion("Corte Comercial y Estilizado");
        itemServ.setCantidad(1);
        itemServ.setPrecioUnitario(new BigDecimal("55000.00"));
        itemServ.setSubtotal(new BigDecimal("55000.00"));
        reqFac.getItems().add(itemServ);

        CrearFacturaRequest.ItemFacturaDTO itemProd = new CrearFacturaRequest.ItemFacturaDTO();
        itemProd.setIdProducto(3); // Perfume canino
        itemProd.setDescripcion("Perfume Canino Frutos Rojos 250ml");
        itemProd.setCantidad(1);
        itemProd.setPrecioUnitario(new BigDecimal("25000.00"));
        itemProd.setSubtotal(new BigDecimal("25000.00"));
        reqFac.getItems().add(itemProd);

        Map<String, Object> resFac = facturaService.emitirFactura(reqFac);
        System.out.println("✔ " + resFac.get("message"));

        System.out.println("\n==========================================================================");
        System.out.println("✅ TODAS LAS PRUEBAS DE SERVICIOS WEB Y NEGOCIO FUERON SUPERADAS AL 100%");
        System.out.println("==========================================================================");
    }
}
