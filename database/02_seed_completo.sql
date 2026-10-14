-- =========================================================================================
-- DATOS INICIALES Y SEMILLA COMPLETA - CANSTYLING
-- Guia de Aprendizaje 07 - Evidencia GA7-220501096-AA5-EV03
-- =========================================================================================

USE peluqueria_canina_db;

-- 1. ROLES
INSERT INTO roles (id_rol, nombre_rol, descripcion) VALUES 
(1, 'ADMINISTRADOR', 'Control total del sistema, finanzas y reportes'),
(2, 'RECEPCIONISTA', 'Manejo de agenda, clientes, caja y turnos'),
(3, 'ESTILISTA', 'Atencion de citas, registro fotografico antes/despues e historial'),
(4, 'CLIENTE', 'Tutor canino con acceso a reservas y album');

-- 2. USUARIOS (Password por defecto: Admin123* -> Cifrado SHA-256/PBKDF2)
INSERT INTO usuarios (id_usuario, id_rol, nombres, apellidos, correo, password_hash, telefono, canal_registro, estado) VALUES
(1, 1, 'Omar Ulpiano', 'Molina Malaver', 'admin@canstyling.com', 'PBKDF2$10000$c1fa0b55de31...$c7e2d93e...', '3001234567', 'ADMIN', 'ACTIVO'),
(2, 2, 'Claudia Patricia', 'Ruiz Gómez', 'recepcion@canstyling.com', 'PBKDF2$10000$c1fa0b55de31...$c7e2d93e...', '3119876543', 'ADMIN', 'ACTIVO'),
(3, 3, 'Mateo Alejandro', 'Ríos Herrera', 'mateo.estilista@canstyling.com', 'PBKDF2$10000$c1fa0b55de31...$c7e2d93e...', '3205558899', 'ADMIN', 'ACTIVO'),
(4, 4, 'Laura', 'Martinez Perez', 'laura.martinez@gmail.com', 'D0K8vu34kWCmz3zK/mgIDyqvtzs+8Fffa14i8SlNChA=', '3001112233', 'WEB', 'ACTIVO'),
(5, 4, 'Andres', 'Sanchez Doria', 'andres.sanchez@hotmail.com', 'D0K8vu34kWCmz3zK/mgIDyqvtzs+8Fffa14i8SlNChA=', '3109876543', 'WHATSAPP', 'ACTIVO'),
(6, 4, 'Maria Fernanda', 'Lopez Cordoba', 'mafe.lopez@yahoo.com', 'D0K8vu34kWCmz3zK/mgIDyqvtzs+8Fffa14i8SlNChA=', '3187654321', 'PRESENCIAL', 'ACTIVO');

-- 3. ESTILISTAS
INSERT INTO estilistas (id_estilista, id_usuario, especialidad, horario_inicio, horario_fin, dias_laborales, estado) VALUES
(1, 3, 'Cortes de tijera en razas medianas y grandes, Stripping', '08:00:00', '18:00:00', 'Lunes a Sabado', 'DISPONIBLE');

-- 4. CATEGORIAS DE SERVICIOS
INSERT INTO categorias_servicios (id_categoria, nombre_categoria, descripcion) VALUES
(1, 'Higiene Basica', 'Bano, secado, limpieza de oidos y corte de unas'),
(2, 'Peluqueria y Estilismo', 'Cortes de raza, bano medicado y peinado'),
(3, 'Tratamientos Spa', 'Hidratacion profunda, deslanado, aromaterapia'),
(4, 'Salud Preventiva', 'Profilaxis y control ectoparasitario');

-- 5. SERVICIOS
INSERT INTO servicios (id_servicio, id_categoria, nombre_servicio, descripcion, precio_base, duracion_estimada_minutos, estado) VALUES
(1, 1, 'Bano Basico e Higiene', 'Bano con shampoo neutro, secado, corte de unas y limpieza otica', 35000.00, 45, 'DISPONIBLE'),
(2, 2, 'Corte Comercial y Estilizado', 'Bano completo, corte segun raza a maquina/tijera y perfume', 55000.00, 75, 'DISPONIBLE'),
(3, 3, 'Spa Canino y Deslanado', 'Mascarilla hidratante, retiro de pelo muerto y masaje relajante', 70000.00, 90, 'DISPONIBLE'),
(4, 4, 'Profilaxis Dental Preventiva', 'Limpieza dental no invasiva y aplicacion de gel antiseptico', 25000.00, 30, 'DISPONIBLE');

-- 6. RAZAS
INSERT INTO razas (id_raza, nombre_raza, tamano, tipo_pelo, descripcion) VALUES
(1, 'Poodle (Caniche)', 'PEQUENO', 'RIZADO', 'Pelo rizado continuo, requiere mantenimiento frecuente'),
(2, 'Golden Retriever', 'GRANDE', 'LARGO', 'Manto doble resistente al agua con muda abundante'),
(3, 'Bulldog Frances', 'PEQUENO', 'CORTO', 'Pelo corto y liso, requiere atencion en pliegues faciales'),
(4, 'Schnauzer Miniatura', 'PEQUENO', 'DOBLE_CAPA', 'Corte tradicional de barba, cejas y patas'),
(5, 'Criollo / Mestizo', 'MEDIANO', 'MEDIO', 'Caracteristicas mixtas, gran vitalidad');

