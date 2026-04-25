package com.ejemplo.monolitomodular.productos.presentacion.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductoResponse(UUID id, String nombre, BigDecimal precio) {
}
