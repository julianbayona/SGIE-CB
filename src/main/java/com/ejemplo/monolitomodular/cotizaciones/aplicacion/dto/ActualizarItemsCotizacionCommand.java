package com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ActualizarItemsCotizacionCommand(
        UUID cotizacionId,
        List<Item> items
) {

    public record Item(
            UUID itemId,
            BigDecimal precioOverride
    ) {
    }
}
