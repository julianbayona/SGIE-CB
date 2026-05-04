package com.ejemplo.monolitomodular.pruebasplato.aplicacion.evento;

import java.time.LocalDateTime;
import java.util.UUID;

public record PruebaPlatoProgramadaEvent(
        UUID pruebaPlatoId,
        UUID eventoId,
        UUID clienteId,
        String nombreCliente,
        String telefonoCliente,
        LocalDateTime fechaRealizacion
) {
}
