package com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.ConfigurarMenuCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.MenuView;

public interface ConfigurarMenuUseCase {

    MenuView ejecutar(ConfigurarMenuCommand command);
}
