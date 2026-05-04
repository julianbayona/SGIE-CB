package com.ejemplo.monolitomodular.pruebasplato.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.pruebasplato.aplicacion.dto.ProgramarPruebaPlatoCommand;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.dto.PruebaPlatoView;

public interface ProgramarPruebaPlatoUseCase {

    PruebaPlatoView ejecutar(ProgramarPruebaPlatoCommand command);
}
