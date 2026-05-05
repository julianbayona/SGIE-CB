-- Seed minimo para probar confirmacion explicita de evento + observers.
-- Ejecutar despues de Flyway.

insert into usuario (id_usuario, nombre, contrasena_hash, rol, activo, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000001',
    'Administrador Confirmacion',
    '$2a$10$abcdefghijklmnopqrstuuuuuuuuuuuuuuuuuuuuuuuuuuuuu',
    'ADMINISTRADOR',
    true,
    now(),
    now()
) on conflict (id_usuario) do nothing;

insert into tipo_evento (id_tipo_evento, nombre, descripcion, activo, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000002',
    'Capacitacion',
    'Tipo de evento para prueba de confirmacion',
    true,
    now(),
    now()
) on conflict (id_tipo_evento) do nothing;

insert into tipo_comida (id_tipo_comida, nombre, descripcion, activo, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000003',
    'Almuerzo',
    'Tipo de comida para prueba de confirmacion',
    true,
    now(),
    now()
) on conflict (id_tipo_comida) do nothing;

insert into cliente (
    id_cliente, cedula, nombre_completo, telefono, correo, tipo_cliente, activo, creado_por, created_at, updated_at
) values (
    '10000000-0000-0000-0000-000000000004',
    '2000000001',
    'Cliente Evento Confirmado',
    '573009998877',
    'julianbayona0315@gmail.com',
    'NO_SOCIO',
    true,
    '10000000-0000-0000-0000-000000000001',
    now(),
    now()
) on conflict (id_cliente) do nothing;

insert into salon (id_salon, id, nombre, capacidad_max, capacidad, descripcion, activo, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000005',
    '10000000-0000-0000-0000-000000000005',
    'Salon Confirmacion',
    120,
    120,
    'Salon para prueba de evento confirmado',
    true,
    now(),
    now()
) on conflict (id_salon) do nothing;

insert into evento (
    id_evento,
    id_cliente,
    id_tipo_evento,
    id_tipo_comida,
    id_usuario_creador,
    fecha_hora_inicio,
    fecha_hora_fin,
    estado,
    gcal_event_id,
    created_at,
    updated_at
) values (
    '10000000-0000-0000-0000-000000000006',
    '10000000-0000-0000-0000-000000000004',
    '10000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000003',
    '10000000-0000-0000-0000-000000000001',
    '2026-05-06 08:00:00',
    '2026-05-06 12:00:00',
    'PENDIENTE',
    null,
    now(),
    now()
) on conflict (id_evento) do nothing;

insert into reserva_salon (
    id_reserva,
    reserva_raiz_id,
    id_evento,
    id_salon,
    num_invitados,
    fecha_hora_inicio,
    fecha_hora_fin,
    version,
    vigente,
    creado_por,
    created_at,
    updated_at
) values (
    '10000000-0000-0000-0000-000000000007',
    '10000000-0000-0000-0000-000000000007',
    '10000000-0000-0000-0000-000000000006',
    '10000000-0000-0000-0000-000000000005',
    80,
    '2026-05-06 08:00:00',
    '2026-05-06 12:00:00',
    1,
    true,
    '10000000-0000-0000-0000-000000000001',
    now(),
    now()
) on conflict (id_reserva) do nothing;

insert into color (id_color, nombre, codigo_hex, activo, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000008',
    'Blanco Confirmacion',
    '#FFFFFF',
    true,
    now(),
    now()
) on conflict (id_color) do nothing;

insert into tipo_mesa (id_tipo_mesa, nombre, activo, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000009',
    'Redonda',
    true,
    now(),
    now()
) on conflict (id_tipo_mesa) do nothing;

insert into tipo_silla (id_tipo_silla, nombre, activo, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000010',
    'Tiffany',
    true,
    now(),
    now()
) on conflict (id_tipo_silla) do nothing;

