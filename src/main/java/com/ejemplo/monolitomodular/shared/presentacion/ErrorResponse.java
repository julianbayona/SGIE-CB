package com.ejemplo.monolitomodular.shared.presentacion;

import java.time.Instant;

public record ErrorResponse(String mensaje, Instant timestamp) {
}
