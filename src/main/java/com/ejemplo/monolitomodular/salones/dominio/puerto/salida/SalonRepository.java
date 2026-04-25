package com.ejemplo.monolitomodular.salones.dominio.puerto.salida;

import com.ejemplo.monolitomodular.salones.dominio.modelo.Salon;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalonRepository {

    Salon guardar(Salon salon);

    Optional<Salon> buscarPorId(UUID id);

    List<Salon> listar();

    List<Salon> buscarTodosPorIds(Collection<UUID> ids);

    boolean existePorNombre(String nombre);
}
