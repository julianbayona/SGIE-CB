-- Seed minimo para probar Google Calendar desde una base vacia.
-- Ejecutar despues de que Flyway haya creado las tablas.

insert into usuario (
    id_usuario,
    nombre,
    contrasena_hash,
    rol,
    activo,
    created_at,
    updated_at
) values (
    '00000000-0000-0000-0000-000000000001',
    'Administrador Prueba',
    '$2a$10$abcdefghijklmnopqrstuuuuuuuuuuuuuuuuuuuuuuuuuuuuu',
    'ADMINISTRADOR',
    true,
    now(),
    now()
) on conflict (id_usuario) do nothing;

insert into tipo_evento (
    id_tipo_evento,
    nombre,
    descripcion,
    activo,
    created_at,
    updated_at
) values (
    '00000000-0000-0000-0000-000000000002',
    'Prueba de plato',
    'Tipo de evento para validar Google Calendar',
    true,
    now(),
    now()
) on conflict (id_tipo_evento) do nothing;

insert into tipo_comida (
    id_tipo_comida,
    nombre,
    descripcion,
    activo,
    created_at,
    updated_at
) values (
    '00000000-0000-0000-0000-000000000003',
    'Almuerzo',
    'Tipo de comida para prueba',
    true,
    now(),
    now()
) on conflict (id_tipo_comida) do nothing;

insert into cliente (
    id_cliente,
    cedula,
    nombre_completo,
    telefono,
    correo,
    tipo_cliente,
    activo,
    creado_por,
    created_at,
    updated_at
) values (
    '00000000-0000-0000-0000-000000000004',
    '1000000001',
    'Cliente Prueba Calendar',
    '573001112233',
    'julianbayona0315@gmail.com',
    'NO_SOCIO',
    true,
    '00000000-0000-0000-0000-000000000001',
    now(),
    now()
) on conflict (id_cliente) do nothing;

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
    '00000000-0000-0000-0000-000000000005',
    '00000000-0000-0000-0000-000000000004',
    '00000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000003',
    '00000000-0000-0000-0000-000000000001',
    '2026-05-05 10:00:00',
    '2026-05-05 12:00:00',
    'PENDIENTE',
    null,
    now(),
    now()
) on conflict (id_evento) do nothing;

select
    'usuarioId' as dato,
    '00000000-0000-0000-0000-000000000001' as valor
union all
select
    'eventoId',
    '00000000-0000-0000-0000-000000000005';
