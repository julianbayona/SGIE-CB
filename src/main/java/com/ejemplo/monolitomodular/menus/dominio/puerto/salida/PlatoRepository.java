package com.ejemplo.monolitomodular.menus.dominio.puerto.salida;

import com.ejemplo.monolitomodular.menus.dominio.modelo.Plato;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlatoRepository {

    Optional<Plato> buscarPorId(UUID id);

    boolean existeActivoParaMomento(UUID platoId, UUID tipoMomentoId);

    List<Plato> listarActivos();
}
