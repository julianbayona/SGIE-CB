package com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearReservaSalonCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;

import java.util.UUID;

public interface CrearReservaSalonUseCase {

    EventoView ejecutar(UUID eventoId, CrearReservaSalonCommand command);
}
