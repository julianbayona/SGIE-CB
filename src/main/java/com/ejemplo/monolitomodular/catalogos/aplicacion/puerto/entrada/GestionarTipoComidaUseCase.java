package com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoView;

import java.util.List;
import java.util.UUID;

public interface GestionarTipoComidaUseCase {

    CatalogoBasicoView crearTipoComida(CatalogoBasicoCommand command);

    CatalogoBasicoView actualizarTipoComida(UUID id, CatalogoBasicoCommand command);

    CatalogoBasicoView desactivarTipoComida(UUID id);

    CatalogoBasicoView obtenerTipoComida(UUID id);

    List<CatalogoBasicoView> listarTiposComida();
}
