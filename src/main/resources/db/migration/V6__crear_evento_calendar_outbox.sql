create table evento_calendar (
    id_evento_calendar uuid primary key,
    origen_tipo varchar(60) not null,
    origen_id uuid not null,
    id_evento uuid not null references evento(id_evento),
    tipo varchar(60) not null,
    google_event_id varchar(255),
    fecha_sync timestamp,
    estado varchar(40) not null,
    payload_json text not null,
    intentos integer not null default 0,
    created_at timestamp not null,
    updated_at timestamp not null
);

create index idx_evento_calendar_evento on evento_calendar (id_evento);
create index idx_evento_calendar_origen on evento_calendar (origen_tipo, origen_id);
create index idx_evento_calendar_pendientes on evento_calendar (estado, tipo, intentos);
