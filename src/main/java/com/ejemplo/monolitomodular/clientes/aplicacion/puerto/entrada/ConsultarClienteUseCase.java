package com.ejemplo.monolitomodular.clientes.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.clientes.aplicacion.dto.ClienteView;

import java.util.List;
import java.util.UUID;

public interface ConsultarClienteUseCase {

    ClienteView obtenerPorId(UUID id);

    List<ClienteView> listar();

    List<ClienteView> buscar(String filtro);
}
