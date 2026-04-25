package com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;

import java.util.List;
import java.util.UUID;

public interface ConsultarEventoUseCase {

    EventoView obtenerPorId(UUID id);

    List<EventoView> listar();
}
