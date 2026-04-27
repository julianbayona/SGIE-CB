package com.ejemplo.monolitomodular.clientes.aplicacion.dto;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;

import java.util.UUID;

public record RegistrarClienteCommand(
        String cedula,
        String nombreCompleto,
        String telefono,
        String correo,
        TipoCliente tipoCliente,
        UUID creadoPor
) {
}
