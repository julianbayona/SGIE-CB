package com.ejemplo.monolitomodular.usuarios.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.usuarios.aplicacion.dto.CrearUsuarioCommand;
import com.ejemplo.monolitomodular.usuarios.aplicacion.dto.UsuarioView;

import java.util.List;
import java.util.UUID;

public interface GestionarUsuarioUseCase {

    UsuarioView crear(CrearUsuarioCommand command);

    List<UsuarioView> listar();

    UsuarioView activar(UUID id);

    UsuarioView desactivar(UUID id);
}
