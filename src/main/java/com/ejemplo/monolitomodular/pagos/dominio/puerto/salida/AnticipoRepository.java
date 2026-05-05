package com.ejemplo.monolitomodular.pagos.dominio.puerto.salida;

import com.ejemplo.monolitomodular.pagos.dominio.modelo.Anticipo;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.EventoAnticipoPendiente;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AnticipoRepository {

    Anticipo guardar(Anticipo anticipo);

    List<Anticipo> listarPorCotizacionId(UUID cotizacionId);

    BigDecimal totalPorCotizacionId(UUID cotizacionId);

    BigDecimal totalPorEventoId(UUID eventoId);

    List<EventoAnticipoPendiente> buscarEventosConAnticipoPendiente(LocalDateTime desde, LocalDateTime hasta, int limite);
}
