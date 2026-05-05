-- =============================================================
-- SEED DE DATOS DE PRUEBA - SGIE Club Boyacá
-- Pobla: usuario, catálogos, salones, clientes, platos/menú
-- NO crea eventos ni cotizaciones (se prueban manualmente)
-- Ejecutar contra la BD con el backend corriendo o via psql:
--   psql -U <user> -d <db> -f scripts/seed_datos_prueba.sql
-- =============================================================

-- Limpiar en orden inverso de dependencias (seguro para re-ejecución)
DELETE FROM item_menu;
DELETE FROM seleccion_menu;
DELETE FROM menu;
DELETE FROM adicional_evento;
DELETE FROM infraestructura_reserva;
DELETE FROM montaje_mesas_reserva;
DELETE FROM montaje;
DELETE FROM historial_estado_evento;
DELETE FROM cotizacion_item;
DELETE FROM cotizacion;
DELETE FROM reserva_salon;
DELETE FROM evento;
DELETE FROM cliente;
DELETE FROM salon;
DELETE FROM plato_momento;
DELETE FROM plato;
DELETE FROM tipo_momento_menu;
DELETE FROM sobremantel;
DELETE FROM mantel;
DELETE FROM color;
DELETE FROM tipo_adicional;
DELETE FROM tipo_silla;
DELETE FROM tipo_mesa;
DELETE FROM tipo_comida;
DELETE FROM tipo_evento;
DELETE FROM usuario;

-- =============================================================
-- 1. USUARIOS (necesarios como FK en eventos y reservas)
-- =============================================================
INSERT INTO usuario (id_usuario, nombre, contrasena_hash, rol, activo, created_at, updated_at) VALUES
  ('00000000-0000-0000-0000-000000000001', 'Administrador Sistema',  '$2a$10$placeholder', 'ADMIN',      true, NOW(), NOW()),
  ('00000000-0000-0000-0000-000000000002', 'Patricia Castro',        '$2a$10$placeholder', 'COORDINADOR',true, NOW(), NOW()),
  ('00000000-0000-0000-0000-000000000003', 'Andrés Morales',         '$2a$10$placeholder', 'COORDINADOR',true, NOW(), NOW()),
  ('00000000-0000-0000-0000-000000000004', 'Luisa Fernanda Ríos',    '$2a$10$placeholder', 'OPERATIVO',  true, NOW(), NOW());

-- =============================================================
-- 2. TIPOS DE EVENTO
-- =============================================================
INSERT INTO tipo_evento (id_tipo_evento, nombre, descripcion, activo, created_at, updated_at) VALUES
  ('10000000-0000-0000-0000-000000000001', 'Boda',               'Ceremonia y recepción nupcial',              true, NOW(), NOW()),
  ('10000000-0000-0000-0000-000000000002', 'Cumpleaños',         'Celebración de cumpleaños',                  true, NOW(), NOW()),
  ('10000000-0000-0000-0000-000000000003', 'Bautizo',            'Celebración de bautismo',                    true, NOW(), NOW()),
  ('10000000-0000-0000-0000-000000000004', 'Grado',              'Ceremonia de graduación',                    true, NOW(), NOW()),
  ('10000000-0000-0000-0000-000000000005', 'Corporativo',        'Evento empresarial o institucional',         true, NOW(), NOW()),
  ('10000000-0000-0000-0000-000000000006', 'Quinceañera',        'Celebración de quince años',                 true, NOW(), NOW()),
  ('10000000-0000-0000-0000-000000000007', 'Reunión Social',     'Reunión informal o familiar',                true, NOW(), NOW()),
  ('10000000-0000-0000-0000-000000000008', 'Aniversario',        'Celebración de aniversario',                 true, NOW(), NOW());

-- =============================================================
-- 3. TIPOS DE COMIDA
-- =============================================================
INSERT INTO tipo_comida (id_tipo_comida, nombre, descripcion, activo, created_at, updated_at) VALUES
  ('20000000-0000-0000-0000-000000000001', 'Desayuno',           'Servicio de desayuno',                       true, NOW(), NOW()),
  ('20000000-0000-0000-0000-000000000002', 'Almuerzo',           'Servicio de almuerzo',                       true, NOW(), NOW()),
  ('20000000-0000-0000-0000-000000000003', 'Cena',               'Servicio de cena',                           true, NOW(), NOW()),
  ('20000000-0000-0000-0000-000000000004', 'Onces',              'Servicio de onces o merienda',               true, NOW(), NOW()),
  ('20000000-0000-0000-0000-000000000005', 'Cóctel / Pasabocas', 'Servicio de cóctel con pasabocas',           true, NOW(), NOW()),
  ('20000000-0000-0000-0000-000000000006', 'Sin servicio',       'Sin servicio de comida incluido',            true, NOW(), NOW());

