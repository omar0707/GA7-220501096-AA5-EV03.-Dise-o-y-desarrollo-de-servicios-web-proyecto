-- =========================================================================================
-- SISTEMA DE GESTION INTEGRAL PARA PELUQUERIA CANINA: CANSTYLING
-- Base de Datos Unificada y Segura (MySQL 8.0 / MariaDB)
-- Guia de Aprendizaje 07 - Evidencia GA7-220501096-AA5-EV03
--
-- Aprendiz: Omar Ulpiano Molina Malaver | Ficha: 3186645
-- Instructor: Adonay Sanchez Vertel
-- Centro Agropecuario y de Biotecnologia El Porvenir (SENA Regional Cordoba)
-- =========================================================================================

DROP DATABASE IF EXISTS peluqueria_canina_db;
CREATE DATABASE peluqueria_canina_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE peluqueria_canina_db;

-- -----------------------------------------------------------------------------------------
-- 1. TABLA: ROLES DE USUARIO
-- -----------------------------------------------------------------------------------------
CREATE TABLE roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE COMMENT 'ADMINISTRADOR, RECEPCIONISTA, ESTILISTA, CLIENTE',
    descripcion VARCHAR(255) NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Roles y permisos del sistema';

-- -----------------------------------------------------------------------------------------
-- 2. TABLA: USUARIOS (ACCESO AL SISTEMA Y AUTENTICACION)
-- -----------------------------------------------------------------------------------------
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(20) NULL,
    canal_registro ENUM('WEB', 'WHATSAPP', 'ADMIN', 'SISTEMA') DEFAULT 'WEB',
    estado ENUM('ACTIVO', 'INACTIVO', 'SUSPENDIDO') DEFAULT 'ACTIVO',
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuarios_roles FOREIGN KEY (id_rol) 
        REFERENCES roles(id_rol) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Usuarios registrados en el sistema';

-- -----------------------------------------------------------------------------------------
-- 3. TABLA: CLIENTES (PROPIETARIOS DE MASCOTAS - IDENTIFICACION POR CORREO OBLIGATORIO)
-- -----------------------------------------------------------------------------------------
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NULL UNIQUE,
    correo VARCHAR(150) NOT NULL UNIQUE COMMENT 'Identificador unico y obligatorio del tutor canino',
    password_hash VARCHAR(255) NULL COMMENT 'Hash de seguridad para autenticacion directa del cliente',
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NULL,
    barrio_ciudad VARCHAR(100) NULL,
    canal_origen ENUM('WEB', 'WHATSAPP', 'PRESENCIAL') DEFAULT 'WEB',
    observaciones TEXT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_clientes_usuarios FOREIGN KEY (id_usuario) 
        REFERENCES usuarios(id_usuario) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Informacion de los propietarios de mascotas';