-- 7. CLIENTES (IDENTIFICACION POR CORREO Y VINCULO CON USUARIOS)
INSERT INTO clientes (id_cliente, id_usuario, correo, password_hash, nombres, apellidos, direccion, barrio_ciudad, canal_origen, observaciones) VALUES
(1, 4, 'laura.martinez@gmail.com', 'D0K8vu34kWCmz3zK/mgIDyqvtzs+8Fffa14i8SlNChA=', 'Laura', 'Martinez Perez', 'Calle 28 # 14-50', 'La Castellana, Monteria', 'WEB', 'Cliente preferencial'),
(2, 5, 'andres.sanchez@hotmail.com', 'D0K8vu34kWCmz3zK/mgIDyqvtzs+8Fffa14i8SlNChA=', 'Andres', 'Sanchez Doria', 'Cra 6 # 45-20', 'El Recreo, Monteria', 'WHATSAPP', 'Solicita atencion los sabados'),
(3, 6, 'mafe.lopez@yahoo.com', 'D0K8vu34kWCmz3zK/mgIDyqvtzs+8Fffa14i8SlNChA=', 'Maria Fernanda', 'Lopez Cordoba', 'Diag 12 # 8-30', 'Pasatiempo, Monteria', 'PRESENCIAL', 'Mascota muy consentida');

-- 8. TELEFONOS MULTI-NUMERO
INSERT INTO cliente_telefonos (id_cliente, numero_telefono, tipo_telefono, es_principal, recibe_notificaciones) VALUES
(1, '3001112233', 'WHATSAPP', TRUE, TRUE),
(1, '3145556677', 'MOVIL', FALSE, FALSE),
(2, '3109876543', 'WHATSAPP', TRUE, TRUE),
(3, '3187654321', 'WHATSAPP', TRUE, TRUE);

-- 9. MASCOTAS
INSERT INTO mascotas (id_mascota, id_cliente, id_raza, nombre, genero, color, edad_aproximada, peso_kg, temperamento, alergias_condiciones, vacunas_al_dia, estado) VALUES
(1, 1, 1, 'Toby', 'MACHO', 'Blanco', 36, 5.50, 'DOCIL', 'Ninguna conocida', TRUE, 'ACTIVO'),
(2, 2, 2, 'Max', 'MACHO', 'Dorado', 48, 28.00, 'HIPERACTIVO', 'Piel sensible, usar shampoo hipoalergenico', TRUE, 'ACTIVO'),
(3, 3, 4, 'Luna', 'HEMBRA', 'Sal y Pimienta', 24, 6.20, 'NERVIOSO', 'Nerviosa con la secadora', TRUE, 'ACTIVO');

-- 10. CITAS CON TOKENS ANTI-IDOR (UUID v4)
INSERT INTO citas (id_cita, token_acceso, id_cliente, id_mascota, id_servicio, id_estilista, fecha_cita, hora_cita, duracion_minutos, estado_cita, origen_reserva, observaciones_cliente) VALUES
(1, 'e4b2d18a-78f9-4c12-98ab-34cd56ef7890', 1, 1, 2, 1, '2026-09-22', '09:00:00', 75, 'COMPLETADA', 'WEB', 'Corte estilizado tipo cachorro'),
(2, 'f9c8d7e6-5432-10fe-dcba-9876543210ab', 2, 2, 3, 1, '2026-09-22', '10:30:00', 90, 'EN_PROCESO', 'WHATSAPP', 'Deslanado intensivo'),
(3, 'a1b2c3d4-e5f6-7890-1234-56789abcdef0', 3, 3, 1, 1, '2026-09-22', '14:00:00', 45, 'PENDIENTE', 'WEB', 'Bano y corte de unas');

-- 11. INVENTARIO DE PRODUCTOS E INSUMOS
INSERT INTO inventario_insumos (id_producto, codigo_barras, nombre_producto, categoria, stock_actual, stock_minimo, unidad_medida, precio_compra, precio_venta) VALUES
(1, '7701234567891', 'Shampoo Hipoalergenico Avena 1L', 'Shampoo', 15, 4, 'Frasco', 22000.00, 38000.00),
(2, '7701234567892', 'Acondicionador Desenredante 500ml', 'Acondicionador', 8, 3, 'Frasco', 16000.00, 28000.00),
(3, '7701234567893', 'Perfume Canino Frutos Rojos 250ml', 'Perfume', 12, 5, 'Unidad', 12000.00, 22000.00),
(4, '7701234567894', 'Cuchilla Oster #10 Profesional', 'Cuchillas', 3, 2, 'Unidad', 95000.00, 140000.00),
(5, '7701234567895', 'Panuelo Decorativo Bandana Canina', 'Accesorios', 25, 10, 'Unidad', 3000.00, 8000.00);

-- 12. FACTURAS / POS
INSERT INTO facturas (id_factura, id_cita, id_cliente, numero_factura, subtotal, iva, descuento, total, metodo_pago, estado_pago) VALUES
(1, 1, 1, 'FAC-2026-0001', 55000.00, 0.00, 0.00, 55000.00, 'TRANSFERENCIA_NEQUI_DAVIPLATA', 'PAGADA');

-- 13. DETALLE DE FACTURAS
INSERT INTO detalle_facturas (id_detalle, id_factura, id_servicio, id_producto, descripcion, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 2, NULL, 'Corte Comercial y Estilizado - Mascota: Toby', 1, 55000.00, 55000.00);

-- 14. FOTOS MASCOTAS (ALBUM DIGITAL ANTES/DESPUES)
INSERT INTO fotos_mascotas (id_foto, id_cita, tipo_foto, url_foto, cloudinary_public_id) VALUES
(1, 1, 'ANTES', 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=600&auto=format&fit=crop&q=80', 'canstyling/antes_toby_001'),
(2, 1, 'DESPUES', 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=600&auto=format&fit=crop&q=80', 'canstyling/despues_toby_001');
