create table tipo_notificacion (
    id_tipo_notificacion uuid primary key,
    codigo varchar(80) not null unique,
    descripcion varchar(255),
    canal varchar(40) not null,
    es_programable boolean not null default true
);

create table notificacion (
    id_notificacion uuid primary key,
    id_evento uuid references evento(id_evento),
    id_tipo_notificacion uuid not null references tipo_notificacion(id_tipo_notificacion),
    fecha_programada timestamp not null,
    fecha_envio timestamp,
    estado varchar(40) not null,
    intentos integer not null default 0,
    payload_json text not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table notificacion_destinatario (
    id_notificacion_destinatario uuid primary key,
    id_notificacion uuid not null references notificacion(id_notificacion),
    id_usuario uuid references usuario(id_usuario),
    telefono varchar(30) not null,
    estado varchar(40) not null
);

create index idx_tipo_notificacion_codigo on tipo_notificacion (codigo);
create index idx_notificacion_pendientes on notificacion (estado, fecha_programada, intentos);
create index idx_notificacion_evento on notificacion (id_evento);
create index idx_notificacion_destinatario_notificacion on notificacion_destinatario (id_notificacion);
