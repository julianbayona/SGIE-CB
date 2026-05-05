create table recordatorio_anticipo (
    id_recordatorio_anticipo uuid primary key,
    id_evento uuid not null references evento(id_evento),
    id_usuario uuid not null references usuario(id_usuario),
    fecha_recordatorio date not null,
    estado varchar(40) not null,
    id_notificacion uuid references notificacion(id_notificacion),
    created_at timestamp not null,
    updated_at timestamp not null
);

create index idx_recordatorio_anticipo_pendientes on recordatorio_anticipo (estado, fecha_recordatorio);
create index idx_recordatorio_anticipo_evento on recordatorio_anticipo (id_evento);
