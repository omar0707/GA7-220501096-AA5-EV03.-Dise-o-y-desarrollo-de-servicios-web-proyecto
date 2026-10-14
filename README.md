# Evidencia GA7-220501096-AA5-EV03: Diseño y Desarrollo de Servicios Web – Proyecto

**Programa de Formación:** Análisis y Desarrollo de Software (ADSO)  
**Ficha:** 3186645  
**Aprendiz:** Omar Ulpiano Molina Malaver  
**Instructor:** Adonay Sánchez Vertel  
**Centro de Formación:** Centro Agropecuario y de Biotecnología El Porvenir (SENA - Regional Córdoba / Montería)  
**Proyecto:** Sistema de Gestión para Peluquería Canina - *CanStyling*  
**Fecha:** Septiembre de 2026  

---

## 1. Descripción de la Evidencia

La presente evidencia representa la **consolidación integral del proyecto de software** para el sistema ***CanStyling***, articulando en una solución completa:
1. **Base de Datos Unificada (MySQL):** Esquema relacional con 17 tablas (`script_peluqueria_canina.sql` de Guía 6 + mejoras de seguridad Guía 7 como Anti-IDOR y OTP).
2. **Servicios Web & API RESTful (Java):** Arquitectura en capas (Controller, Service, DAO, Model, DTO, Security) con soporte para autenticación (Caso SENA), inventario, facturación POS, citas y Webhook de Meta WhatsApp Cloud API con firma `X-Hub-Signature-256`.
3. **Doble Canal de Registro de Clientes:**
   - **Registro Web:** Formulario en la página para auto-registro de propietarios y mascotas.
   - **Registro WhatsApp:** Ingesta de clientes a través de mensajes entrantes del Webhook.
4. **Front-End Interactivo:** Panel administrativo, directorio de clientes, DataTables para citas, control de stock, emisión de facturas y WebApp móvil para estilistas.
5. **Versionamiento Seguro con Git / GitHub:** Configuración estricta de `.gitignore` para proteger `.env` y credenciales sensibles, junto con el manual técnico de publicación en GitHub.

---

## 2. Estructura de Archivos del Proyecto

```
GA7-220501096-AA5-EV03_Diseno_Desarrollo_Servicios_Web_Proyecto/
├── pom.xml                                        # Descriptor Maven (Spring Boot Web, Validation, MySQL)
├── .env.example                                   # Plantilla de variables de entorno segura
├── .env                                           # Configuración local de base de datos
├── .gitignore                                     # Exclusión rigurosa de secretos y binarios
├── GUIA_SUBIR_A_GITHUB.md                         # Manual técnico para subir el proyecto a GitHub
├── DOCUMENTACION_API_REST.md                      # Catálogo y especificación de endpoints REST
├── README.md                                      # Resumen formal de la evidencia (este archivo)
│
├── database/
│   ├── 01_schema_completo_canstyling.sql          # DDL completo con 17 tablas relacionales
│   └── 02_seed_completo.sql                       # Datos de prueba iniciales
│
├── web/
│   └── index.html                                 # Aplicación web completa en React 18 + Tailwind
│
└── src/main/java/com/canstyling/
    ├── Application.java                           # Clase principal Spring Boot
    ├── ProyectoApiTester.java                     # Suite de pruebas y validación en consola
    ├── config/ (DatabaseConnection, EnvConfig)
    ├── controller/ (Auth, Cliente, Cita, Album, Inventario, Factura, MetaWebhook)
    ├── dao/ & dao/impl/ (Interfaces y PreparedStatement JDBC)
    ├── dto/ (ApiResponse, Login, RegistroWeb, RegistroWA, Cita, Factura, Stock, Otp)
    ├── model/ (Usuario, Cliente, Mascota, Servicio, Cita, Inventario, Factura, etc.)
    ├── security/ (SecurityUtils, SignatureValidator HMAC-SHA256)
    └── service/ (Auth, Cliente, Cita, Inventario, Factura, Otp, MetaWebhook, Cloudinary)
```

---

## 3. Instrucciones de Ejecución y Verificación

### 🌐 A. Ejecución de la Aplicación Web (Front-End)
1. Abre en tu navegador web el archivo:  
   👉 [`web/index.html`](file:///d:/proyecto%20sena/guia%207%20adonay/GA7-220501096-AA5-EV03_Diseno_Desarrollo_Servicios_Web_Proyecto/web/index.html)
2. Interactúa con las pestañas de **Dashboard**, **Registro Web**, **Bot WhatsApp**, **Clientes**, **Citas**, **Inventario** y **POS Facturación**.

### 💻 B. Ejecución de las Pruebas Backend (Consola Java)
1. Abre una terminal en este directorio y ejecuta:
   ```powershell
   java -cp target/classes com.canstyling.ProyectoApiTester
   ```
2. Observa la validación exitosa de los 6 módulos de servicios web.

### 🐙 C. Publicación en GitHub
Sigue la guía detallada en [GUIA_SUBIR_A_GITHUB.md](file:///d:/proyecto%20sena/guia%207%20adonay/GA7-220501096-AA5-EV03_Diseno_Desarrollo_Servicios_Web_Proyecto/GUIA_SUBIR_A_GITHUB.md).
