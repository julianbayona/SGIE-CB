package com.ejemplo.monolitomodular.notificaciones.aplicacion.dto;

public record EnviarEmailResult(
        boolean exitoso,
        String mensaje
) {

    public static EnviarEmailResult ok() {
        return new EnviarEmailResult(true, null);
    }

    public static EnviarEmailResult error(String mensaje) {
        return new EnviarEmailResult(false, mensaje);
    }
}
