package com.ejemplo.monolitomodular.productos.aplicacion.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductoView(UUID id, String nombre, BigDecimal precio) {
}
