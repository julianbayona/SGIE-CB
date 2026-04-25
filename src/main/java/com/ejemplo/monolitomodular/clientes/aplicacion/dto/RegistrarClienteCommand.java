package com.ejemplo.monolitomodular.clientes.aplicacion.dto;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;

public record RegistrarClienteCommand(
        String cedula,
        String nombre,
        String telefono,
        String correo,
        TipoCliente tipoCliente
) {
}
