package com.ejemplo.monolitomodular.calendario.aplicacion.dto;

public record SincronizarGoogleCalendarResult(
        boolean exitoso,
        String googleEventId,
        String mensaje
) {

    public static SincronizarGoogleCalendarResult ok(String googleEventId) {
        return new SincronizarGoogleCalendarResult(true, googleEventId, null);
    }

    public static SincronizarGoogleCalendarResult error(String mensaje) {
        return new SincronizarGoogleCalendarResult(false, null, mensaje);
    }
}
