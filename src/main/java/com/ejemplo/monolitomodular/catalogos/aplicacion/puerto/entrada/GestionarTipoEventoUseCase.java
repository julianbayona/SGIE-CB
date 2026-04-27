package com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoView;

import java.util.List;
import java.util.UUID;

public interface GestionarTipoEventoUseCase {

    CatalogoBasicoView crearTipoEvento(CatalogoBasicoCommand command);

    CatalogoBasicoView actualizarTipoEvento(UUID id, CatalogoBasicoCommand command);

    CatalogoBasicoView desactivarTipoEvento(UUID id);

    CatalogoBasicoView obtenerTipoEvento(UUID id);

    List<CatalogoBasicoView> listarTiposEvento();
}
