package com.ejemplo.monolitomodular.clientes.presentacion.rest.dto;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;

import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String cedula,
        String nombreCompleto,
        String telefono,
        String correo,
        TipoCliente tipoCliente,
        boolean activo,
        UUID creadoPor
) {
}
