package com.ejemplo.monolitomodular.salones.aplicacion.dto;

import java.util.UUID;

public record SalonView(UUID id, String nombre, int capacidad, String descripcion, boolean activo) {
}
