package com.ejemplo.monolitomodular.calendario.dominio.puerto.salida;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;

import java.util.List;

public interface EventoCalendarRepository {

    EventoCalendar guardar(EventoCalendar eventoCalendar);

    List<EventoCalendar> buscarPendientes(int limite);
}
