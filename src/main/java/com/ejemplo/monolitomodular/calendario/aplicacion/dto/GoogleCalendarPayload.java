package com.ejemplo.monolitomodular.calendario.aplicacion.dto;

import java.time.LocalDateTime;
import java.util.List;

public record GoogleCalendarPayload(
        String resumen,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        String origen,
        List<Attendee> attendees
) {

    public GoogleCalendarPayload {
        attendees = attendees == null ? List.of() : attendees;
    }

    public record Attendee(String email) {
    }
}