-- =============================================================
-- 4. COLORES
-- =============================================================
INSERT INTO color (id_color, nombre, codigo_hex, activo, created_at, updated_at) VALUES
  ('30000000-0000-0000-0000-000000000001', 'Marfil Real',        '#EFE4CE', true, NOW(), NOW()),
  ('30000000-0000-0000-0000-000000000002', 'Blanco Perla',       '#F4F4EF', true, NOW(), NOW()),
  ('30000000-0000-0000-0000-000000000003', 'Champán Claro',      '#E9D8B4', true, NOW(), NOW()),
  ('30000000-0000-0000-0000-000000000004', 'Verde Oliva',        '#7D8461', true, NOW(), NOW()),
  ('30000000-0000-0000-0000-000000000005', 'Gris Piedra',        '#B5B1A8', true, NOW(), NOW()),
  ('30000000-0000-0000-0000-000000000006', 'Dorado Viejo',       '#B08A3F', true, NOW(), NOW()),
  ('30000000-0000-0000-0000-000000000007', 'Champán Satinado',   '#D6B679', true, NOW(), NOW()),
  ('30000000-0000-0000-0000-000000000008', 'Verde Salvia',       '#95A28A', true, NOW(), NOW()),
  ('30000000-0000-0000-0000-000000000009', 'Grafito',            '#5D6165', true, NOW(), NOW()),
  ('30000000-0000-0000-0000-000000000010', 'Borgoña',            '#722F37', true, NOW(), NOW()),
  ('30000000-0000-0000-0000-000000000011', 'Azul Noche',         '#1B2A4A', true, NOW(), NOW()),
  ('30000000-0000-0000-0000-000000000012', 'Rosa Palo',          '#E8C4B8', true, NOW(), NOW());

-- =============================================================
-- 5. TIPOS DE MESA
-- =============================================================
INSERT INTO tipo_mesa (id_tipo_mesa, nombre, activo, created_at, updated_at) VALUES
  ('40000000-0000-0000-0000-000000000001', 'Redonda',            true, NOW(), NOW()),
  ('40000000-0000-0000-0000-000000000002', 'Rectangular',        true, NOW(), NOW()),
  ('40000000-0000-0000-0000-000000000003', 'Imperial',           true, NOW(), NOW()),
  ('40000000-0000-0000-0000-000000000004', 'Cuadrada',           true, NOW(), NOW());

-- =============================================================
-- 6. TIPOS DE SILLA
-- =============================================================
INSERT INTO tipo_silla (id_tipo_silla, nombre, activo, created_at, updated_at) VALUES
  ('50000000-0000-0000-0000-000000000001', 'Tiffany',            true, NOW(), NOW()),
  ('50000000-0000-0000-0000-000000000002', 'Crossback',          true, NOW(), NOW()),
  ('50000000-0000-0000-0000-000000000003', 'Napoleón',           true, NOW(), NOW()),
  ('50000000-0000-0000-0000-000000000004', 'Chiavari Dorada',    true, NOW(), NOW()),
  ('50000000-0000-0000-0000-000000000005', 'Plegable Blanca',    true, NOW(), NOW());

