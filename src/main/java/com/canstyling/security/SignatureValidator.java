package com.canstyling.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SignatureValidator {

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * Valida la firma criptográfica X-Hub-Signature-256 enviada por Meta Cloud API en Webhooks.
     */
    public static boolean validateMetaSignature(String payload, String headerSignature, String appSecret) {
        if (headerSignature == null || !headerSignature.startsWith("sha256=")) {
            return false;
        }

        String expectedSignature = headerSignature.substring(7);

        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKeySpec);

            byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hmacBytes) {
                sb.append(String.format("%02x", b));
            }
            String computedSignature = sb.toString();

            return computedSignature.equalsIgnoreCase(expectedSignature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            System.err.println("Error validando firma HMAC: " + e.getMessage());
            return false;
        }
    }
}
