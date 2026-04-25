package com.ejemplo.monolitomodular.salones.presentacion.rest.dto;

import java.util.UUID;

public record SalonResponse(UUID id, String nombre, int capacidad, String descripcion, boolean activo) {
}
