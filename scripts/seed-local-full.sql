-- Datos locales de prueba para SGIE.
-- Todos los usuarios insertados usan la contrasena: admin123

insert into usuario (id_usuario, nombre, contrasena_hash, rol, activo, created_at, updated_at) values
('10000000-0000-0000-0000-000000000001', 'Administrador', '$2a$10$tnl0KOJZXBuD9kRAYaNC8epZlRjCAFRcaRaO3MV1OD6P9XxWoU1fu', 'ADMINISTRADOR', true, now(), now()),
('10000000-0000-0000-0000-000000000002', 'Gerente Demo', '$2a$10$tnl0KOJZXBuD9kRAYaNC8epZlRjCAFRcaRaO3MV1OD6P9XxWoU1fu', 'GERENTE', true, now(), now()),
('10000000-0000-0000-0000-000000000003', 'Tesorero Demo', '$2a$10$tnl0KOJZXBuD9kRAYaNC8epZlRjCAFRcaRaO3MV1OD6P9XxWoU1fu', 'TESORERO', true, now(), now()),
('10000000-0000-0000-0000-000000000004', 'Jefe Mesa Demo', '$2a$10$tnl0KOJZXBuD9kRAYaNC8epZlRjCAFRcaRaO3MV1OD6P9XxWoU1fu', 'JEFE_MESA', true, now(), now())
on conflict (id_usuario) do update set
    nombre = excluded.nombre,
    contrasena_hash = excluded.contrasena_hash,
    rol = excluded.rol,
    activo = true,
    updated_at = now();

insert into tipo_evento (id_tipo_evento, nombre, descripcion, activo, created_at, updated_at) values
('20000000-0000-0000-0000-000000000001', 'Boda', 'Celebracion matrimonial', true, now(), now()),
('20000000-0000-0000-0000-000000000002', 'Empresarial', 'Evento corporativo', true, now(), now()),
('20000000-0000-0000-0000-000000000003', 'Cumpleanos', 'Celebracion social', true, now(), now())
on conflict (id_tipo_evento) do nothing;

insert into tipo_comida (id_tipo_comida, nombre, descripcion, activo, created_at, updated_at) values
('21000000-0000-0000-0000-000000000001', 'Almuerzo', 'Servicio de almuerzo', true, now(), now()),
('21000000-0000-0000-0000-000000000002', 'Cena', 'Servicio de cena', true, now(), now()),
('21000000-0000-0000-0000-000000000003', 'Coctel', 'Servicio de coctel', true, now(), now())
on conflict (id_tipo_comida) do nothing;

insert into color (id_color, nombre, codigo_hex, activo, created_at, updated_at) values
('22000000-0000-0000-0000-000000000001', 'Blanco', '#FFFFFF', true, now(), now()),
('22000000-0000-0000-0000-000000000002', 'Dorado', '#A8841C', true, now(), now()),
('22000000-0000-0000-0000-000000000003', 'Azul', '#1D4ED8', true, now(), now())
on conflict (id_color) do nothing;

insert into tipo_mesa (id_tipo_mesa, nombre, activo, created_at, updated_at) values
('23000000-0000-0000-0000-000000000001', 'Redonda', true, now(), now()),
('23000000-0000-0000-0000-000000000002', 'Imperial', true, now(), now())
on conflict (id_tipo_mesa) do nothing;

insert into tipo_silla (id_tipo_silla, nombre, activo, created_at, updated_at) values
('24000000-0000-0000-0000-000000000001', 'Tiffany', true, now(), now()),
('24000000-0000-0000-0000-000000000002', 'Vestida', true, now(), now())
on conflict (id_tipo_silla) do nothing;

insert into mantel (id_mantel, nombre, id_color, activo, created_at, updated_at) values
('25000000-0000-0000-0000-000000000001', 'Mantel blanco', '22000000-0000-0000-0000-000000000001', true, now(), now()),
('25000000-0000-0000-0000-000000000002', 'Mantel azul', '22000000-0000-0000-0000-000000000003', true, now(), now())
on conflict (id_mantel) do nothing;

insert into sobremantel (id_sobremantel, nombre, id_color, activo, created_at, updated_at) values
('26000000-0000-0000-0000-000000000001', 'Sobremantel dorado', '22000000-0000-0000-0000-000000000002', true, now(), now()),
('26000000-0000-0000-0000-000000000002', 'Sobremantel azul', '22000000-0000-0000-0000-000000000003', true, now(), now())
on conflict (id_sobremantel) do nothing;

