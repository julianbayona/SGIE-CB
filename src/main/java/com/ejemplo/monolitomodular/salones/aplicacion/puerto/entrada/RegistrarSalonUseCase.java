package com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.salones.aplicacion.dto.RegistrarSalonCommand;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.SalonView;

import java.util.UUID;

public interface RegistrarSalonUseCase {

    SalonView ejecutar(RegistrarSalonCommand command);

    SalonView actualizar(UUID id, RegistrarSalonCommand command);

    SalonView activar(UUID id);

    SalonView desactivar(UUID id);
}
