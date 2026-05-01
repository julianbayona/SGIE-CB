package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class PlatoMomentoJpaId implements Serializable {

    private UUID platoId;
    private UUID tipoMomentoId;

    public PlatoMomentoJpaId() {
    }

    public PlatoMomentoJpaId(UUID platoId, UUID tipoMomentoId) {
        this.platoId = platoId;
        this.tipoMomentoId = tipoMomentoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlatoMomentoJpaId that)) {
            return false;
        }
        return Objects.equals(platoId, that.platoId) && Objects.equals(tipoMomentoId, that.tipoMomentoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(platoId, tipoMomentoId);
    }
}
