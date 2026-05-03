package com.ejemplo.monolitomodular.menus.aplicacion.dto;

import java.util.List;
import java.util.UUID;

public record SeleccionMenuCommand(
        UUID tipoMomentoId,
        List<ItemMenuCommand> items
) {
}
