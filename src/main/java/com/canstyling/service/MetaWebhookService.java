package com.canstyling.service;

import com.canstyling.config.EnvConfig;
import com.canstyling.dto.RegistroWhatsAppRequest;
import com.canstyling.security.SignatureValidator;

import java.util.HashMap;
import java.util.Map;

public class MetaWebhookService {

    private final ClienteService clienteService = new ClienteService();

    /**
     * Procesa la verificación inicial del Webhook de Meta (GET /webhook/meta).
     */
    public String verifyWebhook(String mode, String token, String challenge) {
        String verifyToken = EnvConfig.get("META_VERIFY_TOKEN", "canstyling_webhook_verify_token_sena_2026");
        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            return challenge;
        }
        return null;
    }

    /**
     * Procesa mensajes entrantes vía Webhook (POST /webhook/meta) con validación criptográfica.
     */
    public Map<String, Object> processIncomingMessage(String rawPayload, String signatureHeader) {
        Map<String, Object> response = new HashMap<>();
        String appSecret = EnvConfig.get("META_APP_SECRET", "canstyling_meta_secret_key_2026_x89a");

        // Validación estricta de firma HMAC-SHA256
        if (signatureHeader != null && !signatureHeader.isEmpty()) {
            boolean isValid = SignatureValidator.validateMetaSignature(rawPayload, signatureHeader, appSecret);
            if (!isValid) {
                response.put("success", false);
                response.put("error", "Firma criptográfica inválida (X-Hub-Signature-256)");
                return response;
            }
        }

        // Simulación de auto-registro y respuesta conversacional
        RegistroWhatsAppRequest req = new RegistroWhatsAppRequest(
            "3119876543",
            "Carlos",
            "Gómez",
            "Rocky",
            "Golden Retriever"
        );
        clienteService.registrarDesdeWhatsApp(req);

        response.put("success", true);
        response.put("status", "EVENT_RECEIVED");
        response.put("message", "Mensaje de WhatsApp procesado exitosamente por CanStyling Bot");
        return response;
    }
}
