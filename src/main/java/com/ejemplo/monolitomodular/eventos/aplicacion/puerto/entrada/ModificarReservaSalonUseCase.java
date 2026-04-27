package com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.ModificarReservaSalonCommand;

import java.util.UUID;

public interface ModificarReservaSalonUseCase {

    EventoView ejecutar(UUID reservaRaizId, ModificarReservaSalonCommand command);
}
