create table usuario (
    id_usuario uuid primary key,
    nombre varchar(120) not null,
    contrasena_hash varchar(255) not null,
    rol varchar(30) not null,
    activo boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table tipo_evento (
    id_tipo_evento uuid primary key,
    nombre varchar(120) not null,
    descripcion varchar(255),
    activo boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table tipo_comida (
    id_tipo_comida uuid primary key,
    nombre varchar(120) not null,
    descripcion varchar(255),
    activo boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table cliente (
    id_cliente uuid primary key,
    cedula varchar(20) not null unique,
    nombre_completo varchar(120) not null,
    telefono varchar(30) not null,
    correo varchar(120) not null,
    tipo_cliente varchar(20) not null,
    activo boolean not null default true,
    creado_por uuid references usuario(id_usuario),
    created_at timestamp not null,
    updated_at timestamp not null
);

create table salon (
    id_salon uuid primary key,
    nombre varchar(120) not null unique,
    capacidad_max integer not null,
    descripcion varchar(255),
    activo boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table evento (
    id_evento uuid primary key,
    id_cliente uuid not null references cliente(id_cliente),
    id_tipo_evento uuid not null references tipo_evento(id_tipo_evento),
    id_tipo_comida uuid not null references tipo_comida(id_tipo_comida),
    id_usuario_creador uuid not null references usuario(id_usuario),
    fecha_hora_inicio timestamp not null,
    fecha_hora_fin timestamp not null,
    estado varchar(40) not null,
    gcal_event_id varchar(255),
    created_at timestamp not null,
    updated_at timestamp not null
);

create table reserva_salon (
    id_reserva uuid primary key,
    reserva_raiz_id uuid not null,
    id_evento uuid not null references evento(id_evento),
    id_salon uuid not null references salon(id_salon),
    num_invitados integer not null,
    fecha_hora_inicio timestamp not null,
    fecha_hora_fin timestamp not null,
    estado varchar(40) not null,
    version integer not null,
    vigente boolean not null default true,
    creado_por uuid not null references usuario(id_usuario),
    created_at timestamp not null,
    updated_at timestamp not null,
    unique (id_reserva, reserva_raiz_id)
);

create table historial_estado_evento (
    id_historial uuid primary key,
    id_evento uuid not null references evento(id_evento),
    id_usuario uuid not null references usuario(id_usuario),
    estado_anterior varchar(40),
    estado_nuevo varchar(40) not null,
    created_at timestamp not null
);

create index idx_usuario_nombre on usuario (nombre);
create index idx_tipo_evento_nombre on tipo_evento (nombre);
create index idx_tipo_comida_nombre on tipo_comida (nombre);
create index idx_cliente_cedula on cliente (cedula);
create index idx_salon_nombre on salon (nombre);
create index idx_evento_cliente on evento (id_cliente);
create index idx_evento_tipo_evento on evento (id_tipo_evento);
create index idx_evento_tipo_comida on evento (id_tipo_comida);
create index idx_evento_usuario_creador on evento (id_usuario_creador);
create index idx_evento_estado on evento (estado);
create index idx_evento_rango on evento (fecha_hora_inicio, fecha_hora_fin);
create index idx_reserva_salon_raiz on reserva_salon (reserva_raiz_id, version);
create index idx_reserva_salon_vigente_evento on reserva_salon (id_evento, vigente);
create index idx_reserva_salon_vigente_evento_salon on reserva_salon (id_evento, id_salon, vigente);
create index idx_reserva_salon_rango on reserva_salon (id_salon, vigente, estado, fecha_hora_inicio, fecha_hora_fin);
create index idx_historial_evento_fecha on historial_estado_evento (id_evento, created_at);
