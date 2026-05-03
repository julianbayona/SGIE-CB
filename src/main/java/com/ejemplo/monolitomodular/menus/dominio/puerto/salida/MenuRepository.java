package com.ejemplo.monolitomodular.menus.dominio.puerto.salida;

import com.ejemplo.monolitomodular.menus.dominio.modelo.Menu;

import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {

    Menu guardar(Menu menu);

    Optional<Menu> buscarPorReservaId(UUID reservaId);
}
