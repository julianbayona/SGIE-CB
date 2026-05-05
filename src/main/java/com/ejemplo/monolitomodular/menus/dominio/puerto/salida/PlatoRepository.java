package com.ejemplo.monolitomodular.menus.dominio.puerto.salida;

import com.ejemplo.monolitomodular.menus.dominio.modelo.Plato;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlatoRepository {

    default Plato guardar(Plato plato) {
        throw new UnsupportedOperationException("Operacion no implementada");
    }

    Optional<Plato> buscarPorId(UUID id);

    default List<Plato> listar() {
        throw new UnsupportedOperationException("Operacion no implementada");
    }

    default boolean existePorNombre(String nombre) {
        throw new UnsupportedOperationException("Operacion no implementada");
    }

    boolean existeActivoParaMomento(UUID platoId, UUID tipoMomentoId);
}
