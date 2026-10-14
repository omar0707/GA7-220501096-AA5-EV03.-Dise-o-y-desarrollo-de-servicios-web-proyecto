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




## Instrucciones de Despliegue y Pruebas (Local y GitHub Codespaces)

### 1. Preparación del Entorno y Base de Datos

1. Copiar el archivo de variables de entorno base:
   cp .env.example .env

2. Iniciar el servicio de base de datos MySQL e importar los esquemas requeridos:
   sudo service mysql start
   sudo mysql < database/01_schema_completo_canstyling.sql
   sudo mysql -e "USE peluqueria_canina_db; ALTER TABLE usuarios MODIFY COLUMN canal_registro VARCHAR(50);"
   sudo mysql < database/02_seed_completo.sql

### 2. Ejecución del Servidor Backend

Compilar el proyecto e iniciar la aplicación con Maven:
   mvn clean spring-boot:run

### 3. Consideraciones Críticas de Enrutamiento y Acceso Web

El backend implementa un context-path a nivel de servlet configurado en el prefijo /api (server.servlet.context-path=/api).

Debido a esta configuración:
* Cualquier intento de acceso a la raíz (http://localhost:8080/ o http://localhost:8080/index.html) resultará en un error HTTP 404 - Not Found.
* Para visualizar correctamente el frontend y los servicios web, es mandatorio acceder incluyendo el prefijo del contexto:

* URL Local:
  http://localhost:8080/api/ (o http://localhost:8080/api/index.html)

* URL en GitHub Codespaces:
  Configurar el puerto 8080 con visibilidad Pública (Public) en la pestaña de Puertos de VS Code y acceder a la URL asignada añadiendo la ruta del servlet:
  https://<id-codespace>-8080.app.github.dev/api/

### 4. Funcionalidades del Sistema y Pruebas Disponibles

Desde la interfaz web es posible probar los módulos y servicios web del aplicativo:
* Registro y administración de clientes (nombre, teléfono, correo, canal de registro).
* Registro de mascotas asociadas a clientes.
* Agendamiento y gestión de citas para servicios de peluquería y estética canina.
* Módulos de facturación, inventario y catálogo de servicios.

### 5. Credenciales de Acceso para Pruebas

Para facilitar la evaluación, en la parte inferior (pie de página / footer) de la pantalla principal de inicio (Home) se encuentran listadas las cuentas de prueba disponibles con sus respectivos roles y accesos directos:

* Usuario Administrador: admin@canstyling.com / Contraseña: Admin123*
* Usuario Recepción: recepcion@canstyling.com / Contraseña: Admin123*
* Usuario Estilista: mateo.estilista@canstyling.com / Contraseña: Admin123*
* Usuario Cliente: laura.martinez@gmail.com / Contraseña: Admin123*
