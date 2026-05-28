alter table cotizacion_item
    add column if not exists concepto_codigo varchar(60),
    add column if not exists origen_tipo varchar(40),
    add column if not exists descripcion_snapshot varchar(500),
    add column if not exists precio_base_snapshot numeric(12,2);

update cotizacion_item
set concepto_codigo = coalesce(concepto_codigo, tipo_concepto),
    origen_tipo = coalesce(
        origen_tipo,
        case
            when upper(tipo_concepto) like '%SALON%' or upper(tipo_concepto) like '%ALQUILER%' then 'SALON'
            when upper(tipo_concepto) like '%MENU%' or upper(tipo_concepto) like '%PLATO%' then 'MENU'
            when upper(tipo_concepto) like '%ADICIONAL%' then 'ADICIONAL'
            when upper(tipo_concepto) like '%MONTAJE%' then 'MONTAJE'
            else 'OTRO'
        end
    ),
    descripcion_snapshot = coalesce(descripcion_snapshot, descripcion),
    precio_base_snapshot = coalesce(precio_base_snapshot, precio_base);

create index if not exists idx_cotizacion_item_concepto_codigo on cotizacion_item (concepto_codigo);
create index if not exists idx_cotizacion_item_origen_normalizado on cotizacion_item (origen_tipo, origen_id);

create table if not exists infraestructura_reserva_item (
    id_infraestructura_item uuid primary key,
    id_infra_reserva uuid not null references infraestructura_reserva(id_infra_reserva),
    id_montaje uuid not null references montaje(id_montaje),
    tipo varchar(60) not null,
    cantidad integer not null default 1,
    observaciones varchar(500),
    created_at timestamp not null default now(),
    unique (id_infra_reserva, tipo)
);

insert into infraestructura_reserva_item (
    id_infraestructura_item,
    id_infra_reserva,
    id_montaje,
    tipo,
    cantidad,
    observaciones
)
select gen_random_uuid(), id_infra_reserva, id_montaje, 'MESA_PONQUE', 1, null
from infraestructura_reserva
where mesa_ponque = true
on conflict (id_infra_reserva, tipo) do nothing;

insert into infraestructura_reserva_item (
    id_infraestructura_item,
    id_infra_reserva,
    id_montaje,
    tipo,
    cantidad,
    observaciones
)
select gen_random_uuid(), id_infra_reserva, id_montaje, 'MESA_REGALOS', 1, null
from infraestructura_reserva
where mesa_regalos = true
on conflict (id_infra_reserva, tipo) do nothing;

insert into infraestructura_reserva_item (
    id_infraestructura_item,
    id_infra_reserva,
    id_montaje,
    tipo,
    cantidad,
    observaciones
)
select gen_random_uuid(), id_infra_reserva, id_montaje, 'ESPACIO_MUSICOS', 1, null
from infraestructura_reserva
where espacio_musicos = true
on conflict (id_infra_reserva, tipo) do nothing;

insert into infraestructura_reserva_item (
    id_infraestructura_item,
    id_infra_reserva,
    id_montaje,
    tipo,
    cantidad,
    observaciones
)
select gen_random_uuid(), id_infra_reserva, id_montaje, 'ESTANTE_BOMBAS', 1, null
from infraestructura_reserva
where estante_bombas = true
on conflict (id_infra_reserva, tipo) do nothing;

create index if not exists idx_infraestructura_item_infra on infraestructura_reserva_item (id_infra_reserva);
create index if not exists idx_infraestructura_item_montaje on infraestructura_reserva_item (id_montaje);
