package com.ejemplo.monolitomodular.menus.dominio.puerto.salida;

import com.ejemplo.monolitomodular.menus.dominio.modelo.TipoMomentoMenu;

import java.util.Optional;
import java.util.UUID;

public interface TipoMomentoMenuRepository {

    Optional<TipoMomentoMenu> buscarPorId(UUID id);

    boolean existeActivoPorId(UUID id);
}
