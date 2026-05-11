package com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;

import java.util.UUID;

public interface RetirarReservaSalonUseCase {

    EventoView retirar(UUID reservaRaizId, UUID usuarioId);
}
