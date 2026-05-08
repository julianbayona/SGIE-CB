package com.ejemplo.monolitomodular.calendario.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.calendario.aplicacion.dto.EventoCalendarView;

import java.util.List;
import java.util.UUID;

public interface ListarEventosCalendarPorEventoUseCase {

    List<EventoCalendarView> listarPorEvento(UUID eventoId);
}