insert into tipo_adicional (id_tipo_adicional, nombre, modo_cobro, precio_base, activo, created_at, updated_at) values
('27000000-0000-0000-0000-000000000001', 'Sonido basico', 'SERVICIO', 350000.00, true, now(), now()),
('27000000-0000-0000-0000-000000000002', 'Estacion de cafe', 'UNIDAD', 12000.00, true, now(), now()),
('27000000-0000-0000-0000-000000000003', 'Decoracion floral', 'SERVICIO', 500000.00, true, now(), now())
on conflict (id_tipo_adicional) do nothing;

insert into tipo_momento_menu (id_tipo_momento, nombre, activo, created_at, updated_at) values
('28000000-0000-0000-0000-000000000001', 'Entrada', true, now(), now()),
('28000000-0000-0000-0000-000000000002', 'Plato fuerte', true, now(), now()),
('28000000-0000-0000-0000-000000000003', 'Postre', true, now(), now())
on conflict (id_tipo_momento) do nothing;

insert into plato (id_plato, nombre, descripcion, precio_base, activo, created_at, updated_at) values
('29000000-0000-0000-0000-000000000001', 'Crema de tomate', 'Entrada caliente', 18000.00, true, now(), now()),
('29000000-0000-0000-0000-000000000002', 'Lomo en salsa de vino', 'Plato fuerte con guarnicion', 62000.00, true, now(), now()),
('29000000-0000-0000-0000-000000000003', 'Cheesecake de frutos rojos', 'Postre individual', 16000.00, true, now(), now()),
('29000000-0000-0000-0000-000000000004', 'Menu infantil', 'Opcion para ninos', 28000.00, true, now(), now())
on conflict (id_plato) do nothing;

insert into plato_momento (id_plato, id_tipo_momento) values
('29000000-0000-0000-0000-000000000001', '28000000-0000-0000-0000-000000000001'),
('29000000-0000-0000-0000-000000000002', '28000000-0000-0000-0000-000000000002'),
('29000000-0000-0000-0000-000000000003', '28000000-0000-0000-0000-000000000003'),
('29000000-0000-0000-0000-000000000004', '28000000-0000-0000-0000-000000000002')
on conflict do nothing;

insert into cliente (id_cliente, cedula, nombre_completo, telefono, correo, tipo_cliente, activo, creado_por, created_at, updated_at) values
('30000000-0000-0000-0000-000000000001', '900100001', 'Empresa Andina SAS', '3001112233', 'eventos@andina.test', 'NO_SOCIO', true, '10000000-0000-0000-0000-000000000002', now(), now()),
('30000000-0000-0000-0000-000000000002', '1020304050', 'Laura Martinez', '3002223344', 'laura.martinez@test.com', 'SOCIO', true, '10000000-0000-0000-0000-000000000002', now(), now()),
('30000000-0000-0000-0000-000000000003', '79888777', 'Carlos Ramirez', '3003334455', 'carlos.ramirez@test.com', 'NO_SOCIO', true, '10000000-0000-0000-0000-000000000002', now(), now())
on conflict (id_cliente) do nothing;

insert into salon (id_salon, nombre, capacidad_max, descripcion, activo, created_at, updated_at) values
('31000000-0000-0000-0000-000000000001', 'Salon Boyaca', 180, 'Salon principal', true, now(), now()),
('31000000-0000-0000-0000-000000000002', 'Salon Fundadores', 90, 'Salon mediano', true, now(), now()),
('31000000-0000-0000-0000-000000000003', 'Terraza', 70, 'Espacio abierto', true, now(), now())
on conflict (id_salon) do nothing;

insert into evento (id_evento, id_cliente, id_tipo_evento, id_tipo_comida, id_usuario_creador, fecha_hora_inicio, fecha_hora_fin, estado, gcal_event_id, created_at, updated_at) values
('40000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', '21000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', current_date + interval '20 days' + time '18:00', current_date + interval '20 days' + time '23:30', 'PENDIENTE_ANTICIPO', null, now(), now()),
('40000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000002', '21000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', current_date + interval '35 days' + time '12:00', current_date + interval '35 days' + time '17:00', 'COTIZACION_ENVIADA', null, now(), now()),
('40000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000003', '21000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002', current_date + interval '45 days' + time '16:00', current_date + interval '45 days' + time '21:00', 'CONFIRMADO', 'local-demo-calendar-1', now(), now()),
('40000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000002', '21000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002', current_date + interval '55 days' + time '09:00', current_date + interval '55 days' + time '13:00', 'PENDIENTE', null, now(), now()),
('40000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', '21000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', current_date - interval '10 days' + time '18:00', current_date - interval '10 days' + time '23:00', 'CANCELADO', null, now(), now())
on conflict (id_evento) do nothing;

