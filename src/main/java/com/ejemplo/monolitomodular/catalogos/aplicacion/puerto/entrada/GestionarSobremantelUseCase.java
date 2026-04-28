package com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorView;

import java.util.List;
import java.util.UUID;

public interface GestionarSobremantelUseCase {

    CatalogoConColorView crearSobremantel(CatalogoConColorCommand command);

    CatalogoConColorView actualizarSobremantel(UUID id, CatalogoConColorCommand command);

    CatalogoConColorView desactivarSobremantel(UUID id);

    CatalogoConColorView obtenerSobremantel(UUID id);

    List<CatalogoConColorView> listarSobremanteles();
}
