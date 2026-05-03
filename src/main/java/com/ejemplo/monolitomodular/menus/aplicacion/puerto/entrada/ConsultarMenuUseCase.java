package com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.MenuView;

import java.util.UUID;

public interface ConsultarMenuUseCase {

    MenuView obtenerPorReservaRaizId(UUID reservaRaizId);
}