-- =============================================================
-- 7. MANTELES (referencia colores)
-- =============================================================
INSERT INTO mantel (id_mantel, nombre, id_color, activo, created_at, updated_at) VALUES
  ('60000000-0000-0000-0000-000000000001', 'Lino Premium',       '30000000-0000-0000-0000-000000000001', true, NOW(), NOW()),
  ('60000000-0000-0000-0000-000000000002', 'Lino Premium',       '30000000-0000-0000-0000-000000000002', true, NOW(), NOW()),
  ('60000000-0000-0000-0000-000000000003', 'Lino Premium',       '30000000-0000-0000-0000-000000000005', true, NOW(), NOW()),
  ('60000000-0000-0000-0000-000000000004', 'Algodón Clásico',    '30000000-0000-0000-0000-000000000002', true, NOW(), NOW()),
  ('60000000-0000-0000-0000-000000000005', 'Algodón Clásico',    '30000000-0000-0000-0000-000000000003', true, NOW(), NOW()),
  ('60000000-0000-0000-0000-000000000006', 'Algodón Clásico',    '30000000-0000-0000-0000-000000000004', true, NOW(), NOW()),
  ('60000000-0000-0000-0000-000000000007', 'Raso Ceremonial',    '30000000-0000-0000-0000-000000000003', true, NOW(), NOW()),
  ('60000000-0000-0000-0000-000000000008', 'Raso Ceremonial',    '30000000-0000-0000-0000-000000000001', true, NOW(), NOW()),
  ('60000000-0000-0000-0000-000000000009', 'Raso Ceremonial',    '30000000-0000-0000-0000-000000000005', true, NOW(), NOW()),
  ('60000000-0000-0000-0000-000000000010', 'Terciopelo',         '30000000-0000-0000-0000-000000000010', true, NOW(), NOW()),
  ('60000000-0000-0000-0000-000000000011', 'Terciopelo',         '30000000-0000-0000-0000-000000000011', true, NOW(), NOW()),
  ('60000000-0000-0000-0000-000000000012', 'Tul Festivo',        '30000000-0000-0000-0000-000000000012', true, NOW(), NOW());

-- =============================================================
-- 8. SOBREMANTELES (referencia colores)
-- =============================================================
INSERT INTO sobremantel (id_sobremantel, nombre, id_color, activo, created_at, updated_at) VALUES
  ('70000000-0000-0000-0000-000000000001', 'Organza',            '30000000-0000-0000-0000-000000000006', true, NOW(), NOW()),
  ('70000000-0000-0000-0000-000000000002', 'Organza',            '30000000-0000-0000-0000-000000000007', true, NOW(), NOW()),
  ('70000000-0000-0000-0000-000000000003', 'Organza',            '30000000-0000-0000-0000-000000000001', true, NOW(), NOW()),
  ('70000000-0000-0000-0000-000000000004', 'Encaje',             '30000000-0000-0000-0000-000000000001', true, NOW(), NOW()),
  ('70000000-0000-0000-0000-000000000005', 'Encaje',             '30000000-0000-0000-0000-000000000008', true, NOW(), NOW()),
  ('70000000-0000-0000-0000-000000000006', 'Encaje',             '30000000-0000-0000-0000-000000000007', true, NOW(), NOW()),
  ('70000000-0000-0000-0000-000000000007', 'Satinado',           '30000000-0000-0000-0000-000000000009', true, NOW(), NOW()),
  ('70000000-0000-0000-0000-000000000008', 'Satinado',           '30000000-0000-0000-0000-000000000006', true, NOW(), NOW()),
  ('70000000-0000-0000-0000-000000000009', 'Satinado',           '30000000-0000-0000-0000-000000000007', true, NOW(), NOW()),
  ('70000000-0000-0000-0000-000000000010', 'Tul Bordado',        '30000000-0000-0000-0000-000000000012', true, NOW(), NOW()),
  ('70000000-0000-0000-0000-000000000011', 'Tul Bordado',        '30000000-0000-0000-0000-000000000002', true, NOW(), NOW());

-- =============================================================
-- 9. TIPOS DE ADICIONAL
-- =============================================================
INSERT INTO tipo_adicional (id_tipo_adicional, nombre, modo_cobro, precio_base, activo, created_at, updated_at) VALUES
  ('80000000-0000-0000-0000-000000000001', 'Tarima',             'UNIDAD',   180000.00, true, NOW(), NOW()),
  ('80000000-0000-0000-0000-000000000002', 'Audiovisuales',      'SERVICIO', 450000.00, true, NOW(), NOW()),
  ('80000000-0000-0000-0000-000000000003', 'Telas Decorativas',  'UNIDAD',   120000.00, true, NOW(), NOW()),
  ('80000000-0000-0000-0000-000000000004', 'Luces Árbol',        'SERVICIO', 260000.00, true, NOW(), NOW()),
  ('80000000-0000-0000-0000-000000000005', 'Luces Techo',        'SERVICIO', 300000.00, true, NOW(), NOW()),
  ('80000000-0000-0000-0000-000000000006', 'Arco Floral',        'UNIDAD',   350000.00, true, NOW(), NOW()),
  ('80000000-0000-0000-0000-000000000007', 'Photobooth',         'SERVICIO', 500000.00, true, NOW(), NOW()),
  ('80000000-0000-0000-0000-000000000008', 'Silla Novia/Novio',  'UNIDAD',   95000.00,  true, NOW(), NOW()),
  ('80000000-0000-0000-0000-000000000009', 'Candelabro',         'UNIDAD',   75000.00,  true, NOW(), NOW()),
  ('80000000-0000-0000-0000-000000000010', 'Pista de Baile',     'SERVICIO', 800000.00, true, NOW(), NOW());

