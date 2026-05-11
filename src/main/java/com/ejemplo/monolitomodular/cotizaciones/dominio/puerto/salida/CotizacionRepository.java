package com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida;

import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CotizacionRepository {

    Cotizacion guardar(Cotizacion cotizacion);

    Optional<Cotizacion> buscarPorId(UUID id);

    Optional<Cotizacion> buscarActivaPorReservaId(UUID reservaId);

    default Optional<Cotizacion> buscarActivaPorEventoId(UUID eventoId) {
        return Optional.empty();
    }

    Optional<Cotizacion> buscarUltimaPorReservaRaizId(UUID reservaRaizId);

    Optional<Cotizacion> buscarAceptadaVigentePorEventoId(UUID eventoId);

    List<Cotizacion> listarPorEventoId(UUID eventoId);

    void desactualizarActivasPorReservaId(UUID reservaId);

    default void desactualizarActivasPorEventoId(UUID eventoId) {
    }
}
