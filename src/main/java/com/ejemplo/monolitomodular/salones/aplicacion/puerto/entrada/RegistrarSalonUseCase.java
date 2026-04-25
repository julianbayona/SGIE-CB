package com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.salones.aplicacion.dto.RegistrarSalonCommand;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.SalonView;

public interface RegistrarSalonUseCase {

    SalonView ejecutar(RegistrarSalonCommand command);
}