insert into reserva_salon (id_reserva, reserva_raiz_id, id_evento, id_salon, num_invitados, fecha_hora_inicio, fecha_hora_fin, version, vigente, creado_por, created_at, updated_at) values
('41000000-0000-0000-0000-000000000001', '41000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', '31000000-0000-0000-0000-000000000001', 120, current_date + interval '20 days' + time '18:00', current_date + interval '20 days' + time '23:30', 1, true, '10000000-0000-0000-0000-000000000002', now(), now()),
('41000000-0000-0000-0000-000000000002', '41000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000002', '31000000-0000-0000-0000-000000000002', 70, current_date + interval '35 days' + time '12:00', current_date + interval '35 days' + time '17:00', 1, true, '10000000-0000-0000-0000-000000000002', now(), now()),
('41000000-0000-0000-0000-000000000003', '41000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000003', '31000000-0000-0000-0000-000000000003', 55, current_date + interval '45 days' + time '16:00', current_date + interval '45 days' + time '21:00', 1, true, '10000000-0000-0000-0000-000000000002', now(), now()),
('41000000-0000-0000-0000-000000000004', '41000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000004', '31000000-0000-0000-0000-000000000002', 45, current_date + interval '55 days' + time '09:00', current_date + interval '55 days' + time '13:00', 1, true, '10000000-0000-0000-0000-000000000002', now(), now()),
('41000000-0000-0000-0000-000000000005', '41000000-0000-0000-0000-000000000005', '40000000-0000-0000-0000-000000000005', '31000000-0000-0000-0000-000000000001', 100, current_date - interval '10 days' + time '18:00', current_date - interval '10 days' + time '23:00', 1, true, '10000000-0000-0000-0000-000000000002', now(), now())
on conflict (id_reserva) do nothing;

insert into montaje (id_montaje, id_reserva, observaciones, created_at, updated_at) values
('42000000-0000-0000-0000-000000000001', '41000000-0000-0000-0000-000000000001', 'Montaje elegante con mesa principal.', now(), now()),
('42000000-0000-0000-0000-000000000002', '41000000-0000-0000-0000-000000000002', 'Montaje tipo auditorio con estacion de cafe.', now(), now()),
('42000000-0000-0000-0000-000000000003', '41000000-0000-0000-0000-000000000003', 'Montaje casual en terraza.', now(), now())
on conflict (id_montaje) do nothing;

insert into montaje_mesas_reserva (id_montaje_mesa, id_montaje, id_tipo_mesa, id_tipo_silla, silla_por_mesa, cantidad_mesas, id_mantel, id_sobremantel, vajilla, fajon) values
('43000000-0000-0000-0000-000000000001', '42000000-0000-0000-0000-000000000001', '23000000-0000-0000-0000-000000000001', '24000000-0000-0000-0000-000000000001', 10, 12, '25000000-0000-0000-0000-000000000001', '26000000-0000-0000-0000-000000000001', true, true),
('43000000-0000-0000-0000-000000000002', '42000000-0000-0000-0000-000000000002', '23000000-0000-0000-0000-000000000002', '24000000-0000-0000-0000-000000000002', 8, 9, '25000000-0000-0000-0000-000000000002', null, true, false),
('43000000-0000-0000-0000-000000000003', '42000000-0000-0000-0000-000000000003', '23000000-0000-0000-0000-000000000001', '24000000-0000-0000-0000-000000000001', 8, 7, '25000000-0000-0000-0000-000000000001', '26000000-0000-0000-0000-000000000002', true, false)
on conflict (id_montaje_mesa) do nothing;

insert into infraestructura_reserva (id_infra_reserva, id_montaje, mesa_ponque, mesa_regalos, espacio_musicos, estante_bombas) values
('44000000-0000-0000-0000-000000000001', '42000000-0000-0000-0000-000000000001', true, true, true, false),
('44000000-0000-0000-0000-000000000002', '42000000-0000-0000-0000-000000000002', false, false, true, false),
('44000000-0000-0000-0000-000000000003', '42000000-0000-0000-0000-000000000003', true, false, false, true)
on conflict (id_infra_reserva) do nothing;

insert into adicional_evento (id_adicional_evento, id_montaje, id_tipo_adicional, cantidad) values
('45000000-0000-0000-0000-000000000001', '42000000-0000-0000-0000-000000000001', '27000000-0000-0000-0000-000000000001', 1),
('45000000-0000-0000-0000-000000000002', '42000000-0000-0000-0000-000000000002', '27000000-0000-0000-0000-000000000002', 70),
('45000000-0000-0000-0000-000000000003', '42000000-0000-0000-0000-000000000003', '27000000-0000-0000-0000-000000000003', 1)
on conflict (id_adicional_evento) do nothing;

