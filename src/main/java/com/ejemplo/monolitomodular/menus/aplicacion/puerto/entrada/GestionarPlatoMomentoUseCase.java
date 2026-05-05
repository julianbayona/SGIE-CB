package com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.PlatoMomentoCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.PlatoMomentoView;

import java.util.List;
import java.util.UUID;

public interface GestionarPlatoMomentoUseCase {

    PlatoMomentoView asociar(PlatoMomentoCommand command);

    void eliminar(UUID platoId, UUID tipoMomentoId);

    List<PlatoMomentoView> listar(UUID platoId, UUID tipoMomentoId);
}
