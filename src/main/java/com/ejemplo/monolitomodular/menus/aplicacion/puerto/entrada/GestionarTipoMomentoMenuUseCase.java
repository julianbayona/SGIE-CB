package com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.TipoMomentoMenuCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.TipoMomentoMenuView;

import java.util.List;
import java.util.UUID;

public interface GestionarTipoMomentoMenuUseCase {

    TipoMomentoMenuView crear(TipoMomentoMenuCommand command);

    TipoMomentoMenuView actualizar(UUID id, TipoMomentoMenuCommand command);

    TipoMomentoMenuView desactivar(UUID id);

    TipoMomentoMenuView obtener(UUID id);

    List<TipoMomentoMenuView> listar();
}
