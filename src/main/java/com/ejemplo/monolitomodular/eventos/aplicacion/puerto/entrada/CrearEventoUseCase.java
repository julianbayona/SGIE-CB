package com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearEventoCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;

public interface CrearEventoUseCase {

    EventoView ejecutar(CrearEventoCommand command);
}
