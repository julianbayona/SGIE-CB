package com.ejemplo.monolitomodular.calendario.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.calendario.aplicacion.dto.EventoCalendarView;

import java.util.UUID;

public interface ReintentarEventoCalendarUseCase {

    EventoCalendarView reintentar(UUID eventoCalendarId);
}
