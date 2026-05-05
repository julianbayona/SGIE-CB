package com.ejemplo.monolitomodular.menus.dominio.puerto.salida;

import com.ejemplo.monolitomodular.menus.dominio.modelo.TipoMomentoMenu;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoMomentoMenuRepository {

    default TipoMomentoMenu guardar(TipoMomentoMenu tipoMomentoMenu) {
        throw new UnsupportedOperationException("Operacion no implementada");
    }

    Optional<TipoMomentoMenu> buscarPorId(UUID id);

    default List<TipoMomentoMenu> listar() {
        throw new UnsupportedOperationException("Operacion no implementada");
    }

    boolean existeActivoPorId(UUID id);

    default boolean existePorNombre(String nombre) {
        throw new UnsupportedOperationException("Operacion no implementada");
    }
}
