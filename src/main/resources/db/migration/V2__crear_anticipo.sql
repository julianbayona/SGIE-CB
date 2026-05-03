create table anticipo (
    id_anticipo uuid primary key,
    id_cotizacion uuid not null references cotizacion(id_cotizacion),
    id_usuario uuid not null references usuario(id_usuario),
    valor numeric(12,2) not null,
    metodo_pago varchar(60) not null,
    fecha_pago date not null,
    observaciones varchar(500)
);

create index idx_anticipo_cotizacion on anticipo (id_cotizacion);
create index idx_anticipo_usuario on anticipo (id_usuario);