insert into menu (id_menu, id_reserva, notas_generales, created_at, updated_at) values
('46000000-0000-0000-0000-000000000001', '41000000-0000-0000-0000-000000000001', 'Incluir opcion vegetariana para 8 invitados.', now(), now()),
('46000000-0000-0000-0000-000000000002', '41000000-0000-0000-0000-000000000002', 'Servicio rapido por agenda empresarial.', now(), now()),
('46000000-0000-0000-0000-000000000003', '41000000-0000-0000-0000-000000000003', 'Menu casual con opcion infantil.', now(), now())
on conflict (id_menu) do nothing;

insert into seleccion_menu (id_seleccion_menu, id_menu, id_tipo_momento) values
('47000000-0000-0000-0000-000000000001', '46000000-0000-0000-0000-000000000001', '28000000-0000-0000-0000-000000000001'),
('47000000-0000-0000-0000-000000000002', '46000000-0000-0000-0000-000000000001', '28000000-0000-0000-0000-000000000002'),
('47000000-0000-0000-0000-000000000003', '46000000-0000-0000-0000-000000000001', '28000000-0000-0000-0000-000000000003'),
('47000000-0000-0000-0000-000000000004', '46000000-0000-0000-0000-000000000002', '28000000-0000-0000-0000-000000000002'),
('47000000-0000-0000-0000-000000000005', '46000000-0000-0000-0000-000000000003', '28000000-0000-0000-0000-000000000002')
on conflict (id_seleccion_menu) do nothing;

insert into item_menu (id_item_menu, id_seleccion_menu, id_plato, cantidad, excepciones) values
('48000000-0000-0000-0000-000000000001', '47000000-0000-0000-0000-000000000001', '29000000-0000-0000-0000-000000000001', 120, null),
('48000000-0000-0000-0000-000000000002', '47000000-0000-0000-0000-000000000002', '29000000-0000-0000-0000-000000000002', 112, '8 vegetarianos'),
('48000000-0000-0000-0000-000000000003', '47000000-0000-0000-0000-000000000003', '29000000-0000-0000-0000-000000000003', 120, null),
('48000000-0000-0000-0000-000000000004', '47000000-0000-0000-0000-000000000004', '29000000-0000-0000-0000-000000000002', 70, null),
('48000000-0000-0000-0000-000000000005', '47000000-0000-0000-0000-000000000005', '29000000-0000-0000-0000-000000000004', 20, null)
on conflict (id_item_menu) do nothing;

