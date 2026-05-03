package com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.pagos.aplicacion.dto.AnticipoView;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.RegistrarAnticipoCommand;

public interface RegistrarAnticipoUseCase {

    AnticipoView ejecutar(RegistrarAnticipoCommand command);
}
