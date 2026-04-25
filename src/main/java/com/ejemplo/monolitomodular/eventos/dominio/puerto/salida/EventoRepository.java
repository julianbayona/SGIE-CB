package com.ejemplo.monolitomodular.eventos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventoRepository {

    Evento guardar(Evento evento);

    Optional<Evento> buscarPorId(UUID id);

    List<Evento> listar();
}
