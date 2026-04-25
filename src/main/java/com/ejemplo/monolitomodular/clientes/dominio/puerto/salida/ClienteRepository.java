package com.ejemplo.monolitomodular.clientes.dominio.puerto.salida;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository {

    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(UUID id);

    Optional<Cliente> buscarPorCedula(String cedula);

    List<Cliente> listar();

    List<Cliente> buscarPorFiltro(String filtro);
}