insert into mantel (id_mantel, nombre, id_color, activo, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000011',
    'Mantel Blanco',
    '10000000-0000-0000-0000-000000000008',
    true,
    now(),
    now()
) on conflict (id_mantel) do nothing;

insert into tipo_adicional (id_tipo_adicional, nombre, modo_cobro, precio_base, activo, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000012',
    'Sonido basico',
    'SERVICIO',
    150000.00,
    true,
    now(),
    now()
) on conflict (id_tipo_adicional) do nothing;

insert into montaje (id_montaje, id_reserva, observaciones, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000013',
    '10000000-0000-0000-0000-000000000007',
    'Montaje de prueba para confirmacion',
    now(),
    now()
) on conflict (id_montaje) do nothing;

insert into montaje_mesas_reserva (
    id_montaje_mesa,
    id_montaje,
    id_tipo_mesa,
    id_tipo_silla,
    silla_por_mesa,
    cantidad_mesas,
    id_mantel,
    id_sobremantel,
    vajilla,
    fajon
) values (
    '10000000-0000-0000-0000-000000000014',
    '10000000-0000-0000-0000-000000000013',
    '10000000-0000-0000-0000-000000000009',
    '10000000-0000-0000-0000-000000000010',
    8,
    10,
    '10000000-0000-0000-0000-000000000011',
    null,
    true,
    false
) on conflict (id_montaje_mesa) do nothing;

insert into infraestructura_reserva (
    id_infra_reserva, id_montaje, mesa_ponque, mesa_regalos, espacio_musicos, estante_bombas
) values (
    '10000000-0000-0000-0000-000000000015',
    '10000000-0000-0000-0000-000000000013',
    false,
    false,
    true,
    false
) on conflict (id_infra_reserva) do nothing;

insert into adicional_evento (id_adicional_evento, id_montaje, id_tipo_adicional, cantidad)
values (
    '10000000-0000-0000-0000-000000000016',
    '10000000-0000-0000-0000-000000000013',
    '10000000-0000-0000-0000-000000000012',
    1
) on conflict (id_adicional_evento) do nothing;

insert into tipo_momento_menu (id_tipo_momento, nombre, activo, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000017',
    'Almuerzo',
    true,
    now(),
    now()
) on conflict (id_tipo_momento) do nothing;

insert into plato (id_plato, nombre, descripcion, precio_base, activo, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000018',
    'Almuerzo ejecutivo',
    'Plato para prueba de confirmacion',
    25000.00,
    true,
    now(),
    now()
) on conflict (id_plato) do nothing;

insert into plato_momento (id_plato, id_tipo_momento)
values (
    '10000000-0000-0000-0000-000000000018',
    '10000000-0000-0000-0000-000000000017'
) on conflict (id_plato, id_tipo_momento) do nothing;

insert into menu (id_menu, id_reserva, notas_generales, created_at, updated_at)
values (
    '10000000-0000-0000-0000-000000000019',
    '10000000-0000-0000-0000-000000000007',
    'Menu de prueba para confirmacion',
    now(),
    now()
) on conflict (id_menu) do nothing;

insert into seleccion_menu (id_seleccion_menu, id_menu, id_tipo_momento)
values (
    '10000000-0000-0000-0000-000000000020',
    '10000000-0000-0000-0000-000000000019',
    '10000000-0000-0000-0000-000000000017'
) on conflict (id_seleccion_menu) do nothing;

insert into item_menu (id_item_menu, id_seleccion_menu, id_plato, cantidad, excepciones)
values (
    '10000000-0000-0000-0000-000000000021',
    '10000000-0000-0000-0000-000000000020',
    '10000000-0000-0000-0000-000000000018',
    80,
    null
) on conflict (id_item_menu) do nothing;

select 'usuarioId' as dato, '10000000-0000-0000-0000-000000000001' as valor
union all
select 'eventoId', '10000000-0000-0000-0000-000000000006'
union all
select 'reservaRaizId', '10000000-0000-0000-0000-000000000007';