-- =============================================================
-- 10. SALONES
-- =============================================================
-- NOTA: salon tiene columnas extra (id, capacidad) agregadas por Hibernate ddl-auto:update
INSERT INTO salon (id_salon, id, nombre, capacidad_max, capacidad, descripcion, activo, created_at, updated_at) VALUES
  ('90000000-0000-0000-0000-000000000001','90000000-0000-0000-0000-000000000001','Salon Jade',          120,120,'Ambiente senorial con iluminacion calida. Ideal para bodas y eventos sociales intimos.',   true, NOW(), NOW()),
  ('90000000-0000-0000-0000-000000000002','90000000-0000-0000-0000-000000000002','Versalles Principal', 220,220,'Salon principal del club. Techos altos, piso de madera y acceso a terraza.',               true, NOW(), NOW()),
  ('90000000-0000-0000-0000-000000000003','90000000-0000-0000-0000-000000000003','Terraza Mirador',      80, 80,'Espacio semiabierto con vista al jardin. Ideal para almuerzos y eventos diurnos.',         true, NOW(), NOW()),
  ('90000000-0000-0000-0000-000000000004','90000000-0000-0000-0000-000000000004','Biblioteca',           35, 35,'Sala privada con ambiente ejecutivo. Para reuniones pequenas y corporativos.',             true, NOW(), NOW()),
  ('90000000-0000-0000-0000-000000000005','90000000-0000-0000-0000-000000000005','Salon Esmeralda',      60, 60,'Salon intermedio con decoracion clasica. Para cumpleanos y reuniones familiares.',          true, NOW(), NOW()),
  ('90000000-0000-0000-0000-000000000006','90000000-0000-0000-0000-000000000006','Area Social Exterior',150,150,'Zona al aire libre con kiosco y jardin. Para eventos casuales y cocteleria.',             true, NOW(), NOW())
ON CONFLICT (id_salon) DO NOTHING;

