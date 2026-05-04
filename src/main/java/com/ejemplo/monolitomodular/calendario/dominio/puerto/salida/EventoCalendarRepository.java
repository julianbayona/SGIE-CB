package com.ejemplo.monolitomodular.calendario.dominio.puerto.salida;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;

public interface EventoCalendarRepository {

    EventoCalendar guardar(EventoCalendar eventoCalendar);
}
