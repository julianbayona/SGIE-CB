package com.ejemplo.monolitomodular.clientes.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.clientes.aplicacion.dto.ClienteView;
import com.ejemplo.monolitomodular.clientes.aplicacion.dto.RegistrarClienteCommand;

public interface RegistrarClienteUseCase {

    ClienteView ejecutar(RegistrarClienteCommand command);
}
