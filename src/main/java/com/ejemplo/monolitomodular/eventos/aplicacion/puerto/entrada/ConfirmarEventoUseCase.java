package com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;

import java.util.UUID;

public interface ConfirmarEventoUseCase {

    EventoView confirmar(UUID eventoId, UUID usuarioId);
}
