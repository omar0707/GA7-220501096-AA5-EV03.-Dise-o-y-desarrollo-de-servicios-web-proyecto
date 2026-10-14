package com.canstyling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase Principal de Arranque del Sistema CanStyling - Servicios Web y API RESTful.
 * Guía de Aprendizaje 7 - Evidencia GA7-220501096-AA5-EV03.
 *
 * @author Omar Ulpiano Molina Malaver
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("🚀 INICIANDO CANSTYLING API REST & SERVICIOS WEB - PROYECTO SENA");
        System.out.println("📦 Guía 7 - Evidencia GA7-220501096-AA5-EV03");
        System.out.println("🐶 Ecosistema Integral: Base de Datos MySQL + Webhooks + Anti-IDOR");
        System.out.println("==================================================================");

        SpringApplication.run(Application.class, args);
    }
}
