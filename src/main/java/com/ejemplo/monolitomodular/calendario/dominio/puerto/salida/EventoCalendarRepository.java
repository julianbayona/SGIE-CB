package com.ejemplo.monolitomodular.calendario.dominio.puerto.salida;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventoCalendarRepository {

    EventoCalendar guardar(EventoCalendar eventoCalendar);

    Optional<EventoCalendar> buscarPorId(UUID id);

    List<EventoCalendar> buscarPendientes(int limite);
}