-- =============================================================
-- 11. CLIENTES
-- =============================================================
INSERT INTO cliente (id_cliente, cedula, nombre_completo, telefono, correo, tipo_cliente, activo, creado_por, created_at, updated_at) VALUES
  -- Socios del club
  ('A0000000-0000-0000-0000-000000000001', '1024456789', 'Ricardo Albarracín Peña',
   '+57 312 456 7890', 'r.albarracin@email.com',       'SOCIO',    true, '00000000-0000-0000-0000-000000000002', NOW(), NOW()),
  ('A0000000-0000-0000-0000-000000000002', '1018234567', 'Mauricio Herrera Ospina',
   '+57 315 987 6543', 'm.herrera@boyaca.org',          'SOCIO',    true, '00000000-0000-0000-0000-000000000002', NOW(), NOW()),
  ('A0000000-0000-0000-0000-000000000003', '46382001',   'Lucía Ramírez Vargas',
   '+57 310 234 5678', 'lucia.ramirez@correo.com',      'SOCIO',    true, '00000000-0000-0000-0000-000000000002', NOW(), NOW()),
  ('A0000000-0000-0000-0000-000000000004', '79567890',   'Carlos Eduardo Pineda',
   '+57 300 111 2233', 'c.pineda@gmail.com',            'SOCIO',    true, '00000000-0000-0000-0000-000000000003', NOW(), NOW()),
  ('A0000000-0000-0000-0000-000000000005', '1052889442', 'Natalia Forero Castillo',
   '+57 316 331 7890', 'natalia.forero@correo.com',     'SOCIO',    true, '00000000-0000-0000-0000-000000000003', NOW(), NOW()),
  ('A0000000-0000-0000-0000-000000000006', '28765432',   'Gloria Inés Suárez',
   '+57 311 445 6677', 'gloria.suarez@hotmail.com',     'SOCIO',    true, '00000000-0000-0000-0000-000000000002', NOW(), NOW()),
  -- No socios
  ('A0000000-0000-0000-0000-000000000007', '79567891',   'Claudia Mendoza Torres',
   '+57 300 123 4455', 'claudia.m@gmail.com',           'NO_SOCIO', true, '00000000-0000-0000-0000-000000000002', NOW(), NOW()),
  ('A0000000-0000-0000-0000-000000000008', '1052001234', 'Juan Camilo Daza Ríos',
   '+57 317 889 0011', 'jcdaza@empresa.co',             'NO_SOCIO', true, '00000000-0000-0000-0000-000000000003', NOW(), NOW()),
  ('A0000000-0000-0000-0000-000000000009', '900564321',  'Constructora Capital S.A.S.',
   '+57 1 234 5678',   'eventos@constructoracapital.co','NO_SOCIO', true, '00000000-0000-0000-0000-000000000002', NOW(), NOW()),
  ('A0000000-0000-0000-0000-000000000010', '1024001122', 'Ana María Rojas Gutiérrez',
   '+57 312 001 1223', 'ana.rojas@personal.com',        'NO_SOCIO', true, '00000000-0000-0000-0000-000000000003', NOW(), NOW()),
  ('A0000000-0000-0000-0000-000000000011', '52334455',   'Familia Mendoza Acosta',
   '+57 313 556 7788', 'mendoza.acosta@gmail.com',      'NO_SOCIO', true, '00000000-0000-0000-0000-000000000002', NOW(), NOW()),
  ('A0000000-0000-0000-0000-000000000012', '1015667788', 'Sebastián Vargas Mora',
   '+57 318 990 1122', 'svargas@correo.com',            'NO_SOCIO', true, '00000000-0000-0000-0000-000000000003', NOW(), NOW());

-- =============================================================
-- 12. TIPOS DE MOMENTO DE MENÚ
-- =============================================================
INSERT INTO tipo_momento_menu (id_tipo_momento, nombre, activo, created_at, updated_at) VALUES
  ('B0000000-0000-0000-0000-000000000001', 'Entrada',            true, NOW(), NOW()),
  ('B0000000-0000-0000-0000-000000000002', 'Consomé / Sopa',     true, NOW(), NOW()),
  ('B0000000-0000-0000-0000-000000000003', 'Plato Fuerte',       true, NOW(), NOW()),
  ('B0000000-0000-0000-0000-000000000004', 'Postre',             true, NOW(), NOW()),
  ('B0000000-0000-0000-0000-000000000005', 'Bebidas',            true, NOW(), NOW()),
  ('B0000000-0000-0000-0000-000000000006', 'Pasabocas',          true, NOW(), NOW()),
  ('B0000000-0000-0000-0000-000000000007', 'Torta / Ponqué',     true, NOW(), NOW());

