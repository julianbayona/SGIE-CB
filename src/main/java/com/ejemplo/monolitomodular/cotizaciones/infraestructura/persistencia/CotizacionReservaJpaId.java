package com.ejemplo.monolitomodular.cotizaciones.infraestructura.persistencia;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class CotizacionReservaJpaId implements Serializable {

    private UUID cotizacionId;
    private UUID reservaId;

    public CotizacionReservaJpaId() {
    }

    public CotizacionReservaJpaId(UUID cotizacionId, UUID reservaId) {
        this.cotizacionId = cotizacionId;
        this.reservaId = reservaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CotizacionReservaJpaId that)) {
            return false;
        }
        return Objects.equals(cotizacionId, that.cotizacionId) && Objects.equals(reservaId, that.reservaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cotizacionId, reservaId);
    }
}
