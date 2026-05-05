package com.ejemplo.monolitomodular.auth.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.auth.aplicacion.dto.AuthResponse;
import com.ejemplo.monolitomodular.auth.aplicacion.dto.LoginCommand;

public interface LoginUseCase {

    AuthResponse login(LoginCommand command);
}
