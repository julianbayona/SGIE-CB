package com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.ColorCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.ColorView;

import java.util.List;
import java.util.UUID;

public interface GestionarColorUseCase {

    ColorView crearColor(ColorCommand command);

    ColorView actualizarColor(UUID id, ColorCommand command);

    ColorView desactivarColor(UUID id);

    ColorView obtenerColor(UUID id);

    List<ColorView> listarColores();
}
