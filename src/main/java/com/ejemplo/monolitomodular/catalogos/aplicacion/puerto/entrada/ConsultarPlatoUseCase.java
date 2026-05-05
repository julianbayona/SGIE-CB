package com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.PlatoView;

import java.util.List;
import java.util.UUID;

public interface ConsultarPlatoUseCase {
    List<PlatoView> listarActivos();
    PlatoView obtenerPorId(UUID id);
}
