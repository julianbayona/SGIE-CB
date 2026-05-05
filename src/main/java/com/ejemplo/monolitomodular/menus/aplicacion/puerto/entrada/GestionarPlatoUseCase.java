package com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.PlatoCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.PlatoView;

import java.util.List;
import java.util.UUID;

public interface GestionarPlatoUseCase {

    PlatoView crear(PlatoCommand command);

    PlatoView actualizar(UUID id, PlatoCommand command);

    PlatoView desactivar(UUID id);

    PlatoView obtener(UUID id);

    List<PlatoView> listar();
}
