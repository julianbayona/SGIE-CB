package com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CancelarEventoCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;

public interface CancelarEventoUseCase {

    EventoView cancelar(CancelarEventoCommand command);
}
