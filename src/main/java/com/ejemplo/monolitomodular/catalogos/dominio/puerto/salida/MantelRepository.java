package com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Mantel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MantelRepository {

    Mantel guardar(Mantel mantel);

    Optional<Mantel> buscarPorId(UUID id);

    List<Mantel> listar();

    boolean existeActivoPorId(UUID id);

    boolean existePorNombre(String nombre);
}
