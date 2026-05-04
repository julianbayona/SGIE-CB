package com.ejemplo.monolitomodular.notificaciones.aplicacion.dto;

public record EnviarWhatsAppResult(
        boolean exitoso,
        String mensaje
) {

    public static EnviarWhatsAppResult ok() {
        return new EnviarWhatsAppResult(true, null);
    }

    public static EnviarWhatsAppResult error(String mensaje) {
        return new EnviarWhatsAppResult(false, mensaje);
    }
}
