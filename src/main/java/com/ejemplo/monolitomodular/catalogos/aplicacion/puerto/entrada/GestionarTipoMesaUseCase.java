package com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoView;

import java.util.List;
import java.util.UUID;

public interface GestionarTipoMesaUseCase {

    CatalogoBasicoView crearTipoMesa(CatalogoBasicoCommand command);

    CatalogoBasicoView actualizarTipoMesa(UUID id, CatalogoBasicoCommand command);

    CatalogoBasicoView desactivarTipoMesa(UUID id);

    CatalogoBasicoView obtenerTipoMesa(UUID id);

    List<CatalogoBasicoView> listarTiposMesa();
}
