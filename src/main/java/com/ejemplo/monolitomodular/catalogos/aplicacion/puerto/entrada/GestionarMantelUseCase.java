package com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorView;

import java.util.List;
import java.util.UUID;

public interface GestionarMantelUseCase {

    CatalogoConColorView crearMantel(CatalogoConColorCommand command);

    CatalogoConColorView actualizarMantel(UUID id, CatalogoConColorCommand command);

    CatalogoConColorView desactivarMantel(UUID id);

    CatalogoConColorView obtenerMantel(UUID id);

    List<CatalogoConColorView> listarManteles();
}