insert into cotizacion (id_cotizacion, id_reserva, id_usuario, estado, valor_subtotal, descuento, valor_total, observaciones, created_at, updated_at, vigente) values
('50000000-0000-0000-0000-000000000001', '41000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', 'ACEPTADA', 10000000.00, 500000.00, 9500000.00, 'Cotizacion aceptada con anticipo parcial.', now(), now(), true),
('50000000-0000-0000-0000-000000000002', '41000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 'GENERADA', 5200000.00, 0.00, 5200000.00, 'Cotizacion generada para probar PDF y envio.', now(), now(), true),
('50000000-0000-0000-0000-000000000003', '41000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002', 'ACEPTADA', 2500000.00, 0.00, 2500000.00, 'Cotizacion aceptada y pagada totalmente.', now(), now(), true),
('50000000-0000-0000-0000-000000000004', '41000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000002', 'BORRADOR', 0.00, 0.00, 0.00, 'Borrador para probar restriccion de descarga.', now(), now(), true)
on conflict (id_cotizacion) do nothing;

insert into cotizacion_item (id_cotizacion_item, id_cotizacion, tipo_concepto, origen_id, descripcion, precio_base, precio_override, cantidad, subtotal) values
('51000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'MENU', '29000000-0000-0000-0000-000000000002', 'Lomo en salsa de vino', 62000.00, 65000.00, 120, 7800000.00),
('51000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000001', 'ADICIONAL', '27000000-0000-0000-0000-000000000001', 'Sonido basico', 350000.00, null, 1, 350000.00),
('51000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000001', 'MONTAJE', '42000000-0000-0000-0000-000000000001', 'Montaje y servicio', 1850000.00, null, 1, 1850000.00),
('51000000-0000-0000-0000-000000000004', '50000000-0000-0000-0000-000000000002', 'MENU', '29000000-0000-0000-0000-000000000002', 'Almuerzo corporativo', 62000.00, null, 70, 4340000.00),
('51000000-0000-0000-0000-000000000005', '50000000-0000-0000-0000-000000000002', 'ADICIONAL', '27000000-0000-0000-0000-000000000002', 'Estacion de cafe', 12000.00, null, 70, 840000.00),
('51000000-0000-0000-0000-000000000006', '50000000-0000-0000-0000-000000000003', 'MENU', '29000000-0000-0000-0000-000000000004', 'Menu celebracion', 28000.00, null, 55, 1540000.00),
('51000000-0000-0000-0000-000000000007', '50000000-0000-0000-0000-000000000003', 'ADICIONAL', '27000000-0000-0000-0000-000000000003', 'Decoracion floral', 500000.00, null, 1, 500000.00),
('51000000-0000-0000-0000-000000000008', '50000000-0000-0000-0000-000000000003', 'MONTAJE', '42000000-0000-0000-0000-000000000003', 'Montaje terraza', 460000.00, null, 1, 460000.00)
on conflict (id_cotizacion_item) do nothing;

insert into anticipo (id_anticipo, id_cotizacion, id_usuario, valor, metodo_pago, fecha_pago, observaciones) values
('52000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000003', 2500000.00, 'TRANSFERENCIA', current_date - interval '2 days', 'Primer anticipo'),
('52000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', 2500000.00, 'TARJETA', current_date - interval '1 days', 'Pago total')
on conflict (id_anticipo) do nothing;

insert into historial_estado_evento (id_historial, id_evento, id_usuario, estado_anterior, estado_nuevo, motivo, created_at) values
('53000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', 'COTIZACION_ENVIADA', 'PENDIENTE_ANTICIPO', null, now()),
('53000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002', 'COTIZACION_APROBADA', 'CONFIRMADO', null, now()),
('53000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000001', 'PENDIENTE', 'CANCELADO', 'Cancelacion de prueba local', now())
on conflict (id_historial) do nothing;

insert into prueba_plato (id_prueba_plato, id_evento, fecha_realizacion, estado, created_at, updated_at) values
('54000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', current_date + interval '7 days' + time '15:00', 'PROGRAMADA', now(), now()),
('54000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000003', current_date + interval '12 days' + time '16:00', 'PROGRAMADA', now(), now())
on conflict (id_prueba_plato) do nothing;

insert into notificacion (id_notificacion, id_evento, tipo, fecha_programada, fecha_envio, estado, intentos, payload_json, created_at, updated_at) values
('55000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', 'RECORDATORIO_ANTICIPO', current_date + interval '3 days' + time '09:00', null, 'PENDIENTE', 0, '{"tipo":"RECORDATORIO_ANTICIPO","demo":true}', now(), now()),
('55000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000002', 'COTIZACION_CLIENTE', now(), null, 'PENDIENTE', 0, '{"tipo":"COTIZACION_CLIENTE","cotizacionId":"50000000-0000-0000-0000-000000000002"}', now(), now())
on conflict (id_notificacion) do nothing;

insert into notificacion_destinatario (id_notificacion_destinatario, id_notificacion, id_usuario, telefono, correo, estado) values
('56000000-0000-0000-0000-000000000001', '55000000-0000-0000-0000-000000000001', null, '3002223344', 'laura.martinez@test.com', 'PENDIENTE'),
('56000000-0000-0000-0000-000000000002', '55000000-0000-0000-0000-000000000002', null, '3001112233', 'eventos@andina.test', 'PENDIENTE')
on conflict (id_notificacion_destinatario) do nothing;

insert into evento_calendar (id_evento_calendar, origen_tipo, origen_id, id_evento, tipo, google_event_id, fecha_sync, estado, payload_json, intentos, mensaje_error, created_at, updated_at) values
('57000000-0000-0000-0000-000000000001', 'EVENTO', '40000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000003', 'CREAR', 'local-demo-calendar-1', now(), 'SINCRONIZADO', '{"demo":true}', 0, null, now(), now()),
('57000000-0000-0000-0000-000000000002', 'PRUEBA_PLATO', '54000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', 'CREAR', null, null, 'PENDIENTE', '{"demo":true}', 0, null, now(), now())
on conflict (id_evento_calendar) do nothing;

insert into recordatorio_anticipo (id_recordatorio_anticipo, id_evento, id_usuario, fecha_recordatorio, estado, id_notificacion, created_at, updated_at) values
('58000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000003', current_date + interval '3 days', 'PENDIENTE', '55000000-0000-0000-0000-000000000001', now(), now())
on conflict (id_recordatorio_anticipo) do nothing;
