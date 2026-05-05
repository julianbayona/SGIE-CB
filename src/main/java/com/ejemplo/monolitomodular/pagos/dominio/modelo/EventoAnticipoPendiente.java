package com.ejemplo.monolitomodular.pagos.dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record EventoAnticipoPendiente(
        UUID eventoId,
        UUID cotizacionId,
        String nombreCliente,
        String telefonoCliente,
        String correoCliente,
        LocalDateTime fechaHoraInicio,
        BigDecimal valorTotal,
        BigDecimal totalPagado
) {

    public BigDecimal saldoPendiente() {
        return valorTotal.subtract(totalPagado);
    }
}
