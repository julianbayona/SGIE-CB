package com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CotizacionReservaRepository {

    void asociarReservas(UUID cotizacionId, Collection<UUID> reservaIds);

    default List<UUID> listarReservaIdsPorCotizacionId(UUID cotizacionId) {
        return List.of();
    }
}
