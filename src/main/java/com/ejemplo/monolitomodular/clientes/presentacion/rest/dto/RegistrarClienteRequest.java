package com.ejemplo.monolitomodular.clientes.presentacion.rest.dto;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrarClienteRequest(
        @NotBlank(message = "La cedula es obligatoria")
        String cedula,
        @NotBlank(message = "El nombre completo es obligatorio")
        String nombreCompleto,
        @NotBlank(message = "El telefono es obligatorio")
        String telefono,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe tener un formato valido")
        String correo,
        @NotNull(message = "El tipo de cliente es obligatorio")
        TipoCliente tipoCliente
) {
}
