package com.ejemplo.monolitomodular.calendario.dominio.puerto.salida;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventoCalendarRepository {

    EventoCalendar guardar(EventoCalendar eventoCalendar);

    Optional<EventoCalendar> buscarPorId(UUID id);

    List<EventoCalendar> buscarPendientes(int limite);

    default List<EventoCalendar> buscarPorEventoId(UUID eventoId) {
        return List.of();
    }

    default List<EventoCalendar> buscarSincronizadosCancelablesPorEventoId(UUID eventoId) {
        return List.of();
    }

    default void cancelarPendientesPorEventoId(UUID eventoId) {
    }

    default void cancelarPendientesPorEventoYOrigen(UUID eventoId, OrigenEventoCalendar origenTipo) {
    }
}
