create table if not exists cotizacion_reserva (
    id_cotizacion uuid not null references cotizacion(id_cotizacion),
    id_reserva uuid not null references reserva_salon(id_reserva),
    created_at timestamp not null default current_timestamp,
    primary key (id_cotizacion, id_reserva)
);

create index if not exists idx_cotizacion_reserva_reserva
    on cotizacion_reserva (id_reserva);
