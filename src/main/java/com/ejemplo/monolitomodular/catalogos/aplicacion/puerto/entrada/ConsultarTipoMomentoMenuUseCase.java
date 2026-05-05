package com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.TipoMomentoMenuView;

import java.util.List;
import java.util.UUID;

public interface ConsultarTipoMomentoMenuUseCase {
    List<TipoMomentoMenuView> listarActivos();
    TipoMomentoMenuView obtenerPorId(UUID id);
}
