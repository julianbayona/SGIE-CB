package com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoView;

import java.util.List;
import java.util.UUID;

public interface GestionarTipoSillaUseCase {

    CatalogoBasicoView crearTipoSilla(CatalogoBasicoCommand command);

    CatalogoBasicoView actualizarTipoSilla(UUID id, CatalogoBasicoCommand command);

    CatalogoBasicoView desactivarTipoSilla(UUID id);

    CatalogoBasicoView obtenerTipoSilla(UUID id);

    List<CatalogoBasicoView> listarTiposSilla();
}
