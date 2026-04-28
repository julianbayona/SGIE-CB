package com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.TipoAdicionalCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.TipoAdicionalView;

import java.util.List;
import java.util.UUID;

public interface GestionarTipoAdicionalUseCase {

    TipoAdicionalView crearTipoAdicional(TipoAdicionalCommand command);

    TipoAdicionalView actualizarTipoAdicional(UUID id, TipoAdicionalCommand command);

    TipoAdicionalView desactivarTipoAdicional(UUID id);

    TipoAdicionalView obtenerTipoAdicional(UUID id);

    List<TipoAdicionalView> listarTiposAdicional();
}
