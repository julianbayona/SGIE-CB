package com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto;

public record DocumentoCotizacionView(
        String nombreArchivo,
        String contentType,
        byte[] contenido
) {
}