-- -----------------------------------------------------------------------------------------
-- 4. TABLA: TELEFONOS DE CLIENTES (SOPORTE MULTI-NUMERO)
-- -----------------------------------------------------------------------------------------
CREATE TABLE cliente_telefonos (
    id_telefono INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    numero_telefono VARCHAR(20) NOT NULL,
    tipo_telefono ENUM('WHATSAPP', 'MOVIL', 'FIJO', 'TRABAJO') DEFAULT 'WHATSAPP',
    es_principal BOOLEAN DEFAULT FALSE,
    recibe_notificaciones BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_telefonos_clientes FOREIGN KEY (id_cliente) 
        REFERENCES clientes(id_cliente) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Telefonos de contacto con soporte multi-numero';

-- -----------------------------------------------------------------------------------------
-- 5. TABLA: RAZAS CANINAS
-- -----------------------------------------------------------------------------------------
CREATE TABLE razas (
    id_raza INT AUTO_INCREMENT PRIMARY KEY,
    nombre_raza VARCHAR(100) NOT NULL UNIQUE,
    tamano ENUM('MINI', 'PEQUENO', 'MEDIANO', 'GRANDE', 'GIGANTE') NOT NULL,
    tipo_pelo ENUM('CORTO', 'MEDIO', 'LARGO', 'RIZADO', 'DOBLE_CAPA') NOT NULL,
    descripcion TEXT NULL
) ENGINE=InnoDB COMMENT='Catalogo de razas caninas y caracteristicas fisicas';

-- -----------------------------------------------------------------------------------------
-- 6. TABLA: MASCOTAS (CLIENTES CANINOS)
-- -----------------------------------------------------------------------------------------
CREATE TABLE mascotas (
    id_mascota INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_raza INT NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    genero ENUM('MACHO', 'HEMBRA') NOT NULL,
    color VARCHAR(60) NULL,
    edad_aproximada INT NULL COMMENT 'Edad en meses',
    peso_kg DECIMAL(5,2) NULL,
    temperamento ENUM('DOCIL', 'NERVIOSO', 'AGRESIVO', 'TIMIDO', 'HIPERACTIVO') DEFAULT 'DOCIL',
    alergias_condiciones TEXT NULL,
    vacunas_al_dia BOOLEAN DEFAULT TRUE,
    foto_perfil_url VARCHAR(255) NULL,
    estado ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO',
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mascotas_clientes FOREIGN KEY (id_cliente) 
        REFERENCES clientes(id_cliente) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_mascotas_razas FOREIGN KEY (id_raza) 
        REFERENCES razas(id_raza) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Registro individual de mascotas';

-- -----------------------------------------------------------------------------------------
-- 7. TABLA: CATEGORIAS DE SERVICIOS
-- -----------------------------------------------------------------------------------------
CREATE TABLE categorias_servicios (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NULL
) ENGINE=InnoDB COMMENT='Clasificacion de servicios de peluqueria y estetica';

-- -----------------------------------------------------------------------------------------
-- 8. TABLA: SERVICIOS OFRECIDOS
-- -----------------------------------------------------------------------------------------
CREATE TABLE servicios (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    id_categoria INT NOT NULL,
    nombre_servicio VARCHAR(120) NOT NULL,
    descripcion TEXT NULL,
    precio_base DECIMAL(10,2) NOT NULL,
    duracion_estimada_minutos INT NOT NULL DEFAULT 45,
    estado ENUM('DISPONIBLE', 'DESCONTINUADO') DEFAULT 'DISPONIBLE',
    CONSTRAINT fk_servicios_categorias FOREIGN KEY (id_categoria) 
        REFERENCES categorias_servicios(id_categoria) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Catalogo de servicios y tarifas';

-- -----------------------------------------------------------------------------------------
-- 9. TABLA: ESTILISTAS Y PELUQUEROS CANINOS
-- -----------------------------------------------------------------------------------------
CREATE TABLE estilistas (
    id_estilista INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL UNIQUE,
    especialidad VARCHAR(120) NULL COMMENT 'Corte con tijera, razas gigantes, stripping',
    horario_inicio TIME NOT NULL DEFAULT '08:00:00',
    horario_fin TIME NOT NULL DEFAULT '18:00:00',
    dias_laborales VARCHAR(100) DEFAULT 'Lunes a Sabado',
    estado ENUM('DISPONIBLE', 'EN_TURNO', 'VACACIONES', 'INACTIVO') DEFAULT 'DISPONIBLE',
    CONSTRAINT fk_estilistas_usuarios FOREIGN KEY (id_usuario) 
        REFERENCES usuarios(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Personal estilista encargado del cuidado y corte';

-- -----------------------------------------------------------------------------------------
-- 10. TABLA: CITAS Y TURNOS DE ATENCION (CON SEGURIDAD ANTI-IDOR)
-- -----------------------------------------------------------------------------------------
CREATE TABLE citas (
    id_cita INT AUTO_INCREMENT PRIMARY KEY,
    token_acceso VARCHAR(64) NOT NULL UNIQUE COMMENT 'Token UUID v4 criptografico Anti-IDOR para album publico',
    id_cliente INT NOT NULL,
    id_mascota INT NOT NULL,
    id_servicio INT NOT NULL,
    id_estilista INT NULL,
    fecha_cita DATE NOT NULL,
    hora_cita TIME NOT NULL,
    duracion_minutos INT NOT NULL DEFAULT 60,
    estado_cita ENUM('PENDIENTE', 'CONFIRMADA', 'EN_PROCESO', 'PENDIENTE_APROBACION', 'COMPLETADA', 'CANCELADA', 'NO_ASISTIO') DEFAULT 'PENDIENTE',
    origen_reserva ENUM('WEB', 'WHATSAPP', 'PRESENCIAL', 'TELEFONICO') DEFAULT 'WEB',
    observaciones_cliente TEXT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_citas_clientes FOREIGN KEY (id_cliente) 
        REFERENCES clientes(id_cliente) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_citas_mascotas FOREIGN KEY (id_mascota) 
        REFERENCES mascotas(id_mascota) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_citas_servicios FOREIGN KEY (id_servicio) 
        REFERENCES servicios(id_servicio) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_citas_estilistas FOREIGN KEY (id_estilista) 
        REFERENCES estilistas(id_estilista) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Gestion de turnos con proteccion Anti-IDOR';

-- -----------------------------------------------------------------------------------------
-- 11. TABLA: HISTORIAL DE ATENCIONES POST-SERVICIO
-- -----------------------------------------------------------------------------------------
CREATE TABLE historial_atenciones (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    id_cita INT NOT NULL UNIQUE,
    id_mascota INT NOT NULL,
    id_estilista INT NOT NULL,
    procedimiento_realizado TEXT NOT NULL,
    productos_aplicados VARCHAR(255) NULL,
    novedades_salud TEXT NULL COMMENT 'Pulgas, nudos severos, otitis, problemas dermicos',
    recomendaciones_tutor TEXT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historial_citas FOREIGN KEY (id_cita) 
        REFERENCES citas(id_cita) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_historial_mascotas FOREIGN KEY (id_mascota) 
        REFERENCES mascotas(id_mascota) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_historial_estilistas FOREIGN KEY (id_estilista) 
        REFERENCES estilistas(id_estilista) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Seguimiento veterinario-estetico post-servicio';

-- -----------------------------------------------------------------------------------------
-- 12. TABLA: INVENTARIO DE PRODUCTOS E INSUMOS
-- -----------------------------------------------------------------------------------------
CREATE TABLE inventario_insumos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    codigo_barras VARCHAR(50) NULL UNIQUE,
    nombre_producto VARCHAR(120) NOT NULL,
    categoria VARCHAR(60) NOT NULL COMMENT 'Shampoo, Acondicionador, Perfume, Cuchillas, Accesorios',
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 5,
    unidad_medida VARCHAR(30) DEFAULT 'Unidad',
    precio_compra DECIMAL(10,2) NOT NULL,
    precio_venta DECIMAL(10,2) NULL,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Control de stock para insumos de peluqueria y venta';

-- -----------------------------------------------------------------------------------------
-- 13. TABLA: FACTURAS (MODULO DE COBRO / POS)
-- -----------------------------------------------------------------------------------------
CREATE TABLE facturas (
    id_factura INT AUTO_INCREMENT PRIMARY KEY,
    id_cita INT NULL UNIQUE,
    id_cliente INT NOT NULL,
    numero_factura VARCHAR(50) NOT NULL UNIQUE,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL,
    iva DECIMAL(10,2) DEFAULT 0.00,
    descuento DECIMAL(10,2) DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL,
    metodo_pago ENUM('EFECTIVO', 'TARJETA_DEBITO', 'TARJETA_CREDITO', 'TRANSFERENCIA_NEQUI_DAVIPLATA', 'OTRO') NOT NULL,
    estado_pago ENUM('PAGADA', 'PENDIENTE', 'ANULADA') DEFAULT 'PAGADA',
    CONSTRAINT fk_facturas_citas FOREIGN KEY (id_cita) 
        REFERENCES citas(id_cita) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_facturas_clientes FOREIGN KEY (id_cliente) 
        REFERENCES clientes(id_cliente) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Registro financiero de facturacion y POS';

-- -----------------------------------------------------------------------------------------
-- 14. TABLA: DETALLE DE FACTURA (ITEMS Y SERVICIOS)
-- -----------------------------------------------------------------------------------------
CREATE TABLE detalle_facturas (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_factura INT NOT NULL,
    id_servicio INT NULL,
    id_producto INT NULL,
    descripcion VARCHAR(200) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_facturas FOREIGN KEY (id_factura) 
        REFERENCES facturas(id_factura) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_servicios FOREIGN KEY (id_servicio) 
        REFERENCES servicios(id_servicio) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_productos FOREIGN KEY (id_producto) 
        REFERENCES inventario_insumos(id_producto) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Detalle linea a linea de productos y servicios cobrados';

-- -----------------------------------------------------------------------------------------
-- 15. TABLA: FOTOGRAFIAS ANTES / DESPUES (ALBUM DIGITAL)
-- -----------------------------------------------------------------------------------------
CREATE TABLE fotos_mascotas (
    id_foto INT AUTO_INCREMENT PRIMARY KEY,
    id_cita INT NOT NULL,
    tipo_foto ENUM('ANTES', 'PROCESO', 'DESPUES') NOT NULL DEFAULT 'PROCESO',
    url_foto VARCHAR(255) NOT NULL,
    cloudinary_public_id VARCHAR(100) NULL,
    subida_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fotos_citas FOREIGN KEY (id_cita) 
        REFERENCES citas(id_cita) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Fotografias de transformacion estetica del servicio';

-- -----------------------------------------------------------------------------------------
-- 16. TABLA: CODIGOS OTP (AUTENTICACION PASSWORDLESS)
-- -----------------------------------------------------------------------------------------
CREATE TABLE codigos_otp (
    id_otp INT AUTO_INCREMENT PRIMARY KEY,
    correo VARCHAR(150) NOT NULL,
    codigo_hash VARCHAR(255) NOT NULL,
    intentos INT DEFAULT 0,
    usado BOOLEAN DEFAULT FALSE,
    expira_en TIMESTAMP NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_otp_correo_expira (correo, expira_en)
) ENGINE=InnoDB COMMENT='Codigos de un solo uso con vigencia de 5 minutos';

-- -----------------------------------------------------------------------------------------
-- 17. TABLA: RECORDATORIOS Y MENSAJES DE WHATSAPP
-- -----------------------------------------------------------------------------------------
CREATE TABLE recordatorios_whatsapp (
    id_recordatorio INT AUTO_INCREMENT PRIMARY KEY,
    id_cita INT NOT NULL,
    numero_destino VARCHAR(20) NOT NULL,
    tipo_mensaje ENUM('CONFIRMACION_CITA', 'ALBUM_LISTO', 'RECORDATORIO_24H', 'ENCUESTA') NOT NULL,
    mensaje_texto TEXT NOT NULL,
    estado_envio ENUM('PENDIENTE', 'ENVIADO', 'FALLIDO') DEFAULT 'PENDIENTE',
    whatsapp_message_id VARCHAR(100) NULL,
    enviado_en TIMESTAMP NULL,
    CONSTRAINT fk_recordatorios_citas FOREIGN KEY (id_cita) 
        REFERENCES citas(id_cita) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Trazabilidad de notificaciones salientes via Meta Cloud API';

-- -----------------------------------------------------------------------------------------
-- 18. TABLA: CONFIGURACION GLOBAL DEL NEGOCIO (HORARIOS, DIAS CERRADOS, CAPACIDAD, POLITICAS)
-- -----------------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS configuracion_negocio (
    clave VARCHAR(50) PRIMARY KEY,
    valor TEXT NOT NULL,
    descripcion VARCHAR(255) NULL,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Parametros globales del negocio y reglas de agendamiento';

INSERT INTO configuracion_negocio (clave, valor, descripcion) VALUES
('horario_apertura', '08:00', 'Hora de apertura del negocio (HH:mm)'),
('horario_cierre', '17:00', 'Hora de cierre del negocio (HH:mm)'),
('dias_cerrados', '0', 'Indices de dias cerrados separados por coma (0=Domingo, 1=Lunes, etc.)'),
('capacidad_estilistas', '3', 'Numero de estilistas en turno para capacidad de citas concurrentes (1 a 5)'),
('politica_cancelacion_horas', '24', 'Horas minimas de anticipacion requeridas para cancelar una cita'),
('duraciones_razas', '{"Criollo / Mestizo":120,"Samoyedo":180,"Gato / Felino":60,"Poodle":120,"Golden Retriever":150,"Bulldog Frances":90,"Schnauzer Miniatura":120}', 'Duracion del proceso de atencion por raza en minutos')
ON DUPLICATE KEY UPDATE valor = VALUES(valor);

-- -----------------------------------------------------------------------------------------
-- VISTAS AUXILIARES PARA REPORTES Y DASHBOARD
-- -----------------------------------------------------------------------------------------
CREATE OR REPLACE VIEW vw_citas_resumen AS
SELECT 
    c.id_cita,
    c.token_acceso,
    c.id_cliente,
    c.id_mascota,
    c.id_servicio,
    c.id_estilista,
    c.duracion_minutos,
    c.observaciones_cliente,
    c.fecha_cita,
    c.hora_cita,
    c.estado_cita,
    c.origen_reserva,
    CONCAT(cli.nombres, ' ', cli.apellidos) AS cliente_nombre,
    cli.correo AS cliente_correo,
    (SELECT ct.numero_telefono FROM cliente_telefonos ct WHERE ct.id_cliente = cli.id_cliente ORDER BY ct.es_principal DESC, ct.id_telefono ASC LIMIT 1) AS cliente_telefono,
    m.nombre AS mascota_nombre,
    r.nombre_raza,
    s.nombre_servicio,
    s.precio_base,
    CONCAT(u_est.nombres, ' ', u_est.apellidos) AS estilista_nombre
FROM citas c
INNER JOIN clientes cli ON c.id_cliente = cli.id_cliente
INNER JOIN mascotas m ON c.id_mascota = m.id_mascota
INNER JOIN razas r ON m.id_raza = r.id_raza
INNER JOIN servicios s ON c.id_servicio = s.id_servicio
LEFT JOIN estilistas e ON c.id_estilista = e.id_estilista
LEFT JOIN usuarios u_est ON e.id_usuario = u_est.id_usuario;