-- =============================================================
-- 13. PLATOS
-- =============================================================
INSERT INTO plato (id_plato, nombre, descripcion, precio_base, activo, created_at, updated_at) VALUES
  -- Entradas
  ('C0000000-0000-0000-0000-000000000001', 'Carpaccio de Res',
   'Láminas finas de res con alcaparras, parmesano y aceite de oliva',          25000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000002', 'Ensalada de Frutos del Bosque',
   'Mix de lechugas, frutos rojos, nueces y vinagreta de frambuesa',             22000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000003', 'Ceviche de Camarón',
   'Camarones frescos en leche de tigre con aguacate y maíz tostado',           28000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000004', 'Bruschetta Tricolor',
   'Pan artesanal con tomate, albahaca y queso de cabra',                        18000.00, true, NOW(), NOW()),
  -- Consomés / Sopas
  ('C0000000-0000-0000-0000-000000000005', 'Crema de Espárragos',
   'Crema suave de espárragos verdes con aceite de trufa',                        8500.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000006', 'Consomé de Pavo',
   'Caldo artesanal de pavo con vegetales y hierbas finas',                       9000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000007', 'Crema de Champiñones',
   'Crema de champiñones silvestres con croutons y cebollín',                     9500.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000008', 'Sopa de Tomate Asado',
   'Tomates asados con albahaca fresca y crema de leche',                         8000.00, true, NOW(), NOW()),
  -- Platos fuertes
  ('C0000000-0000-0000-0000-000000000009', 'Medallón de Lomo en Salsa Pimienta',
   'Lomo de res al punto con salsa de pimienta verde y papas gratinadas',        65000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000010', 'Salmón a la Parrilla',
   'Filete de salmón con finas hierbas, espárragos y risotto de limón',          68000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000011', 'Pechuga Rellena de Espinaca',
   'Pechuga de pollo rellena con espinaca y queso, salsa de champiñones',        52000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000012', 'Costilla de Cerdo BBQ',
   'Costilla de cerdo glaseada con salsa BBQ artesanal y puré de papa',          58000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000013', 'Risotto de Mariscos',
   'Arroz arbóreo cremoso con camarones, mejillones y vieiras',                  72000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000014', 'Lomo de Cerdo al Vino Tinto',
   'Lomo de cerdo braseado en vino tinto con vegetales asados',                  55000.00, true, NOW(), NOW()),
  -- Postres
  ('C0000000-0000-0000-0000-000000000015', 'Mousse de Chocolate 70%',
   'Mousse aireado de chocolate oscuro con coulis de frambuesa',                 12000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000016', 'Cheesecake de Frutos Amarillos',
   'Cheesecake cremoso con compota de maracuyá y mango',                         13000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000017', 'Tiramisú Clásico',
   'Tiramisú italiano con café espresso y mascarpone',                           11000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000018', 'Panna Cotta de Vainilla',
   'Panna cotta con coulis de frutos rojos y menta fresca',                      10000.00, true, NOW(), NOW()),
  -- Bebidas
  ('C0000000-0000-0000-0000-000000000019', 'Jugo Natural + Agua',
   'Jugo de fruta de temporada y agua mineral',                                  15000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000020', 'Vino de la Casa',
   'Copa de vino tinto o blanco de la casa',                                     26000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000021', 'Limonada de Coco',
   'Limonada artesanal con leche de coco y hierbabuena',                         12000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000022', 'Agua Saborizada',
   'Agua infusionada con pepino, limón y menta',                                  8000.00, true, NOW(), NOW()),
  -- Pasabocas
  ('C0000000-0000-0000-0000-000000000023', 'Tabla de Quesos y Embutidos',
   'Selección de quesos artesanales, jamón serrano y acompañamientos',           35000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000024', 'Mini Canapés Variados',
   'Surtido de 6 canapés: salmón, queso crema, paté y vegetales',               28000.00, true, NOW(), NOW()),
  -- Torta
  ('C0000000-0000-0000-0000-000000000025', 'Torta de Bodas Clásica',
   'Torta de tres pisos con relleno de frutos rojos y cubierta de fondant',     120000.00, true, NOW(), NOW()),
  ('C0000000-0000-0000-0000-000000000026', 'Ponqué de Cumpleaños',
   'Ponqué húmedo de vainilla con buttercream y decoración personalizada',       85000.00, true, NOW(), NOW());

-- =============================================================
-- 14. PLATO_MOMENTO (qué platos pertenecen a qué momento)
-- =============================================================
-- Entradas
INSERT INTO plato_momento (id_plato, id_tipo_momento) VALUES
  ('C0000000-0000-0000-0000-000000000001', 'B0000000-0000-0000-0000-000000000001'),
  ('C0000000-0000-0000-0000-000000000002', 'B0000000-0000-0000-0000-000000000001'),
  ('C0000000-0000-0000-0000-000000000003', 'B0000000-0000-0000-0000-000000000001'),
  ('C0000000-0000-0000-0000-000000000004', 'B0000000-0000-0000-0000-000000000001');
-- Consomés
INSERT INTO plato_momento (id_plato, id_tipo_momento) VALUES
  ('C0000000-0000-0000-0000-000000000005', 'B0000000-0000-0000-0000-000000000002'),
  ('C0000000-0000-0000-0000-000000000006', 'B0000000-0000-0000-0000-000000000002'),
  ('C0000000-0000-0000-0000-000000000007', 'B0000000-0000-0000-0000-000000000002'),
  ('C0000000-0000-0000-0000-000000000008', 'B0000000-0000-0000-0000-000000000002');
