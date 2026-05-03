alter table cotizacion
    add column vigente boolean not null default true;

create index idx_cotizacion_reserva_vigente on cotizacion (id_reserva, vigente);
