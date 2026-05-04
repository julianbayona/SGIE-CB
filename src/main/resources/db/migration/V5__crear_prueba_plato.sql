create table prueba_plato (
    id_prueba_plato uuid primary key,
    id_evento uuid not null references evento(id_evento),
    fecha_realizacion timestamp not null,
    estado varchar(40) not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

create index idx_prueba_plato_evento on prueba_plato (id_evento);
create index idx_prueba_plato_fecha on prueba_plato (fecha_realizacion);
