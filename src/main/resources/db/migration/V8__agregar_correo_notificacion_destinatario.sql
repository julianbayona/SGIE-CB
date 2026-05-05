alter table notificacion_destinatario
    add column correo varchar(120);

alter table notificacion_destinatario
    alter column telefono drop not null;
