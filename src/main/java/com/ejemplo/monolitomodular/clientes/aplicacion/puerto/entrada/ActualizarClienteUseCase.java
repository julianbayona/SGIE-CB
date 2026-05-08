package com.ejemplo.monolitomodular.clientes.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.clientes.aplicacion.dto.ActualizarClienteCommand;
import com.ejemplo.monolitomodular.clientes.aplicacion.dto.ClienteView;

public interface ActualizarClienteUseCase {

    ClienteView ejecutar(ActualizarClienteCommand command);
}
