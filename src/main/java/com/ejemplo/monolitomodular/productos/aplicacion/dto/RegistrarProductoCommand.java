package com.ejemplo.monolitomodular.productos.aplicacion.dto;

import java.math.BigDecimal;

public record RegistrarProductoCommand(String nombre, BigDecimal precio) {
}
