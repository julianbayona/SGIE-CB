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

create table color (
    id_color uuid primary key,
    nombre varchar(120) not null,
    codigo_hex varchar(7) not null,
    activo boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table tipo_mesa (
    id_tipo_mesa uuid primary key,
    nombre varchar(120) not null,
    activo boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table tipo_silla (
    id_tipo_silla uuid primary key,
    nombre varchar(120) not null,
    activo boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table mantel (
    id_mantel uuid primary key,
    nombre varchar(120) not null,
    id_color uuid not null references color(id_color),
    activo boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table sobremantel (
    id_sobremantel uuid primary key,
    nombre varchar(120) not null,
    id_color uuid not null references color(id_color),
    activo boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table tipo_adicional (
    id_tipo_adicional uuid primary key,
    nombre varchar(120) not null,
    modo_cobro varchar(40) not null check (modo_cobro in ('UNIDAD', 'SERVICIO')),
    precio_base numeric(12,2) not null,
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
    version integer not null,
    vigente boolean not null default true,
    creado_por uuid not null references usuario(id_usuario),
    created_at timestamp not null,
    updated_at timestamp not null,
    unique (id_reserva, reserva_raiz_id)
);

create table montaje (
    id_montaje uuid primary key,
    id_reserva uuid not null unique references reserva_salon(id_reserva),
    observaciones varchar(500),
    created_at timestamp not null,
    updated_at timestamp not null
);

create table montaje_mesas_reserva (
    id_montaje_mesa uuid primary key,
    id_montaje uuid not null references montaje(id_montaje),
    id_tipo_mesa uuid not null references tipo_mesa(id_tipo_mesa),
    id_tipo_silla uuid not null references tipo_silla(id_tipo_silla),
    silla_por_mesa integer not null,
    cantidad_mesas integer not null,
    id_mantel uuid not null references mantel(id_mantel),
    id_sobremantel uuid references sobremantel(id_sobremantel),
    vajilla boolean not null default false,
    fajon boolean not null default false
);

create table infraestructura_reserva (
    id_infra_reserva uuid primary key,
    id_montaje uuid not null unique references montaje(id_montaje),
    mesa_ponque boolean not null default false,
    mesa_regalos boolean not null default false,
    espacio_musicos boolean not null default false,
    estante_bombas boolean not null default false
);

create table adicional_evento (
    id_adicional_evento uuid primary key,
    id_montaje uuid not null references montaje(id_montaje),
    id_tipo_adicional uuid not null references tipo_adicional(id_tipo_adicional),
    cantidad integer not null,
    precio_override numeric(12,2)
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
create index idx_color_nombre on color (nombre);
create index idx_tipo_silla_nombre on tipo_silla (nombre);
create index idx_mantel_nombre on mantel (nombre);
create index idx_mantel_color on mantel (id_color);
create index idx_sobremantel_nombre on sobremantel (nombre);
create index idx_sobremantel_color on sobremantel (id_color);
create index idx_tipo_adicional_nombre on tipo_adicional (nombre);
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
create index idx_reserva_salon_rango on reserva_salon (id_salon, vigente, fecha_hora_inicio, fecha_hora_fin);
create index idx_montaje_reserva on montaje (id_reserva);
create index idx_montaje_mesas_montaje on montaje_mesas_reserva (id_montaje);
create index idx_infraestructura_montaje on infraestructura_reserva (id_montaje);
create index idx_adicional_evento_montaje on adicional_evento (id_montaje);
create index idx_adicional_evento_tipo on adicional_evento (id_tipo_adicional);
create index idx_historial_evento_fecha on historial_estado_evento (id_evento, created_at);
