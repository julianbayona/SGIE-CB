alter table reserva_salon
    add column if not exists activa boolean not null default true;

create index if not exists idx_reserva_salon_operativa_evento
    on reserva_salon (id_evento, vigente, activa);

create index if not exists idx_reserva_salon_operativa_rango
    on reserva_salon (id_salon, vigente, activa, fecha_hora_inicio, fecha_hora_fin);
