package com.ejemplo.monolitomodular.montajes.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.montajes.aplicacion.dto.ConfigurarMontajeCommand;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.MontajeView;

public interface ConfigurarMontajeUseCase {

    MontajeView ejecutar(ConfigurarMontajeCommand command);
}
