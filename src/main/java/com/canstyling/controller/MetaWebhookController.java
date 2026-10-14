package com.canstyling.controller;

import com.canstyling.service.MetaWebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook/meta")
@CrossOrigin(origins = "*")
public class MetaWebhookController {

    private final MetaWebhookService metaWebhookService = new MetaWebhookService();

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        String result = metaWebhookService.verifyWebhook(mode, token, challenge);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error de verificación del Webhook de Meta");
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> handleIncomingEvent(
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody String rawPayload) {

        Map<String, Object> result = metaWebhookService.processIncomingMessage(rawPayload, signature);
        if (result.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        return ResponseEntity.ok(result);
    }
}
