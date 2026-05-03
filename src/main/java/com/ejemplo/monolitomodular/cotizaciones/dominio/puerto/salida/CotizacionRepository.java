package com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida;

import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;

import java.util.Optional;
import java.util.UUID;

public interface CotizacionRepository {

    Cotizacion guardar(Cotizacion cotizacion);

    Optional<Cotizacion> buscarPorId(UUID id);

    Optional<Cotizacion> buscarActivaPorReservaId(UUID reservaId);

    Optional<Cotizacion> buscarUltimaPorReservaRaizId(UUID reservaRaizId);

    void desactualizarActivasPorReservaId(UUID reservaId);
}
