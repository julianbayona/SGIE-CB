create table cliente (
    id uuid primary key,
    cedula varchar(20) not null unique,
    nombre varchar(120) not null,
    telefono varchar(30) not null,
    correo varchar(120) not null,
    tipo_cliente varchar(20) not null,
    activo boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table salon (
    id uuid primary key,
    nombre varchar(120) not null unique,
    capacidad integer not null,
    descripcion varchar(255),
    activo boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table usuario (
    id uuid primary key,
    nombre varchar(120) not null,
    email varchar(120) not null unique,
    password_hash varchar(255) not null,
    rol varchar(30) not null,
    activo boolean not null default true,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table evento (
    id uuid primary key,
    cliente_id uuid not null references cliente(id),
    tipo_evento varchar(100) not null,
    tipo_comida varchar(100) not null,
    fecha_evento date not null,
    hora_inicio time not null,
    hora_fin time not null,
    numero_personas integer not null,
    estado varchar(40) not null,
    observaciones text,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table reserva_salon (
    id uuid primary key,
    evento_id uuid not null references evento(id),
    salon_id uuid not null references salon(id),
    fecha_inicio timestamp not null,
    fecha_fin timestamp not null,
    created_at timestamp not null,
    unique (evento_id, salon_id)
);

create table historial_estado_evento (
    id uuid primary key,
    evento_id uuid not null references evento(id),
    usuario_id uuid references usuario(id),
    estado_anterior varchar(40),
    estado_nuevo varchar(40) not null,
    observacion varchar(255),
    fecha_cambio timestamp not null
);

create index idx_cliente_cedula on cliente (cedula);
create index idx_salon_nombre on salon (nombre);
create index idx_usuario_email on usuario (email);
create index idx_evento_cliente on evento (cliente_id);
create index idx_evento_fecha on evento (fecha_evento);
create index idx_evento_estado on evento (estado);
create index idx_reserva_salon_rango on reserva_salon (salon_id, fecha_inicio, fecha_fin);
create index idx_historial_evento_fecha on historial_estado_evento (evento_id, fecha_cambio);
