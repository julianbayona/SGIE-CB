package com.ejemplo.monolitomodular.pagos.infraestructura.persistencia;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface EventoAnticipoPendienteProjection {

    UUID getEventoId();

    UUID getCotizacionId();

    String getNombreCliente();

    String getTelefonoCliente();

    String getCorreoCliente();

    LocalDateTime getFechaHoraInicio();

    BigDecimal getValorTotal();

    BigDecimal getTotalPagado();
}
