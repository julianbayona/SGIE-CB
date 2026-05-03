package com.ejemplo.monolitomodular.menus.aplicacion.dto;

import java.util.List;
import java.util.UUID;

public record SeleccionMenuView(
        UUID id,
        UUID tipoMomentoId,
        List<ItemMenuView> items
) {
}
