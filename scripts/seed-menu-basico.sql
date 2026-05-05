insert into tipo_momento_menu (id_tipo_momento, nombre, activo, created_at, updated_at)
values (
    '20000000-0000-0000-0000-000000000001',
    'Plato fuerte',
    true,
    now(),
    now()
)
on conflict (id_tipo_momento) do update
set nombre = excluded.nombre,
    activo = true,
    updated_at = now();

insert into plato (id_plato, nombre, descripcion, precio_base, activo, created_at, updated_at)
values (
    '20000000-0000-0000-0000-000000000002',
    'Pollo en salsa de champinones',
    'Plato base para pruebas E2E',
    28000,
    true,
    now(),
    now()
)
on conflict (id_plato) do update
set nombre = excluded.nombre,
    descripcion = excluded.descripcion,
    precio_base = excluded.precio_base,
    activo = true,
    updated_at = now();

insert into plato_momento (id_plato, id_tipo_momento)
values (
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000001'
)
on conflict (id_plato, id_tipo_momento) do nothing;
