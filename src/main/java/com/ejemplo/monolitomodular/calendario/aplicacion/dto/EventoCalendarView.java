package com.ejemplo.monolitomodular.calendario.aplicacion.dto;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventoCalendarView(
        UUID id,
        OrigenEventoCalendar origenTipo,
        UUID origenId,
        UUID eventoId,
        TipoOperacionCalendar tipo,
        String googleEventId,
        LocalDateTime fechaSync,
        EstadoEventoCalendar estado,
        int intentos,
        String mensajeError
) {
}
