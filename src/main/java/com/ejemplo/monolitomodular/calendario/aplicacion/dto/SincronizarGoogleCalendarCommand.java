package com.ejemplo.monolitomodular.calendario.aplicacion.dto;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;

import java.util.UUID;

public record SincronizarGoogleCalendarCommand(
        UUID eventoCalendarId,
        TipoOperacionCalendar tipo,
        String googleEventId,
        String payloadJson
) {
}
