package com.ejemplo.monolitomodular.auth.presentacion.rest;

import com.ejemplo.monolitomodular.auth.aplicacion.dto.AuthResponse;
import com.ejemplo.monolitomodular.auth.aplicacion.dto.LoginCommand;
import com.ejemplo.monolitomodular.auth.aplicacion.dto.UsuarioAutenticadoView;
import com.ejemplo.monolitomodular.auth.aplicacion.puerto.entrada.LoginUseCase;
import com.ejemplo.monolitomodular.auth.infraestructura.seguridad.UsuarioAutenticado;
import com.ejemplo.monolitomodular.auth.presentacion.rest.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return loginUseCase.login(new LoginCommand(request.nombre(), request.contrasena()));
    }

    @GetMapping("/me")
    public UsuarioAutenticadoView me(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return new UsuarioAutenticadoView(usuario.id(), usuario.nombre(), usuario.rol(), usuario.expiraEn());
    }
}
