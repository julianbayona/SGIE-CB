package com.ejemplo.monolitomodular.montajes.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Montaje {

    private final UUID id;
    private final UUID reservaId;
    private final String observaciones;
    private final List<MontajeMesaReserva> mesas;
    private final InfraestructuraReserva infraestructura;
    private final List<AdicionalEvento> adicionales;

    private Montaje(
            UUID id,
            UUID reservaId,
            String observaciones,
            List<MontajeMesaReserva> mesas,
            InfraestructuraReserva infraestructura,
            List<AdicionalEvento> adicionales
    ) {
        this.id = Objects.requireNonNull(id, "El id del montaje es obligatorio");
        this.reservaId = Objects.requireNonNull(reservaId, "La reserva del montaje es obligatoria");
        this.observaciones = normalizarObservaciones(observaciones);
        if (mesas == null || mesas.isEmpty()) {
            throw new DomainException("El montaje debe tener al menos una configuracion de mesas");
        }
        this.mesas = List.copyOf(mesas);
        this.infraestructura = Objects.requireNonNull(infraestructura, "La infraestructura del montaje es obligatoria");
        this.adicionales = adicionales == null ? List.of() : List.copyOf(adicionales);
    }

    public static Montaje configurar(
            UUID id,
            UUID reservaId,
            String observaciones,
            List<MontajeMesaReserva> mesas,
            InfraestructuraReserva infraestructura,
            List<AdicionalEvento> adicionales
    ) {
        return new Montaje(id, reservaId, observaciones, mesas, infraestructura, adicionales);
    }

    public static Montaje reconstruir(
            UUID id,
            UUID reservaId,
            String observaciones,
            List<MontajeMesaReserva> mesas,
            InfraestructuraReserva infraestructura,
            List<AdicionalEvento> adicionales
    ) {
        return new Montaje(id, reservaId, observaciones, mesas, infraestructura, adicionales);
    }

    public UUID getId() {
        return id;
    }

    public UUID getReservaId() {
        return reservaId;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public List<MontajeMesaReserva> getMesas() {
        return mesas;
    }

    public InfraestructuraReserva getInfraestructura() {
        return infraestructura;
    }

    public List<AdicionalEvento> getAdicionales() {
        return adicionales;
    }

    private static String normalizarObservaciones(String observaciones) {
        if (observaciones == null || observaciones.isBlank()) {
            return null;
        }
        return observaciones.trim();
    }
}