-- Platos fuertes
INSERT INTO plato_momento (id_plato, id_tipo_momento) VALUES
  ('C0000000-0000-0000-0000-000000000009', 'B0000000-0000-0000-0000-000000000003'),
  ('C0000000-0000-0000-0000-000000000010', 'B0000000-0000-0000-0000-000000000003'),
  ('C0000000-0000-0000-0000-000000000011', 'B0000000-0000-0000-0000-000000000003'),
  ('C0000000-0000-0000-0000-000000000012', 'B0000000-0000-0000-0000-000000000003'),
  ('C0000000-0000-0000-0000-000000000013', 'B0000000-0000-0000-0000-000000000003'),
  ('C0000000-0000-0000-0000-000000000014', 'B0000000-0000-0000-0000-000000000003');
-- Postres
INSERT INTO plato_momento (id_plato, id_tipo_momento) VALUES
  ('C0000000-0000-0000-0000-000000000015', 'B0000000-0000-0000-0000-000000000004'),
  ('C0000000-0000-0000-0000-000000000016', 'B0000000-0000-0000-0000-000000000004'),
  ('C0000000-0000-0000-0000-000000000017', 'B0000000-0000-0000-0000-000000000004'),
  ('C0000000-0000-0000-0000-000000000018', 'B0000000-0000-0000-0000-000000000004');
-- Bebidas
INSERT INTO plato_momento (id_plato, id_tipo_momento) VALUES
  ('C0000000-0000-0000-0000-000000000019', 'B0000000-0000-0000-0000-000000000005'),
  ('C0000000-0000-0000-0000-000000000020', 'B0000000-0000-0000-0000-000000000005'),
  ('C0000000-0000-0000-0000-000000000021', 'B0000000-0000-0000-0000-000000000005'),
  ('C0000000-0000-0000-0000-000000000022', 'B0000000-0000-0000-0000-000000000005');
-- Pasabocas
INSERT INTO plato_momento (id_plato, id_tipo_momento) VALUES
  ('C0000000-0000-0000-0000-000000000023', 'B0000000-0000-0000-0000-000000000006'),
  ('C0000000-0000-0000-0000-000000000024', 'B0000000-0000-0000-0000-000000000006');
-- Torta
INSERT INTO plato_momento (id_plato, id_tipo_momento) VALUES
  ('C0000000-0000-0000-0000-000000000025', 'B0000000-0000-0000-0000-000000000007'),
  ('C0000000-0000-0000-0000-000000000026', 'B0000000-0000-0000-0000-000000000007');

-- =============================================================
-- FIN DEL SEED
-- =============================================================
-- Resumen de lo insertado:
--   4  usuarios
--   8  tipos de evento
--   6  tipos de comida
--  12  colores
--   4  tipos de mesa
--   5  tipos de silla
--  12  manteles
--  11  sobremanteles
--  10  tipos de adicional
--   6  salones
--  12  clientes (6 socios + 6 no socios)
--   7  momentos de menú
--  26  platos
--  26  relaciones plato-momento
-- =============================================================

-- =============================================================
-- SCRIPT DE VERIFICACIÓN (ejecutar después del seed)
-- =============================================================
-- SELECT 'usuarios'       AS tabla, COUNT(*) AS total FROM usuario
-- UNION ALL SELECT 'tipos_evento',   COUNT(*) FROM tipo_evento
-- UNION ALL SELECT 'tipos_comida',   COUNT(*) FROM tipo_comida
-- UNION ALL SELECT 'colores',        COUNT(*) FROM color
-- UNION ALL SELECT 'tipos_mesa',     COUNT(*) FROM tipo_mesa
-- UNION ALL SELECT 'tipos_silla',    COUNT(*) FROM tipo_silla
-- UNION ALL SELECT 'manteles',       COUNT(*) FROM mantel
-- UNION ALL SELECT 'sobremanteles',  COUNT(*) FROM sobremantel
-- UNION ALL SELECT 'tipos_adicional',COUNT(*) FROM tipo_adicional
-- UNION ALL SELECT 'salones',        COUNT(*) FROM salon
-- UNION ALL SELECT 'clientes',       COUNT(*) FROM cliente
-- UNION ALL SELECT 'momentos_menu',  COUNT(*) FROM tipo_momento_menu
-- UNION ALL SELECT 'platos',         COUNT(*) FROM plato
-- UNION ALL SELECT 'plato_momento',  COUNT(*) FROM plato_momento;

