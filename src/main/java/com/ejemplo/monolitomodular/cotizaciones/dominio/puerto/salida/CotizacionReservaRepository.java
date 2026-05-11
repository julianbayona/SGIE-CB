package com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida;

import java.util.Collection;
import java.util.UUID;

public interface CotizacionReservaRepository {

    void asociarReservas(UUID cotizacionId, Collection<UUID> reservaIds);
}
